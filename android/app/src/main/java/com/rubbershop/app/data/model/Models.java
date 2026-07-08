package com.rubbershop.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Models {

    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public T getData() { return data; }
        public boolean isSuccess() { return code == 200; }
    }

    public static class LoginRequest {
        private String phone;
        private String password;
        public LoginRequest(String phone, String password) { this.phone = phone; this.password = password; }
        public String getPhone() { return phone; }
        public String getPassword() { return password; }
    }

    public static class RegisterRequest {
        private String phone;
        private String password;
        private String realName;
        public RegisterRequest(String phone, String password, String realName) { this.phone = phone; this.password = password; this.realName = realName; }
    }

    public static class LoginResponse {
        private String token;
        private Long userId;
        private String role;
        public String getToken() { return token; }
        public Long getUserId() { return userId; }
        public String getRole() { return role; }
    }

    public static class PasswordUpdateRequest {
        private String oldPassword;
        private String newPassword;
        public PasswordUpdateRequest(String oldPassword, String newPassword) { this.oldPassword = oldPassword; this.newPassword = newPassword; }
    }

    public static class UserProfile {
        private Long id;
        private String phone;
        private String role;
        private String realName;
        private String avatar;
        private Double balance;
        private Double creditLimit;
        private String companyName;
        @SerializedName("defaultReceiverName") private String receiverName;
        @SerializedName("defaultReceiverPhone") private String receiverPhone;
        @SerializedName("defaultProvince") private String province;
        @SerializedName("defaultCity") private String city;
        @SerializedName("defaultDistrict") private String district;
        @SerializedName("defaultDetailAddress") private String detailAddress;
        private Integer status;
        private String createdAt;
        private Integer points;
        public Long getId() { return id; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
        public String getRealName() { return realName; }
        public String getAvatar() { return avatar; }
        public Double getBalance() { return balance; }
        public Double getCreditLimit() { return creditLimit; }
        public String getCompanyName() { return companyName; }
        public String getReceiverName() { return receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getDetailAddress() { return detailAddress; }
        public Integer getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public Integer getPoints() { return points; }
    }

    public static class ProfileUpdateRequest {
        private String realName;
        private String avatar;
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        public ProfileUpdateRequest(String realName, String avatar, String receiverName, String receiverPhone, String province, String city, String district, String detailAddress) {
            this.realName = realName; this.avatar = avatar; this.receiverName = receiverName; this.receiverPhone = receiverPhone;
            this.province = province; this.city = city; this.district = district; this.detailAddress = detailAddress;
        }
    }

    public static class RechargeRequest {
        private Double amount;
        public RechargeRequest(Double amount) { this.amount = amount; }
    }

    public static class BalanceLog {
        @SerializedName("id_zj") private Long id;
        @SerializedName("user_id_zj") private Long userId;
        @SerializedName("change_amount_zj") private Double changeAmount;
        @SerializedName("current_balance_zj") private Double currentBalance;
        @SerializedName("type_zj") private String type;
        @SerializedName("reference_id_zj") private Long referenceId;
        @SerializedName("remark_zj") private String remark;
        @SerializedName("created_at_zj") private String createdAt;
        public Long getId() { return id; }
        public Long getUserId() { return userId; }
        public Double getChangeAmount() { return changeAmount; }
        public Double getCurrentBalance() { return currentBalance; }
        public String getType() { return type; }
        public Long getReferenceId() { return referenceId; }
        public String getRemark() { return remark; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class Product {
        @SerializedName("id_zj") private Long id;
        @SerializedName("category_id_zj") private Long categoryId;
        @SerializedName("name_zj") private String name;
        @SerializedName("spec_zj") private String spec;
        @SerializedName("description_zj") private String description;
        @SerializedName("images_zj") private String images;
        @SerializedName("price_zj") private Double price;
        @SerializedName("stock_zj") private Integer stock;
        @SerializedName("warning_stock_zj") private Integer warningStock;
        @SerializedName("status_zj") private String status;
        @SerializedName("factory_id_zj") private Long factoryId;
        @SerializedName("is_customized_zj") private Integer isCustomized;
        @SerializedName("created_at_zj") private String createdAt;
        public Long getId() { return id; }
        public Long getCategoryId() { return categoryId; }
        public String getName() { return name; }
        public String getSpec() { return spec; }
        public String getDescription() { return description; }
        public String getImages() { return images; }
        public Double getPrice() { return price != null ? price : 0.0; }
        public Integer getStock() { return stock != null ? stock : 0; }
        public Integer getWarningStock() { return warningStock; }
        public String getStatus() { return status; }
        public Long getFactoryId() { return factoryId; }
        public Integer getIsCustomized() { return isCustomized; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class CategoryTree {
        @SerializedName("id") private Long id;
        @SerializedName("name") private String name;
        @SerializedName("parentId") private Long parentId;
        @SerializedName("sort") private Integer sort;
        @SerializedName("icon") private String icon;
        private List<CategoryTree> children;
        public Long getId() { return id; }
        public String getName() { return name; }
        public Long getParentId() { return parentId; }
        public Integer getSort() { return sort; }
        public String getIcon() { return icon; }
        public List<CategoryTree> getChildren() { return children; }
    }

    public static class Order {
        @SerializedName("id_zj") private Long id;
        @SerializedName("order_no_zj") private String orderNo;
        @SerializedName("user_id_zj") private Long userId;
        @SerializedName("total_amount_zj") private Double totalAmount;
        @SerializedName("actual_amount_zj") private Double actualAmount;
        @SerializedName("payment_method_zj") private String paymentMethod;
        @SerializedName("status_zj") private String status;
        @SerializedName("receiver_name_zj") private String receiverName;
        @SerializedName("receiver_phone_zj") private String receiverPhone;
        @SerializedName("province_zj") private String province;
        @SerializedName("city_zj") private String city;
        @SerializedName("district_zj") private String district;
        @SerializedName("detail_address_zj") private String detailAddress;
        @SerializedName("express_company_zj") private String expressCompany;
        @SerializedName("tracking_no_zj") private String trackingNo;
        @SerializedName("need_installation_zj") private Integer needInstallation;
        @SerializedName("customer_express_company_zj") private String customerExpressCompany;
        @SerializedName("customer_tracking_no_zj") private String customerTrackingNo;
        @SerializedName("installation_video_zj") private String installationVideo;
        @SerializedName("installation_images_zj") private String installationImages;
        @SerializedName("paid_at_zj") private String paidAt;
        @SerializedName("shipped_at_zj") private String shippedAt;
        @SerializedName("finished_at_zj") private String finishedAt;
        @SerializedName("created_at_zj") private String createdAt;
        public Long getId() { return id; }
        public String getOrderNo() { return orderNo; }
        public Long getUserId() { return userId; }
        public Double getTotalAmount() { return totalAmount; }
        public Double getActualAmount() { return actualAmount != null ? actualAmount : 0.0; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getStatus() { return status; }
        public String getReceiverName() { return receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getDetailAddress() { return detailAddress; }
        public String getExpressCompany() { return expressCompany; }
        public String getTrackingNo() { return trackingNo; }
        public Integer getNeedInstallation() { return needInstallation; }
        public String getCustomerExpressCompany() { return customerExpressCompany; }
        public String getCustomerTrackingNo() { return customerTrackingNo; }
        public String getInstallationVideo() { return installationVideo; }
        public String getInstallationImages() { return installationImages; }
        public String getPaidAt() { return paidAt; }
        public String getShippedAt() { return shippedAt; }
        public String getFinishedAt() { return finishedAt; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class OrderItem {
        @SerializedName("id_zj") private Long id;
        @SerializedName("order_id_zj") private Long orderId;
        @SerializedName("product_id_zj") private Long productId;
        @SerializedName("product_name_zj") private String productName;
        @SerializedName("product_image_zj") private String productImage;
        @SerializedName("price_zj") private Double price;
        @SerializedName("quantity_zj") private Integer quantity;
        @SerializedName("subtotal_zj") private Double subtotal;
        public Long getId() { return id; }
        public String getProductName() { return productName; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice() { return price != null ? price : 0.0; }
        public Double getSubtotal() { return subtotal != null ? subtotal : 0.0; }
    }

    public static class OrderStatusLog {
        @SerializedName("id_zj") private Long id;
        @SerializedName("order_id_zj") private Long orderId;
        @SerializedName("from_status_zj") private String fromStatus;
        @SerializedName("to_status_zj") private String toStatus;
        @SerializedName("remark_zj") private String remark;
        @SerializedName("created_at_zj") private String createdAt;
        public String getToStatus() { return toStatus; }
        public String getRemark() { return remark; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class OrderDetailResponse {
        private Order order;
        private List<OrderItem> items;
        @SerializedName("statusLogs") private List<OrderStatusLog> statusLogs;
        public Order getOrder() { return order; }
        public List<OrderItem> getItems() { return items; }
        public List<OrderStatusLog> getStatusLogs() { return statusLogs; }
    }

    public static class OrderCreateRequest {
        public List<OrderItemRequest> items;
        public String paymentMethod;
        public Integer needInstallation;
        public String receiverName;
        public String receiverPhone;
        public String province;
        public String city;
        public String district;
        public String detailAddress;
        public OrderCreateRequest(List<OrderItemRequest> items, String paymentMethod) { this.items = items; this.paymentMethod = paymentMethod; }
    }

    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        public OrderItemRequest(Long productId, Integer quantity) { this.productId = productId; this.quantity = quantity; }
    }

    public static class PageResponse<T> {
        private List<T> records;
        private Long total;
        public List<T> getRecords() { return records; }
        public Long getTotal() { return total; }
    }

    public static class Receivable {
        @SerializedName("id_zj") private Long id;
        @SerializedName("order_id_zj") private Long orderId;
        @SerializedName("user_id_zj") private Long userId;
        @SerializedName("amount_owed_zj") private Double amountOwed;
        @SerializedName("amount_paid_zj") private Double amountPaid;
        @SerializedName("status_zj") private String status;
        @SerializedName("created_at_zj") private String createdAt;
        public Long getId() { return id; }
        public Long getOrderId() { return orderId; }
        public Double getAmountOwed() { return amountOwed != null ? amountOwed : 0.0; }
        public Double getAmountPaid() { return amountPaid != null ? amountPaid : 0.0; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class ReceivableDetailResponse {
        private Receivable receivable;
        private List<Receipt> receipts;
        public Receivable getReceivable() { return receivable; }
        public List<Receipt> getReceipts() { return receipts; }
    }

    public static class Receipt {
        @SerializedName("id_zj") private Long id;
        @SerializedName("amount_zj") private Double amount;
        @SerializedName("payment_method_zj") private String paymentMethod;
        @SerializedName("remark_zj") private String remark;
        @SerializedName("created_at_zj") private String createdAt;
        public Long getId() { return id; }
        public Double getAmount() { return amount != null ? amount : 0.0; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getRemark() { return remark; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class RepaymentRequest {
        private Double amount;
        private String paymentMethod;
        private String remark;
        public RepaymentRequest(Double amount, String paymentMethod, String remark) { this.amount = amount; this.paymentMethod = paymentMethod; this.remark = remark; }
    }

    public static class IdentityVerification {
        @SerializedName("id_zj") private Long id;
        @SerializedName("user_id_zj") private Long userId;
        @SerializedName("id_card_zj") private String idCard;
        @SerializedName("real_name_zj") private String realName;
        @SerializedName("face_image_zj") private String faceImage;
        @SerializedName("status_zj") private Integer status;
        public Long getId() { return id; }
        public String getIdCard() { return idCard; }
        public String getRealName() { return realName; }
        public Integer getStatus() { return status; }
    }

    public static class Customization {
        @SerializedName("id_zj") private Long id;
        @SerializedName("status_zj") private String status;
        @SerializedName("description_zj") private String description;
        @SerializedName("total_quoted_price_zj") private Double totalQuotedPrice;
        @SerializedName("created_at_zj") private String createdAt;
        private List<CustomizationItem> items;
        public Long getId() { return id; }
        public String getStatus() { return status; }
        public String getDescription() { return description; }
        public Double getTotalQuotedPrice() { return totalQuotedPrice != null ? totalQuotedPrice : 0.0; }
        public String getCreatedAt() { return createdAt; }
        public List<CustomizationItem> getItems() { return items; }
    }

    public static class CustomizationItem {
        @SerializedName("id_zj") private Long id;
        @SerializedName("product_spec_zj") private String productSpec;
        @SerializedName("quantity_zj") private Integer quantity;
        @SerializedName("unit_price_zj") private Double unitPrice;
        public String getProductSpec() { return productSpec; }
        public Integer getQuantity() { return quantity; }
        public Double getUnitPrice() { return unitPrice != null ? unitPrice : 0.0; }
    }

    public static class CustomizationCreateRequest {
        private String description;
        private List<String> referenceImages;
        public CustomizationCreateRequest(String description) { this.description = description; }
    }

    public static class ConfirmCustomizationRequest {
        private String paymentMethod;
        public ConfirmCustomizationRequest(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class SysConfig {
        @SerializedName("config_key_zj") private String key;
        @SerializedName("config_value_zj") private String value;
        public String getKey() { return key; }
        public String getValue() { return value; }
    }

    public static class MessageResponse {
        private String message;
        public String getMessage() { return message; }
    }

    public static class DashboardData {
        @SerializedName("today_orders_zj") private Long todayOrders;
        @SerializedName("total_sales_zj") private Double totalSales;
        @SerializedName("pending_orders_zj") private Long pendingOrders;
        @SerializedName("low_stock_zj") private Long lowStock;
        public Long getTodayOrders() { return todayOrders != null ? todayOrders : 0L; }
        public Double getTotalSales() { return totalSales != null ? totalSales : 0.0; }
        public Long getPendingOrders() { return pendingOrders != null ? pendingOrders : 0L; }
        public Long getLowStock() { return lowStock != null ? lowStock : 0L; }
    }

    public static class ChatConversation {
        @SerializedName("id") private Long id;
        @SerializedName("customerId") private Long customerId;
        @SerializedName("merchantId") private Long merchantId;
        @SerializedName("customerName") private String customerName;
        @SerializedName("customerPhone") private String customerPhone;
        @SerializedName("customerAvatar") private String customerAvatar;
        @SerializedName("lastMessage") private String lastMessage;
        @SerializedName("lastMessageTime") private String lastMessageTime;
        @SerializedName("unreadCount") private Integer unreadCount;
        @SerializedName("status") private String status;
        public Long getId() { return id; }
        public Long getCustomerId() { return customerId; }
        public Long getMerchantId() { return merchantId; }
        public String getCustomerName() { return customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public String getCustomerAvatar() { return customerAvatar; }
        public String getLastMessage() { return lastMessage; }
        public String getLastMessageTime() { return lastMessageTime; }
        public Integer getUnreadCount() { return unreadCount != null ? unreadCount : 0; }
        public String getStatus() { return status; }
    }

    public static class ChatMessage {
        private Long id;
        private Long conversationId;
        private Long senderId;
        private String senderName;
        private String senderAvatar;
        private String senderRole;
        private String content;
        private String messageType;
        private Integer isRead;
        private String createdAt;
        public Long getId() { return id; }
        public Long getConversationId() { return conversationId; }
        public Long getSenderId() { return senderId; }
        public String getSenderName() { return senderName; }
        public String getSenderAvatar() { return senderAvatar; }
        public String getSenderRole() { return senderRole; }
        public String getContent() { return content; }
        public String getMessageType() { return messageType; }
        public Integer getIsRead() { return isRead; }
        public String getCreatedAt() { return createdAt; }
    }

    public static class ChatMessagesResponse {
        private List<ChatMessage> records;
        private Long total;
        private Integer page;
        private Integer pageSize;
        private ChatConversation conversation;
        public List<ChatMessage> getRecords() { return records; }
        public Long getTotal() { return total; }
        public Integer getPage() { return page; }
        public ChatConversation getConversation() { return conversation; }
    }

    public static class ChatSendRequest {
        private Long conversationId;
        private String content;
        public ChatSendRequest(String content) { this.content = content; }
        public ChatSendRequest(Long conversationId, String content) { this.conversationId = conversationId; this.content = content; }
    }

    public static class InstallationInfo {
        private Integer needInstallation;
        private String status;
        private String customerExpressCompany;
        private String customerTrackingNo;
        private String video;
        private String images;
        private String remark;
        private String completedAt;
        public Integer getNeedInstallation() { return needInstallation; }
        public String getStatus() { return status; }
        public String getCustomerExpressCompany() { return customerExpressCompany; }
        public String getCustomerTrackingNo() { return customerTrackingNo; }
        public String getVideo() { return video; }
        public String getImages() { return images; }
        public String getRemark() { return remark; }
        public String getCompletedAt() { return completedAt; }
    }

    public static class MerchantAddress {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        public String getReceiverName() { return receiverName; }
        public String getReceiverPhone() { return receiverPhone; }
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getDistrict() { return district; }
        public String getDetailAddress() { return detailAddress; }
        public String getFullAddress() {
            return (province != null ? province : "") + (city != null ? city : "") + (district != null ? district : "") + " " + (detailAddress != null ? detailAddress : "");
        }
    }

    public static class Purchase {
        @SerializedName("id_zj") private Long id;
        @SerializedName("order_no_zj") private String orderNo;
        @SerializedName("factory_id_zj") private Long factoryId;
        @SerializedName("total_amount_zj") private Double totalAmount;
        @SerializedName("status_zj") private String status;
        @SerializedName("expected_delivery_date_zj") private String expectedDeliveryDate;
        @SerializedName("express_company_zj") private String expressCompany;
        @SerializedName("tracking_no_zj") private String trackingNo;
        @SerializedName("created_at_zj") private String createdAt;
        @SerializedName("updated_at_zj") private String updatedAt;
        public Long getId() { return id; }
        public String getOrderNo() { return orderNo; }
        public Long getFactoryId() { return factoryId; }
        public Double getTotalAmount() { return totalAmount; }
        public String getStatus() { return status; }
        public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
        public String getExpressCompany() { return expressCompany; }
        public String getTrackingNo() { return trackingNo; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }

    public static class PurchaseItem {
        @SerializedName("id_zj") private Long id;
        @SerializedName("purchase_id_zj") private Long purchaseId;
        @SerializedName("product_id_zj") private Long productId;
        @SerializedName("product_name_zj") private String productName;
        @SerializedName("spec_zj") private String spec;
        @SerializedName("quantity_zj") private Integer quantity;
        @SerializedName("unit_price_zj") private Double unitPrice;
        @SerializedName("subtotal_zj") private Double subtotal;
        public Long getId() { return id; }
        public Long getPurchaseId() { return purchaseId; }
        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public String getSpec() { return spec; }
        public Integer getQuantity() { return quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public Double getSubtotal() { return subtotal; }
    }

    public static class PurchaseDetailResponse {
        private Purchase purchase;
        private List<PurchaseItem> items;
        public Purchase getPurchase() { return purchase; }
        public List<PurchaseItem> getItems() { return items; }
    }
}
