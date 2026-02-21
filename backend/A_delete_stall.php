<?php
header('Content-Type: application/json');
include 'config.php';
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$stall_id = $_POST['stall_id'] ?? '';

if (empty($stall_id)) {
    http_response_code(400);
    die(json_encode(['status' => 'error', 'message' => 'Stall ID is required.']));
}

try {
    // 1. Delete Order Items (Grandchildren of Stall -> Child of Orders)
    // We must find all orders belonging to this stall first
    $stmt_items = $conn->prepare("DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE stall_id = ?)");
    $stmt_items->bind_param("s", $stall_id);
    $stmt_items->execute();
    $stmt_items->close();

    // 2. Delete Orders (Direct Child of Stall)
    $stmt_orders = $conn->prepare("DELETE FROM orders WHERE stall_id = ?");
    $stmt_orders->bind_param("s", $stall_id);
    $stmt_orders->execute();
    $stmt_orders->close();

    // 3. Delete Menu Items
    $stmt_menu = $conn->prepare("DELETE FROM menu_items WHERE stall_id = ?");
    $stmt_menu->bind_param("s", $stall_id);
    $stmt_menu->execute();
    $stmt_menu->close();

    // 4. Delete Favorites
    $stmt_fav = $conn->prepare("DELETE FROM favorite_stalls WHERE stall_id = ?");
    $stmt_fav->bind_param("s", $stall_id);
    $stmt_fav->execute();
    $stmt_fav->close();

    // 5. Delete Rent Invoices
    $stmt_rent = $conn->prepare("DELETE FROM rent_invoices WHERE stall_id = ?");
    $stmt_rent->bind_param("s", $stall_id);
    $stmt_rent->execute();
    $stmt_rent->close();

    // 6. Delete Reviews (Owner & Student)
    $stmt_orev = $conn->prepare("DELETE FROM owner_reviews WHERE stall_id = ?");
    $stmt_orev->bind_param("s", $stall_id);
    $stmt_orev->execute();
    $stmt_orev->close();

    $stmt_srev = $conn->prepare("DELETE FROM student_reviews WHERE stall_id = ?");
    $stmt_srev->bind_param("s", $stall_id);
    $stmt_srev->execute();
    $stmt_srev->close();

    // 7. FINALLY, Delete the Stall itself (Parent)
    $stmt = $conn->prepare("DELETE FROM stalldetails WHERE stall_id = ?");
    $stmt->bind_param("s", $stall_id);
    
    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Stall and all related records permanently deleted.']);
        } else {
            // It's possible the stall ID doesn't exist, but we successfully cleaned up nothing.
            // Or the stall was already deleted.
            echo json_encode(['status' => 'error', 'message' => 'Stall not found or already deleted.']);
        }
    } else {
        throw new Exception("Database delete operation failed.");
    }
    $stmt->close();

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Server Error: ' . $e->getMessage()]);
}

$conn->close();
?>