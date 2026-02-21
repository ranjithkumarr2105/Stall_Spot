<?php
header('Content-Type: application/json');
include 'config.php';
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

// [MODIFIED] Check $_POST first, then raw JSON input
$postData = $_POST;
if (empty($postData)) {
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true);
    if (json_last_error() === JSON_ERROR_NONE && is_array($jsonData)) {
        $postData = $jsonData;
    }
}

$student_id = $postData['student_id'] ?? '';
// --- End modification ---

if (empty($student_id)) {
    http_response_code(400);
    // Send empty array as per original logic
    die(json_encode([])); 
}

try {
    // --- [THE FIX] ---
    // Replaced JSON_ARRAYAGG and JSON_OBJECT with manual CONCAT
    $sql = "
        SELECT 
            o.*,
            sd.stallname,
            
            -- Manually build the JSON array string for items_json
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
        JOIN stalldetails sd ON o.stall_id = sd.stall_id
        WHERE o.student_id = ?
        ORDER BY o.order_date DESC
    ";
    // --- [END FIX] ---
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $student_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $orders = $result->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
    
    echo json_encode($orders);

} catch (Exception $e) { 
    http_response_code(500); 
    // Log the error for your own debugging
    error_log("U_get_order_history.php Error: " . $e->getMessage()); 
    echo json_encode(["error" => "An internal server error occurred.", "message" => $e->getMessage()]); 
}

$conn->close();
?>