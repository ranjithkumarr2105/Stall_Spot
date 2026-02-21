<?php
header('Content-Type: application/json');
require 'vendor/autoload.php'; 
include 'config.php';

// Use your actual Razorpay keys
$keyId = 'rzp_test_APuQCp0MiHoD9M';
$keySecret = '06kTw2BRDXPQ3FUuhBZTrPXZ';

use Razorpay\Api\Api;
$api = new Api($keyId, $keySecret);

$invoice_id = $_POST['invoice_id'] ?? 0;
$stall_id = $_POST['stall_id'] ?? '';

if ($invoice_id == 0 || empty($stall_id)) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invoice ID and Stall ID are required.']);
    exit;
}

// [SECURITY FIX] Fetch the REAL amounts from the database
// We do NOT trust the 'rent_amount' sent from the app.
$stmt = $conn->prepare("SELECT rent_amount, late_fee FROM rent_invoices WHERE invoice_id = ? AND stall_id = ? AND status = 'unpaid'");
$stmt->bind_param("is", $invoice_id, $stall_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid Invoice ID or Invoice already paid.']);
    exit;
}

$invoice = $result->fetch_assoc();
$stmt->close();

// Calculate Total Payable: Rent + Late Fee
$real_rent_amount = (float)$invoice['rent_amount'];
$real_late_fee = (float)$invoice['late_fee'];
$total_payable = $real_rent_amount + $real_late_fee;

if ($total_payable <= 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid payable amount.']);
    exit;
}

// Amount must be in paise (1 INR = 100 paise)
$amountInPaise = $total_payable * 100;

$orderData = [
    'receipt'         => 'rent_invoice_' . $invoice_id,
    'amount'          => $amountInPaise,
    'currency'        => 'INR',
    'payment_capture' => 1, // Automatically capture the payment
    'notes'           => [
        'invoice_id' => $invoice_id,
        'stall_id'   => $stall_id,
        'type'       => 'rent_payment',
        'base_rent'  => $real_rent_amount,
        'late_fee'   => $real_late_fee
    ]
];

try {
    $razorpayOrder = $api->order->create($orderData);
    $razorpayOrderId = $razorpayOrder['id'];
    
    echo json_encode([
        'status' => 'success',
        'order_id' => $razorpayOrderId,
        'amount' => $amountInPaise, // This forces the Android app to use THIS amount
        'key_id' => $keyId,
        'debug_info' => 'Late fee applied automatically if applicable'
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()]);
}
?>