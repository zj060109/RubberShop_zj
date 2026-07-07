package com.rubbershop.app.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.rubbershop.app.data.api.ApiService;
import com.rubbershop.app.data.api.RetrofitClient;
import com.rubbershop.app.data.model.Models.*;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class Repository {
    private static volatile ApiService api;
    private static Handler mainHandler;

    public Repository() {
        if (api == null) {
            api = RetrofitClient.getApi();
        }
    }

    private static Handler getMainHandler() {
        if (mainHandler == null) {
            mainHandler = new Handler(Looper.getMainLooper());
        }
        return mainHandler;
    }

    public interface ResultCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    private <T> void enqueue(Call<ApiResponse<T>> call, ResultCallback<T> callback) {
        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                Handler h = getMainHandler();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    T data = response.body().getData();
                    if (callback != null) h.post(() -> callback.onSuccess(data));
                } else {
                    String msg = "请求失败";
                    if (response.body() != null && response.body().getMessage() != null)
                        msg = response.body().getMessage();
                    else if (response.code() == 401 || response.code() == 403)
                        msg = "请重新登录";
                    final String finalMsg = msg;
                    if (callback != null) h.post(() -> callback.onError(finalMsg));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "网络错误";
                if (msg.contains("Unable to resolve host") || msg.contains("Failed to connect")) {
                    msg = "无法连接服务器，请检查网络";
                }
                final String finalMsg = msg;
                if (callback != null) getMainHandler().post(() -> callback.onError(finalMsg));
            }
        });
    }

    public void login(String phone, String password, ResultCallback<LoginResponse> cb) { enqueue(api.login(new LoginRequest(phone, password)), cb); }
    public void register(String phone, String password, String realName, ResultCallback<LoginResponse> cb) { enqueue(api.register(new RegisterRequest(phone, password, realName)), cb); }
    public void changePassword(String oldPw, String newPw, ResultCallback<Object> cb) { enqueue(api.changePassword(new PasswordUpdateRequest(oldPw, newPw)), cb); }

    public void getProfile(ResultCallback<UserProfile> cb) { enqueue(api.getProfile(), cb); }
    public void updateProfile(ProfileUpdateRequest req, ResultCallback<Object> cb) { enqueue(api.updateProfile(req), cb); }
    public void recharge(Double amount, ResultCallback<Object> cb) { enqueue(api.recharge(new RechargeRequest(amount)), cb); }
    public void getBalanceLogs(int page, int pageSize, ResultCallback<PageResponse<BalanceLog>> cb) { enqueue(api.getBalanceLogs(page, pageSize), cb); }

    public void getProducts(int page, int pageSize, String keyword, Long categoryId, ResultCallback<PageResponse<Product>> cb) { enqueue(api.getProducts(page, pageSize, keyword, categoryId), cb); }
    public void getProductDetail(Long id, ResultCallback<Product> cb) { enqueue(api.getProductDetail(id), cb); }
    public void getCategories(ResultCallback<List<CategoryTree>> cb) { enqueue(api.getCategories(), cb); }

    public void createOrder(OrderCreateRequest req, ResultCallback<Order> cb) { enqueue(api.createOrder(req), cb); }
    public void getOrders(int page, int pageSize, String status, ResultCallback<PageResponse<Order>> cb) { enqueue(api.getOrders(page, pageSize, status), cb); }
    public void getOrderDetail(Long id, ResultCallback<OrderDetailResponse> cb) { enqueue(api.getOrderDetail(id), cb); }
    public void receiveOrder(Long id, ResultCallback<Object> cb) { enqueue(api.receiveOrder(id), cb); }
    public void cancelOrder(Long id, ResultCallback<Object> cb) { enqueue(api.cancelOrder(id), cb); }
    public void updateOrderStatus(Long id, String status, ResultCallback<Object> cb) { enqueue(api.updateOrderStatus(id, status), cb); }
    public void shipOrder(Long id, String expressCompany, String trackingNo, ResultCallback<Object> cb) { enqueue(api.shipOrder(id, expressCompany, trackingNo), cb); }

    public void getReceivables(int page, int pageSize, String status, ResultCallback<PageResponse<Receivable>> cb) { enqueue(api.getReceivables(page, pageSize, status), cb); }
    public void getReceivableDetail(Long id, ResultCallback<ReceivableDetailResponse> cb) { enqueue(api.getReceivableDetail(id), cb); }
    public void repay(Long id, Double amount, String paymentMethod, String remark, ResultCallback<Object> cb) { enqueue(api.repay(id, new RepaymentRequest(amount, paymentMethod, remark)), cb); }

    public void getIdentityStatus(ResultCallback<IdentityVerification> cb) { enqueue(api.getIdentityStatus(), cb); }
    public void submitIdentity(String idCard, String realName, String faceImage, ResultCallback<Object> cb) { enqueue(api.submitIdentity(idCard, realName, faceImage), cb); }

    public void createCustomization(String description, ResultCallback<Object> cb) { enqueue(api.createCustomization(new CustomizationCreateRequest(description)), cb); }
    public void getCustomizations(int page, int pageSize, String status, ResultCallback<PageResponse<Customization>> cb) { enqueue(api.getCustomizations(page, pageSize, status), cb); }
    public void getCustomizationDetail(Long id, ResultCallback<Customization> cb) { enqueue(api.getCustomizationDetail(id), cb); }
    public void confirmCustomization(Long id, String paymentMethod, ResultCallback<Order> cb) { enqueue(api.confirmCustomization(id, new ConfirmCustomizationRequest(paymentMethod)), cb); }
    public void cancelCustomization(Long id, ResultCallback<Object> cb) { enqueue(api.cancelCustomization(id), cb); }

    public void getConfigs(ResultCallback<List<SysConfig>> cb) { enqueue(api.getConfigs(), cb); }

    public void uploadFile(MultipartBody.Part part, ResultCallback<String> cb) {
        enqueue(api.uploadFile(part), cb);
    }

    public void getConversations(ResultCallback<List<ChatConversation>> cb) { enqueue(api.getConversations(), cb); }
    public void getMessages(Long conversationId, int page, int pageSize, ResultCallback<ChatMessagesResponse> cb) { enqueue(api.getMessages(conversationId, page, pageSize), cb); }
    public void sendMessage(Long conversationId, String content, ResultCallback<ChatMessage> cb) { enqueue(api.sendMessage(new ChatSendRequest(conversationId, content)), cb); }
    public void markRead(Long conversationId, ResultCallback<Object> cb) { enqueue(api.markRead(conversationId), cb); }

    public void customerShip(Long id, String expressCompany, String trackingNo, ResultCallback<Object> cb) { enqueue(api.customerShip(id, expressCompany, trackingNo), cb); }
    public void merchantReceive(Long id, ResultCallback<Object> cb) { enqueue(api.merchantReceive(id), cb); }
    public void updateInstallation(Long id, String video, String images, String remark, String status, ResultCallback<Object> cb) { enqueue(api.updateInstallation(id, video, images, remark, status), cb); }
    public void getInstallation(Long id, ResultCallback<InstallationInfo> cb) { enqueue(api.getInstallation(id), cb); }
    public void getMerchantAddress(ResultCallback<MerchantAddress> cb) { enqueue(api.getMerchantAddress(), cb); }
    public void updateMerchantAddress(java.util.Map<String, String> body, ResultCallback<Object> cb) { enqueue(api.updateMerchantAddress(body), cb); }

    public void getPurchases(int page, int pageSize, String status, ResultCallback<PageResponse<Purchase>> cb) { enqueue(api.getPurchases(page, pageSize, status), cb); }
    public void getPurchaseDetail(Long id, ResultCallback<PurchaseDetailResponse> cb) { enqueue(api.getPurchaseDetail(id), cb); }
    public void updatePurchaseStatus(Long id, String status, ResultCallback<Object> cb) { enqueue(api.updatePurchaseStatus(id, status), cb); }
    public void quotePurchase(Long id, java.util.Map<String, Object> body, ResultCallback<Object> cb) { enqueue(api.quotePurchase(id, body), cb); }
    public void updatePurchaseLogistics(Long id, String expressCompany, String trackingNo, ResultCallback<Object> cb) { enqueue(api.updatePurchaseLogistics(id, expressCompany, trackingNo), cb); }
}
