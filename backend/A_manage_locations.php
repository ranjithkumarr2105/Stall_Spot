<?php
header('Content-Type: application/json');
include 'config.php';
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

// [NEW] Robust way to get POST data
$postData = $_POST; // Try standard form-data first

if (empty($postData)) {
    // If $_POST is empty, try reading raw JSON input
    $rawInput = file_get_contents('php://input');
    $jsonData = json_decode($rawInput, true); // Decode as an associative array
    
    if (json_last_error() === JSON_ERROR_NONE && is_array($jsonData)) {
        $postData = $jsonData; // Use the decoded JSON data
    } else {
         parse_str($rawInput, $parsedData);
         if (!empty($parsedData)) {
             $postData = $parsedData;
         }
    }
}

$action = $postData['action'] ?? '';
if (empty($action)) { 
    error_log("A_manage_locations.php: 'action' not specified. Raw input: " . file_get_contents('php://input'));
    die(json_encode(["status" => "error", "message" => "Action not specified."])); 
}

try {
    switch ($action) {
        case 'get_locations':
            handle_get_locations($conn);
            break;
        case 'set_location':
            handle_set_location($conn, $postData);
            break;
        case 'delete_location':
            handle_delete_location($conn, $postData);
            break;
        default:
            throw new Exception("Invalid action.");
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => $e->getMessage()]);
}

function handle_get_locations($conn) {
    // This function doesn't use POST data, so it's unchanged
    $stmt = $conn->prepare("SELECT stall_id, stallname, latitude, longitude FROM stalldetails WHERE approval = 1 AND latitude IS NOT NULL AND longitude IS NOT NULL ORDER BY stallname ASC");
    $stmt->execute();
    $locations = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
    $stmt->close();
    echo json_encode(["status" => "success", "locations" => $locations]);
}

function handle_set_location($conn, $postData) {
    // [FIX] Trim whitespace from input
    $stall_id = trim($postData['stall_id'] ?? '');
    $latitude = trim($postData['latitude'] ?? '');
    $longitude = trim($postData['longitude'] ?? '');


    if (empty($stall_id) || empty($latitude) || empty($longitude)) {
        throw new Exception("Stall ID, latitude, and longitude are required.");
    }

    // [FIX] Use TRIM() in the SQL query
    $stmt = $conn->prepare("UPDATE stalldetails SET latitude = ?, longitude = ? WHERE TRIM(stall_id) = ? AND approval = 1");
    $stmt->bind_param("dds", $latitude, $longitude, $stall_id);
    $stmt->execute();

    if ($stmt->affected_rows > 0) {
        echo json_encode(["status" => "success", "message" => "Location updated successfully for stall " . $stall_id]);
    } else {
        // [FIX] Use TRIM() in the verification query
        $checkStmt = $conn->prepare("SELECT stall_id FROM stalldetails WHERE TRIM(stall_id) = ? AND approval = 1");
        $checkStmt->bind_param("s", $stall_id);
        $checkStmt->execute();
        $result = $checkStmt->get_result();
        if ($result->num_rows == 0) {
            throw new Exception("Stall ID not found or is not an approved stall.");
        } else {
             echo json_encode(["status" => "success", "message" => "Location is already set to this value."]);
        }
        $checkStmt->close();
    }
    $stmt->close();
}

function handle_delete_location($conn, $postData) {
    // [FIX] Trim whitespace from input
    $stall_id = trim($postData['stall_id'] ?? '');
    if (empty($stall_id)) {
        throw new Exception("Stall ID is required.");
    }
    
    // [FIX] Use TRIM() in the SQL query
    $stmt = $conn->prepare("UPDATE stalldetails SET latitude = NULL, longitude = NULL WHERE TRIM(stall_id) = ?");
    $stmt->bind_param("s", $stall_id);
    $stmt->execute();

    if ($stmt->affected_rows > 0) {
        echo json_encode(["status" => "success", "message" => "Location for " . $stall_id . " has been deleted."]);
    } else {
        throw new Exception("Stall ID not found or location was already null.");
    }
    $stmt->close();
}

$conn->close();
?>