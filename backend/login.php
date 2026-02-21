<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
// Logging setup
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/php_error.log'); 
// --- End logging setup ---
header('Content-Type: application/json');
include 'config.php';

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

$response = [];
$identifier = $_POST['identifier'] ?? '';
$password = $_POST['password'] ?? '';

error_log("--- New login.php request ---"); 
error_log("Identifier received: " . $identifier); 

if (empty($identifier) || empty($password)) {
    error_log("Login failed: Missing identifier or password.");
    echo json_encode(['status' => 'error', 'message' => 'ID and password are required']);
    exit;
}

try {
    $user_found = false;

    // --- Scenario 1: Check for Owner logging in with their Stall ID (e.g. S12345) ---
    if (strpos($identifier, 'S') === 0) {
        error_log("Scenario 1: Detected Stall ID login for ID: " . $identifier);
        
        $stmt_owner = $conn->prepare(
            "SELECT sd.stallname, sd.agreement_accepted, sd.approval, o.password, o.phonenumber
             FROM stalldetails sd
             JOIN Osignup o ON sd.phonenumber = o.phonenumber
             WHERE sd.stall_id = ?"
        );
        $stmt_owner->bind_param("s", $identifier);
        $stmt_owner->execute();
        $result_owner = $stmt_owner->get_result();

        if ($result_owner->num_rows === 1) {
            $owner_details = $result_owner->fetch_assoc();
            $db_pass = $owner_details['password'];
            
            $pass_ok = false;
            if (password_verify($password, $db_pass)) {
                $pass_ok = true;
            } elseif ($password === $db_pass) {
                $pass_ok = true;
            }

            if ($pass_ok) {
                $user_found = true;

                if ($owner_details['approval'] == 1) {
                    // LOGIN VIA STALL ID: Goes to Agreement (if not accepted) or Home
                    if ($owner_details['agreement_accepted'] === null) {
                        $response = [
                            'status' => 'success',
                            'message' => 'Please accept the terms to continue.',
                            'role' => 'owner_agreement_pending',
                            'data' => [
                                'phonenumber' => $owner_details['phonenumber'],
                                'stall_id' => $identifier,
                                'stall_name' => $owner_details['stallname']
                            ]
                        ];
                    } else {
                        $response = [
                            'status' => 'success',
                            'message' => 'Owner login successful!',
                            'role' => 'owner_approved',
                            'data' => [ 'stall_id' => $identifier, 'stall_name' => $owner_details['stallname'] ]
                        ];
                    }
                } else {
                    // Not approved yet (rare for Stall ID login, but safe fallback)
                    $response = [
                        'status' => 'success',
                        'message' => 'Login successful (Stall Status Check).',
                        'role' => 'owner_status_check',
                        'data' => [
                            'stall_status' => $owner_details['approval'], 
                            'stall_id' => $identifier,
                            'phonenumber' => $owner_details['phonenumber']
                        ]
                    ];
                }
            }
        }
        $stmt_owner->close();
    }

    // --- Scenario 2: If not a Stall ID, check if it's a User/Student ---
    if (!$user_found) {
        $stmt_check_id = $conn->prepare("SELECT id FROM usignup WHERE student_id = ? AND is_admin = 0");
        $stmt_check_id->bind_param("s", $identifier);
        $stmt_check_id->execute();
        $res_check = $stmt_check_id->get_result();

        if ($res_check->num_rows > 0) {
            $stmt_check_id->close(); 
            
            $stmt_user = $conn->prepare("SELECT * FROM usignup WHERE student_id = ? AND is_admin = 0");
            $stmt_user->bind_param("s", $identifier);
            $stmt_user->execute();
            $result_user = $stmt_user->get_result();
            $user = $result_user->fetch_assoc();
            $db_pass = $user['password'];

            $pass_ok = false;
            if (password_verify($password, $db_pass)) {
                $pass_ok = true;
            } elseif ($password === $db_pass) {
                $pass_ok = true;
            }

            if ($pass_ok) {
                $user_found = true;
                $response = [
                    'status' => 'success',
                    'message' => 'User login successful!',
                    'role' => 'student',
                    'data' => [
                        'id' => $user['id'],
                        'fullname' => $user['fullname'],
                        'student_id' => $user['student_id'],
                        'email' => $user['email']
                    ]
                ];
            }
            $stmt_user->close();
        } else {
            $stmt_check_id->close();
        }
    }

    // --- Scenario 3: Owner Login by Phone Number ---
    if (!$user_found) {
        error_log("Scenario 3: Checking Phone Number login for identifier: " . $identifier);
        
        // We LEFT JOIN stalldetails. If approval is NULL, it means they registered but didn't submit details.
        $stmt_owner_phone = $conn->prepare(
            "SELECT o.*, sd.approval, sd.stall_id, sd.rejection_reason, sd.stallname, sd.agreement_accepted
             FROM Osignup o
             LEFT JOIN stalldetails sd ON o.phonenumber = sd.phonenumber
             WHERE o.phonenumber = ?"
        );
        $stmt_owner_phone->bind_param("s", $identifier);
        $stmt_owner_phone->execute();
        $result_owner_phone = $stmt_owner_phone->get_result();

        if ($result_owner_phone->num_rows === 1) {
            $owner = $result_owner_phone->fetch_assoc();
            $db_pass = $owner['password'];

            $pass_ok = false;
            if (password_verify($password, $db_pass)) {
                $pass_ok = true;
            } elseif ($password === $db_pass) {
                $pass_ok = true;
            }

            if ($pass_ok) {
                $user_found = true;

                if (is_null($owner['approval'])) {
                    // Role: DETAILS REQUIRED (Go to OstalldetailsActivity)
                    $response = [
                        'status' => 'success',
                        'message' => 'Please submit your stall details.',
                        'role' => 'owner_details_required', 
                        'data' => [
                            'phonenumber' => $owner['phonenumber'],
                            'email' => $owner['email'],
                            'fullname' => $owner['fullname']
                        ]
                    ];
                } else {
                    $stall_status = (int)$owner['approval'];
                    
                    if ($stall_status == 1) {
                        // --- [FIXED] LOGIN VIA PHONE NUMBER: Goes to "Approved View" (To see ID) ---
                        // We do NOT send them to Agreement yet. They must log in with Stall ID for that.
                        $response = [
                            'status' => 'success',
                            'message' => 'Your stall has been approved! Please use your Stall ID to login.',
                            'role' => 'owner_approval_pending_view', // <--- CHANGED THIS
                            'data' => [ 
                                'phonenumber' => $owner['phonenumber'],
                                'stall_id' => $owner['stall_id'],
                                'stall_name' => $owner['stallname']
                            ]
                        ];
                    } else {
                        // PENDING (0) or REJECTED (-1)
                        $response = [
                            'status' => 'success',
                            'message' => 'Owner status check successful!',
                            'role' => 'owner_status_check',
                            'data' => [
                                'stall_status' => $stall_status,
                                'stall_id' => $owner['stall_id'] ?? null,
                                'rejection_reason' => $owner['rejection_reason'] ?? null,
                                'fullname' => $owner['fullname'],
                                'email' => $owner['email'],
                                'phonenumber' => $owner['phonenumber']
                            ]
                        ];
                    }
                }
            }
        }
        $stmt_owner_phone->close();
    }

    if (!$user_found) {
        error_log("Login failed: User not found in any scenario or password mismatch.");
        $response = ['status' => 'error', 'message' => 'Invalid credentials or user not found'];
    }

} catch (mysqli_sql_exception $e) {
    error_log("Database Exception: " . $e->getMessage()); 
    $response = ['status' => 'error', 'message' => 'Database Error: ' . $e->getMessage()];
} catch (Exception $e) { 
    error_log("General Exception: " . $e->getMessage());
    $response = ['status' => 'error', 'message' => 'Server Error: ' . $e->getMessage()];
}

echo json_encode($response);
$conn->close();
?>