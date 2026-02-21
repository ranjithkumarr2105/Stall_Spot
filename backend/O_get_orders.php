<?php
header('Content-Type: application/json');
include 'config.php'; // Your database connection
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

// Use $_REQUEST to accept data from both POST (your app) and GET (browser testing)
$stall_id = $_REQUEST['stall_id'] ?? '';
$filter = $_REQUEST['filter'] ?? '';

// --- START OF NEW, MORE ROBUST ERROR CHECKING ---
if (empty($stall_id)) {
    http_response_code(400); // Bad Request
    die(json_encode(["status" => "error", "message" => "Stall ID is missing."]));
}
if (empty($filter)) {
    http_response_code(400); // Bad Request
    die(json_encode(["status" => "error", "message" => "Filter is missing."]));
}
// --- END OF NEW ERROR CHECKING ---

try {
    $status_condition = "";
    $date_condition = ""; 

    if ($filter == 'approved') { 
        $status_condition = "o.order_status = 'Delivered'";
        $date_condition = "AND DATE(o.order_date) = CURDATE()";
    } 
    elseif ($filter == 'rejected') { 
        $status_condition = "o.order_status = 'Rejected'"; 
        $date_condition = "AND DATE(o.order_date) = CURDATE()";
    }
    elseif ($filter == 'pending') { 
        $status_condition = "o.order_status = 'Pending'";
        $date_condition = "AND DATE(o.order_date) = CURDATE()"; 
    }
    else { 
        throw new Exception("Invalid filter provided."); 
    }

    // --- [THIS IS THE FIX] ---
    // This query uses manual CONCAT to build the JSON string
    // It does NOT use JSON_ARRAYAGG or JSON_OBJECT
    $sql = "
        SELECT 
            o.display_order_id, o.student_id, o.total_amount, o.subtotal, o.parcel_fee, o.order_status, 
            o.parcel_type, TIME_FORMAT(o.pickup_time, '%l:%i %p') as pickup_time,
            DATE_FORMAT(o.order_date, '%d %b, %h:%i %p') as order_date, 
            
            COALESCE(GROUP_CONCAT(DISTINCT CONCAT(oi.item_name, ' × ', oi.quantity) SEPARATOR '\n'), 'No items found') as items_summary,
            
            -- Manually build the JSON array string
            CONCAT('[', 
                COALESCE(
                    (SELECT GROUP_CONCAT(
                        CONCAT(
                            '{\"name\":\"', REPLACE(REPLACE(item_name, '\\\\', '\\\\\\\\'), '\"', '\\\"'), '\",', -- Escape quotes and backslashes
                            '\"quantity\":', quantity, ',',
                            '\"price\":', price, ',',
                            '\"parcel_status\":\"', REPLACE(REPLACE(parcel_status, '\\\\', '\\\\\\\\'), '\"', '\\\"'), '\"}'
                        )
                    SEPARATOR ',')
                    FROM order_items 
                    WHERE order_id = o.order_id
                    ), 
                '')
            , ']') as items_json
            
        FROM orders o
        LEFT JOIN order_items oi ON o.order_id = oi.order_id
        WHERE o.stall_id = ? AND $status_condition $date_condition
        GROUP BY o.order_id
        ORDER BY o.order_date ASC
    ";
    // --- [END OF FIX] ---
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $stall_id);
    $stmt->execute();
    $orders = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
    
    echo json_encode(['status' => 'success', 'orders' => $orders]);

} catch (Exception $e) { 
    http_response_code(500); 
    // Log the error to your server log for debugging
    error_log("O_get_orders.php Error: " . $e->getMessage()); 
    echo json_encode(["status" => "error", "message" => $e->getMessage()]); 
}
$conn->close();
?>