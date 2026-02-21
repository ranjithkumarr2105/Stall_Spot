<?php
header('Content-Type: application/json');
include 'config.php';
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

// Use $_REQUEST to allow testing with GET in browser
$stall_id = $_REQUEST['stall_id'] ?? '';
$date = $_REQUEST['date'] ?? '';

if (empty($stall_id)) {
    http_response_code(400);
    die(json_encode(["error" => "Stall ID is required."]));
}
if (empty($date)) {
    http_response_code(400);
    die(json_encode(["error" => "Date is required."]));
}

try {
    // [FIX] Replaced JSON_ARRAYAGG and JSON_OBJECT with compatible CONCAT functions
    $sql = "
        SELECT 
            o.order_id,
            o.stall_id,
            o.student_id,
            o.total_amount,
            o.subtotal,
            o.parcel_fee,
            o.order_status,
            o.order_date,
            o.payment_method,
            o.parcel_type,
            o.payment_id,
            o.pickup_time,
            o.display_order_id,
            o.refund_timestamp, -- This line is correct
            
            -- This is the new, backward-compatible way to build the JSON array
            CONCAT('[', 
                COALESCE(
                    (SELECT GROUP_CONCAT(
                        CONCAT(
                            '{\"name\":\"', REPLACE(REPLACE(oi.item_name, '\\\\', '\\\\\\\\'), '\"', '\\\"'), '\",',
                            '\"quantity\":', oi.quantity, ',',
                            '\"price\":', oi.price, ',',
                            '\"parcel_status\":\"', REPLACE(REPLACE(oi.parcel_status, '\\\\', '\\\\\\\\'), '\"', '\\\"'), '\"}'
                        )
                    SEPARATOR ',')
                    FROM order_items oi
                    WHERE oi.order_id = o.order_id
                    ), 
                '')
            , ']') as items_json

        FROM orders o
        WHERE 
            o.stall_id = ? 
            AND DATE(o.order_date) = ?
            AND o.order_status IN ('Delivered', 'Rejected') -- This line is correct
        ORDER BY 
            o.order_date DESC
    ";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $stall_id, $date);
    $stmt->execute();
    $result = $stmt->get_result();
    $orders = $result->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
    
    echo json_encode($orders); // Send the successful result

} catch (Exception $e) { 
    http_response_code(500);
    // Log the error for your own debugging
    error_log("O_get_order_history.php Error: " . $e->getMessage()); 
    echo json_encode(["error" => "An internal server error occurred.", "message" => $e->getMessage()]); 
}

$conn->close();
?>