package com.rubbershop.app.data.api;

import com.rubbershop.app.data.model.Models.*;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;
import java.util.List;

public interface ApiService {
    @POST("auth/login") Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    @POST("auth/register") Call<ApiResponse<LoginResponse>> register(@Body RegisterRequest request);
    @PUT("auth/password") Call<ApiResponse<Object>> changePassword(@Body PasswordUpdateRequest request);

    @GET("user/profile") Call<ApiResponse<UserProfile>> getProfile();
    @PUT("user/profile") Call<ApiResponse<Object>> updateProfile(@Body ProfileUpdateRequest request);
    @POST("user/recharge") Call<ApiResponse<Object>> recharge(@Body RechargeRequest request);
    @GET("user/balance_logs") Call<ApiResponse<PageResponse<BalanceLog>>> getBalanceLogs(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("products") Call<ApiResponse<PageResponse<Product>>> getProducts(@Query("page") int page, @Query("pageSize") int pageSize, @Query("keyword") String keyword, @Query("categoryId") Long categoryId);
    @GET("products/{id}") Call<ApiResponse<Product>> getProductDetail(@Path("id") Long id);
    @GET("categories") Call<ApiResponse<List<CategoryTree>>> getCategories();

    @POST("orders") Call<ApiResponse<Order>> createOrder(@Body OrderCreateRequest request);
    @GET("orders") Call<ApiResponse<PageResponse<Order>>> getOrders(@Query("page") int page, @Query("pageSize") int pageSize, @Query("status") String status);
    @GET("orders/{id}") Call<ApiResponse<OrderDetailResponse>> getOrderDetail(@Path("id") Long id);
    @PUT("orders/{id}/receive") Call<ApiResponse<Object>> receiveOrder(@Path("id") Long id);
    @POST("orders/{id}/cancel") Call<ApiResponse<Object>> cancelOrder(@Path("id") Long id);
    @PUT("orders/{id}/status") Call<ApiResponse<Object>> updateOrderStatus(@Path("id") Long id, @Query("status") String status);
    @PUT("orders/{id}/ship") Call<ApiResponse<Object>> shipOrder(@Path("id") Long id, @Query("expressCompany") String expressCompany, @Query("trackingNo") String trackingNo);
    @GET("admin/statistics/dashboard") Call<ApiResponse<Object>> getDashboard();

    @GET("receivables") Call<ApiResponse<PageResponse<Receivable>>> getReceivables(@Query("page") int page, @Query("pageSize") int pageSize, @Query("status") String status);
    @GET("receivables/{id}") Call<ApiResponse<ReceivableDetailResponse>> getReceivableDetail(@Path("id") Long id);
    @POST("receivables/{id}/receipts") Call<ApiResponse<Object>> repay(@Path("id") Long id, @Body RepaymentRequest request);

    @GET("identity/status") Call<ApiResponse<IdentityVerification>> getIdentityStatus();
    @POST("identity/submit") Call<ApiResponse<Object>> submitIdentity(@Query("idCard") String idCard, @Query("realName") String realName, @Query("faceImage") String faceImage);

    @POST("customizations") Call<ApiResponse<Object>> createCustomization(@Body CustomizationCreateRequest request);
    @GET("customizations") Call<ApiResponse<PageResponse<Customization>>> getCustomizations(@Query("page") int page, @Query("pageSize") int pageSize, @Query("status") String status);
    @GET("customizations/{id}") Call<ApiResponse<Customization>> getCustomizationDetail(@Path("id") Long id);
    @PUT("customizations/{id}/confirm") Call<ApiResponse<Order>> confirmCustomization(@Path("id") Long id, @Body ConfirmCustomizationRequest request);
    @POST("customizations/{id}/cancel") Call<ApiResponse<Object>> cancelCustomization(@Path("id") Long id);

    @Multipart @POST("upload") Call<ApiResponse<String>> uploadFile(@Part MultipartBody.Part file);

    @GET("configs") Call<ApiResponse<List<SysConfig>>> getConfigs();

    @GET("chat/conversations") Call<ApiResponse<List<ChatConversation>>> getConversations();
    @GET("chat/conversations/{id}/messages") Call<ApiResponse<ChatMessagesResponse>> getMessages(@Path("id") Long conversationId, @Query("page") int page, @Query("pageSize") int pageSize);
    @POST("chat/messages") Call<ApiResponse<ChatMessage>> sendMessage(@Body ChatSendRequest request);
    @PUT("chat/conversations/{id}/read") Call<ApiResponse<Object>> markRead(@Path("id") Long conversationId);

    @PUT("orders/{id}/customer-ship") Call<ApiResponse<Object>> customerShip(@Path("id") Long id, @Query("expressCompany") String expressCompany, @Query("trackingNo") String trackingNo);
    @PUT("orders/{id}/merchant-receive") Call<ApiResponse<Object>> merchantReceive(@Path("id") Long id);
    @PUT("orders/{id}/installation") Call<ApiResponse<Object>> updateInstallation(@Path("id") Long id, @Query("video") String video, @Query("images") String images, @Query("remark") String remark, @Query("status") String status);
    @GET("orders/{id}/installation") Call<ApiResponse<InstallationInfo>> getInstallation(@Path("id") Long id);
    @GET("orders/merchant-address") Call<ApiResponse<MerchantAddress>> getMerchantAddress();
    @PUT("orders/merchant-address") Call<ApiResponse<Object>> updateMerchantAddress(@Body java.util.Map<String, String> body);

    @GET("purchases") Call<ApiResponse<PageResponse<Purchase>>> getPurchases(@Query("page") int page, @Query("pageSize") int pageSize, @Query("status") String status);
    @GET("purchases/{id}") Call<ApiResponse<PurchaseDetailResponse>> getPurchaseDetail(@Path("id") Long id);
    @PUT("purchases/{id}/status") Call<ApiResponse<Object>> updatePurchaseStatus(@Path("id") Long id, @Query("status") String status);
    @PUT("purchases/{id}/quote") Call<ApiResponse<Object>> quotePurchase(@Path("id") Long id, @Body java.util.Map<String, Object> body);
    @PUT("purchases/{id}/logistics") Call<ApiResponse<Object>> updatePurchaseLogistics(@Path("id") Long id, @Query("expressCompany") String expressCompany, @Query("trackingNo") String trackingNo);
}
