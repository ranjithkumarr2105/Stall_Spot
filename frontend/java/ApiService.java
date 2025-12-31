package com.simats.foodstall;

import androidx.annotation.Nullable;

import com.simats.foodstall.model.AdminHomeCounts;
import com.simats.foodstall.model.AdminProfileResponse;
import com.simats.foodstall.model.AdminStall;
import com.simats.foodstall.model.AnalysisData;
import com.simats.foodstall.model.FavoriteStallsResponse;
import com.simats.foodstall.model.OGetMenuResponse;
import com.simats.foodstall.model.OGetOrdersResponse;
import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.OrderPlacementResponse;
import com.simats.foodstall.model.OwnerProfileResponse;
import com.simats.foodstall.model.ProductAnalyticsResponse;
import com.simats.foodstall.model.RazorpayOrderResponse;
import com.simats.foodstall.model.ReportItem;
import com.simats.foodstall.model.RevenueResponse;
import com.simats.foodstall.model.StallEdit;
import com.simats.foodstall.model.StallLocationResponse;
import com.simats.foodstall.model.StatusResponse;
import com.simats.foodstall.model.HomeDataResponse;
import com.simats.foodstall.model.LoginResponse;
import com.simats.foodstall.model.StallDetailsListResponse;
import com.simats.foodstall.model.DashboardResponse;
import com.simats.foodstall.model.StallMenuResponse;
import com.simats.foodstall.model.TimeSlotsResponse;
import com.simats.foodstall.model.UserOrder;
import com.simats.foodstall.model.UserProfileResponse;
import com.simats.foodstall.model.WalletBalanceResponse;
import com.simats.foodstall.model.WalletDetailsResponse;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ApiService {

    @FormUrlEncoded
    @POST("Usignup.php")
    Call<StatusResponse> registerUser(
            @Field("fullname") String fullName,
            @Field("id") String studentId,
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirm_password") String confirmPassword
    );

    @FormUrlEncoded
    @POST("Osignup.php")
    Call<StatusResponse> registerOwner(
            @Field("fullname") String fullName,
            @Field("phonenumber") String phoneNumber,
            @Field("password") String password,
            @Field("confirm_password") String confirmPassword
    );

    @FormUrlEncoded
    @POST("Ostallsignup.php")
    Call<StatusResponse> submitStallDetails(
            @Field("stallname") String stallName,
            @Field("ownername") String ownerName,
            @Field("phonenumber") String phoneNumber,
            @Field("email") String email,
            @Field("fulladdress") String fullAddress,
            @Field("fssainumber") String fssaiNumber,
            @Field("password") String password,
            @Field("signup_type") String signupType // NEW FIELD
    );

    @FormUrlEncoded
    @POST("update_stall_status.php")
    Call<StatusResponse> updateStallStatus(
            @Field("email") String email,
            @Field("status") String status,
            @Field("reason") @Nullable String reason
    );

    @GET("Aget_pending_stalls.php")
    Call<StallDetailsListResponse> getOnlyPendingStalls();

    @FormUrlEncoded
    @POST("Aget_stalls.php")
    Call<StallDetailsListResponse> getStallsByFilter(@Field("filter") String filter);

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> loginUser(
            @Field("identifier") String identifier,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("send_otp.php")
    Call<StatusResponse> sendOtp(@Field("email") String email);

    @FormUrlEncoded
    @POST("verify_otp.php")
    Call<StatusResponse> verifyOtp(
            @Field("email") String email,
            @Field("otp") String otp
    );

    @FormUrlEncoded
    @POST("reset_password.php")
    Call<StatusResponse> resetPassword(
            @Field("email") String email,
            @Field("otp") String otp,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("Odashboard_data.php")
    Call<DashboardResponse> getDashboardData(@Field("stall_id") String stallId);

    @FormUrlEncoded
    @POST("oupdate_menu.php")
    Call<OGetMenuResponse> getOwnerMenuDetails(@Field("action") String action, @Field("stall_id") String stallId);

    @Multipart
    @POST("oupdate_menu.php")
    Call<StatusResponse> addMenuItem(@PartMap Map<String, RequestBody> fields, @Nullable @Part MultipartBody.Part image);

    @Multipart
    @POST("oupdate_menu.php")
    Call<StatusResponse> updateMenuItem(@PartMap Map<String, RequestBody> fields, @Nullable @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST("oupdate_menu.php")
    Call<StatusResponse> deleteMenuItem(@Field("action") String action, @Field("item_id") int itemId);

    // UPDATED: Renamed for clarity to fix duplicate method error
    @FormUrlEncoded
    @POST("oupdate_menu.php")
    Call<StatusResponse> updateOwnerStallStatus(@Field("action") String action, @Field("stall_id") String stallId, @Field("is_open_today") int isOpen, @Field("opening_hours") String openingHours, @Field("closing_hours") String closingHours);

    @FormUrlEncoded
    @POST("Uget_home_data.php")
    Call<HomeDataResponse> getHomeData(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("Uget_stall_menu.php")
    Call<StallMenuResponse> getStallMenu(
            @Field("stall_id") String stallId,
            @Field("student_id") String studentId
    );

    @FormUrlEncoded
    @POST("Usubmit_review.php")
    Call<StatusResponse> submitReview(
            @Field("stall_id") String stallId,
            @Field("student_id") String studentId,
            @Field("rating") float rating,
            @Field("review_text") String reviewText
    );

    @FormUrlEncoded
    @POST("Uget_pickup_slots.php")
    Call<TimeSlotsResponse> getPickupSlots(@Field("stall_id") String stallId);

    @FormUrlEncoded
    @POST("Udelete_review.php")
    Call<StatusResponse> deleteReview(
            @Field("stall_id") String stallId,
            @Field("student_id") String studentId
    );

    @FormUrlEncoded
    @POST("Uget_wallet_details.php")
    Call<WalletBalanceResponse> getWalletBalance(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("Ucreate_razorpay_order.php")
    Call<RazorpayOrderResponse> createRazorpayOrder(@Field("amount") double amount);

    @FormUrlEncoded
    @POST("Uplace_order.php")
    Call<OrderPlacementResponse> placeOrder(@Field("stall_id") String stallId, @Field("student_id") String studentId, @Field("total_amount") double totalAmount, @Field("subtotal") double subtotal, @Field("parcel_fee") double parcelFee, @Field("payment_id") String paymentId, @Field("pickup_time") @Nullable String pickupTime, @Field("order_items") String orderItemsJson);

    @FormUrlEncoded
    @POST("Uplace_order_wallet.php")
    Call<OrderPlacementResponse> placeWalletOrder(@Field("stall_id") String stallId, @Field("student_id") String studentId, @Field("total_amount") double totalAmount,@Field("subtotal") double subtotal, @Field("parcel_fee") double parcelFee, @Field("pickup_time") @Nullable String pickupTime, @Field("order_items") String orderItemsJson);

    @FormUrlEncoded
    @POST("Uget_wallet_details.php")
    Call<WalletDetailsResponse> getWalletDetails(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("Uadd_money_to_wallet.php")
    Call<StatusResponse> addMoneyToWallet(
            @Field("student_id") String studentId,
            @Field("amount") double amount,
            @Field("razorpay_payment_id") String paymentId,
            @Field("razorpay_order_id") String orderId,
            @Field("razorpay_signature") String signature
    );

    @FormUrlEncoded
    @POST("Utoggle_favorite.php")
    Call<StatusResponse> toggleFavorite(@Field("student_id") String studentId, @Field("stall_id") String stallId);

    @FormUrlEncoded
    @POST("Uget_favorite_stalls.php")
    Call<FavoriteStallsResponse> getFavoriteStalls(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("A_manage_locations.php")
    Call<StallLocationResponse> getStallLocations(@Field("action") String action);

    @FormUrlEncoded
    @POST("A_manage_locations.php")
    Call<StatusResponse> setStallLocation(
            @Field("action") String action,
            @Field("stall_id") String stallId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("A_manage_locations.php")
    Call<StatusResponse> deleteStallLocation(
            @Field("action") String action,
            @Field("stall_id") String stallId
    );

    @FormUrlEncoded
    @POST("O_get_orders.php")
    Call<OGetOrdersResponse> getOwnerOrders(
            @Field("stall_id") String stallId,
            @Field("filter") String filter
    );
    @FormUrlEncoded
    @POST("O_get_order_history.php")
    Call<List<OOrder>> getOrderHistory(
            @Field("stall_id") String stallId,
            @Field("date") String date // Date in "YYYY-MM-DD" format
    );


    @FormUrlEncoded
    @POST("O_update_order_status.php")
    Call<StatusResponse> updateOwnerOrderStatus(@Field("stall_id") String stallId, @Field("display_order_id") String displayOrderId, @Field("new_status") String newStatus);

    @FormUrlEncoded
    @POST("O_get_revenue_data.php")
    Call<RevenueResponse> getRevenueData(@Field("stall_id") String stallId);

    @FormUrlEncoded
    @POST("O_get_product_analytics.php")
    Call<ProductAnalyticsResponse> getProductAnalytics(
            @Field("stall_id") String stallId,
            @Field("filter") String filter // Can be "overall" or "year"
    );
    @FormUrlEncoded
    @POST("U_get_order_history.php")
    Call<List<UserOrder>> getUserOrderHistory(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("U_delete_orders.php")
    Call<StatusResponse> deleteUserOrders(@Field("student_id") String studentId, @Field("order_ids_json") String orderIdsJson);

    @FormUrlEncoded
    @POST("Udelete_transactions.php")
    Call<StatusResponse> deleteWalletTransactions(
            @Field("student_id") String studentId,
            @Field("transaction_ids_json") String transactionIdsJson
    );
    @FormUrlEncoded
    @POST("U_get_profile.php")
    Call<UserProfileResponse> getUserProfile(@Field("student_id") String studentId);

    @FormUrlEncoded
    @POST("U_update_profile.php")
    Call<StatusResponse> updateUserProfile(
            @Field("student_id") String studentId,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("phonenumber") String phonenumber
    );
    @FormUrlEncoded
    @POST("O_get_profile.php")
    Call<OwnerProfileResponse> getOwnerProfile(@Field("stall_id") String stallId);

    @FormUrlEncoded
    @POST("O_update_profile.php")
    Call<StatusResponse> updateOwnerProfile(
            @Field("stall_id") String stallId,
            @Field("ownername") String ownerName,
            @Field("phonenumber") String phoneNumber,
            @Field("email") String email,
            @Field("fulladdress") String fullAddress
    );
    @FormUrlEncoded
    @POST("A_get_profile.php")
    Call<AdminProfileResponse> getAdminProfile(@Field("admin_id") String adminId);

    @FormUrlEncoded
    @POST("A_update_profile.php")
    Call<StatusResponse> updateAdminProfile(
            @Field("admin_id") String adminId,
            @Field("fullname") String fullname,
            @Field("email") String email,
            @Field("phonenumber") String phonenumber
    );
    @Multipart
    @POST("update_profile.php")
    Call<StatusResponse> updateProfile(
            @PartMap Map<String, RequestBody> fields,
            @Part @Nullable MultipartBody.Part profilePhoto
    );
    @FormUrlEncoded
    @POST("google_auth.php")
    Call<LoginResponse> handleGoogleSignIn ( // Reuse LoginResponse as structure is similar
                                            @Field("id_token") String idToken,
                                            @Field("role_hint") String roleHint);
    @FormUrlEncoded
    @POST("set_password.php")
    Call<StatusResponse> setPassword(
            @Field("role") String role,             // "user" or "owner"
            @Field("identifier") String identifier, // student_id for user, email for owner
            @Field("new_password") String newPassword
    );
    @GET("get_agreement.php")
    Call<ResponseBody> getOwnerAgreement();

    @FormUrlEncoded
    @POST("accept_agreement.php")
    Call<StatusResponse> acceptOwnerAgreement(
            @Field("phonenumber") String phoneNumber
    );
    @Streaming
    @GET("generate_report_pdf.php")
    Call<ResponseBody> downloadMonthlyReport(
            @Query("stall_id") String stallId,
            @Query("month") int month,
            @Query("year") int year
    );
    // Add these two new methods for the rent payment flow
    @FormUrlEncoded
    @POST("O_create_rent_payment_order.php")
    Call<RazorpayOrderResponse> createRentOrder(
            @Field("stall_id") String stallId,
            @Field("invoice_id") int invoiceId,
            @Field("rent_amount") double rentAmount
    );

    @FormUrlEncoded
    @POST("O_verify_rent_payment.php")
    Call<StatusResponse> verifyRentPayment(
            @Field("razorpay_payment_id") String paymentId,
            @Field("razorpay_order_id") String orderId,
            @Field("razorpay_signature") String signature,
            @Field("invoice_id") int invoiceId
    );
    @GET("A_get_stall_rankings.php")
    Call<List<ReportItem>> getStallRankings();
    @GET("A_get_all_stalls.php")
    Call<List<StallEdit>> getAllStalls();

    @FormUrlEncoded
    @POST("A_update_stall_location.php")
    Call<StatusResponse> updateStallLocation(
            @Field("stall_id") String stallId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude
    );

    @FormUrlEncoded
    @POST("A_delete_stall.php")
    Call<StatusResponse> deleteStall(@Field("stall_id") String stallId);
    @GET("A_get_analysis_data.php")
    Call<AnalysisData> getAnalysisData();
    @GET("A_get_home_counts.php")
    Call<AdminHomeCounts> getAdminHomeCounts();
    @GET("A_get_stalls_by_status.php")
    Call<List<AdminStall>> getStallsByStatus(@Query("status") String status);
    @FormUrlEncoded
    @POST("delete_account.php")
    Call<StatusResponse> deleteAccount(
            @Field("identifier") String identifier,
            @Field("role") String role // "user", "owner", "admin"
    );
    // Add this method to verify the current password
    @FormUrlEncoded
    @POST("verify_current_password.php")
    Call<StatusResponse> verifyCurrentPassword(
            @Field("role") String role,
            @Field("identifier") String identifier,
            @Field("current_password") String currentPassword
    );

    // Add this method to update the password after verification
    @FormUrlEncoded
    @POST("update_password.php")
    Call<StatusResponse> updatePassword(
            @Field("role") String role,
            @Field("identifier") String identifier,
            @Field("new_password") String newPassword
    );
    // --- Add this method for fetching policy content ---
    @GET("get_policy.php")
    Call<ResponseBody> getPolicyContent(@Query("policy_key") String policyKey);
    // --- End of new method ---
    // --- Add this method for acknowledging rent ---
    @FormUrlEncoded
    @POST("acknowledge_rent_payment.php")
    Call<StatusResponse> acknowledgeRentPayment(@Field("invoice_id") int invoiceId);
    // --- End new method ---
}