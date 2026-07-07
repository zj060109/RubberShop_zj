package com.rubbershop.app.ui.product;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.rubbershop.app.R;
import com.rubbershop.app.data.local.CartManager;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.Collections;

public class ProductDetailFragment extends Fragment {
    private TextView tvName, tvPrice, tvStock, tvDesc, tvQty;
    private ImageView ivProduct;
    private ImageButton btnMinus, btnPlus;
    private Button btnBalance, btnCredit, btnAddCart;
    private ProgressBar progressBar;
    private Repository repository;
    private long productId;
    private int quantity = 1;
    private Product product;
    private String pendingPaymentMethod = null;

    public ProductDetailFragment() { super(R.layout.fragment_product_detail); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        productId = getArguments() != null ? getArguments().getLong("productId") : 0;

        tvName = view.findViewById(R.id.tv_name); tvPrice = view.findViewById(R.id.tv_price);
        tvStock = view.findViewById(R.id.tv_stock); tvDesc = view.findViewById(R.id.tv_desc);
        tvQty = view.findViewById(R.id.tv_qty); ivProduct = view.findViewById(R.id.iv_product);
        btnMinus = view.findViewById(R.id.btn_minus); btnPlus = view.findViewById(R.id.btn_plus);
        btnBalance = view.findViewById(R.id.btn_buy_balance);
        btnCredit = view.findViewById(R.id.btn_buy_credit);
        btnAddCart = view.findViewById(R.id.btn_add_cart);
        progressBar = view.findViewById(R.id.progress);

        updateQtyDisplay();
        btnMinus.setOnClickListener(v -> { if (quantity > 1) { quantity--; updateQtyDisplay(); } });
        btnPlus.setOnClickListener(v -> { quantity++; updateQtyDisplay(); });
        btnAddCart.setOnClickListener(v -> {
            if (product != null) { CartManager.getInstance().add(product, quantity); Utils.toast(getContext(), "已加入购物车"); }
        });
        btnBalance.setOnClickListener(v -> showAddressSheet("balance"));
        btnCredit.setOnClickListener(v -> showAddressSheet("credit"));
        loadProduct();
    }

    private void updateQtyDisplay() { tvQty.setText(String.valueOf(quantity)); }

    private void loadProduct() {
        if (!isAdded()) return;
        progressBar.setVisibility(View.VISIBLE);
        repository.getProductDetail(productId, new Repository.ResultCallback<Product>() {
            @Override public void onSuccess(Product p) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                product = p;
                tvName.setText(p.getName()); tvPrice.setText(Utils.toPrice(p.getPrice()));
                tvStock.setText("库存: " + p.getStock());
                tvDesc.setText(p.getDescription() != null ? p.getDescription() : "暂无描述");
                String imgUrl = Utils.getFirstImageUrl(p.getImages());
                if (imgUrl != null) Glide.with(ProductDetailFragment.this).load(imgUrl).placeholder(R.drawable.bg_avatar).into(ivProduct);
            }
            @Override public void onError(String message) {
                if (!isAdded()) return; progressBar.setVisibility(View.GONE); Utils.toast(getContext(), message);
            }
        });
    }

    private void showAddressSheet(String paymentMethod) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = getLayoutInflater().inflate(R.layout.sheet_address, null);
        TextInputEditText etName = sheet.findViewById(R.id.et_name);
        TextInputEditText etPhone = sheet.findViewById(R.id.et_phone);
        TextInputEditText etProvince = sheet.findViewById(R.id.et_province);
        TextInputEditText etCity = sheet.findViewById(R.id.et_city);
        TextInputEditText etDistrict = sheet.findViewById(R.id.et_district);
        TextInputEditText etAddress = sheet.findViewById(R.id.et_address);
        CheckBox cbInstallation = sheet.findViewById(R.id.cb_installation);
        CheckBox cbInsurance = sheet.findViewById(R.id.cb_insurance);
        TextView tvInsurance = sheet.findViewById(R.id.tv_insurance);
        TextView tvTotal = sheet.findViewById(R.id.tv_total);
        Button btnSubmit = sheet.findViewById(R.id.btn_submit);

        double total = (product != null ? product.getPrice() : 0) * quantity;
        tvTotal.setText("应付：" + Utils.toPrice(total) + (paymentMethod.equals("credit") ? " (赊账)" : ""));

        // Insurance prompt for orders over ¥500
        final double INSURANCE_THRESHOLD = 500.0;
        if (total >= INSURANCE_THRESHOLD) {
            double insuranceFee = Math.max(total * 0.01, 5.0);
            tvInsurance.setVisibility(View.VISIBLE);
            tvInsurance.setText("订单金额超过 ¥" + (int)INSURANCE_THRESHOLD + "，建议购买快递保价（¥" + String.format("%.2f", insuranceFee) + "）保障物流安全");
            cbInsurance.setVisibility(View.VISIBLE);
            cbInsurance.setText("购买快递保价（费率1%，费用 ¥" + String.format("%.2f", insuranceFee) + "）");
        }

        repository.getProfile(new Repository.ResultCallback<UserProfile>() {
            @Override public void onSuccess(UserProfile p) {
                etName.setText(p.getReceiverName()); etPhone.setText(p.getReceiverPhone());
                etProvince.setText(p.getProvince()); etCity.setText(p.getCity());
                etDistrict.setText(p.getDistrict()); etAddress.setText(p.getDetailAddress());
            }
            @Override public void onError(String m) {}
        });

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String addr = etAddress.getText().toString().trim();
            if (name.isEmpty() || phone.isEmpty() || addr.isEmpty()) {
                Utils.toast(getContext(), "请填写完整的收货信息"); return;
            }
            dialog.dismiss();
            boolean needInstallation = cbInstallation.isChecked();
            boolean needInsurance = cbInsurance.isChecked();
            placeOrder(paymentMethod, name, phone, etProvince.getText().toString().trim(),
                    etCity.getText().toString().trim(), etDistrict.getText().toString().trim(), addr,
                    needInstallation, needInsurance, total);
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void placeOrder(String pm, String name, String phone, String province, String city, String district, String addr,
                            boolean needInstallation, boolean needInsurance, double orderTotal) {
        if (!isAdded()) return;

        if (needInsurance) {
            double insuranceFee = Math.max(orderTotal * 0.01, 5.0);
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("快递保价确认")
                .setMessage("保价费用 ¥" + String.format("%.2f", insuranceFee) + " 将随订单一同支付。\n快递丢失可获全额赔付。")
                .setPositiveButton("确认购买", (d, w) -> submitOrder(pm, name, phone, province, city, district, addr, needInstallation))
                .setNegativeButton("不需要", (d, w) -> submitOrder(pm, name, phone, province, city, district, addr, needInstallation))
                .show();
        } else {
            submitOrder(pm, name, phone, province, city, district, addr, needInstallation);
        }
    }

    private void submitOrder(String pm, String name, String phone, String province, String city, String district, String addr,
                             boolean needInstallation) {
        if (!isAdded()) return;
        progressBar.setVisibility(View.VISIBLE);
        OrderCreateRequest req = new OrderCreateRequest(
                Collections.singletonList(new OrderItemRequest(productId, quantity)), pm);
        req.receiverName = name; req.receiverPhone = phone;
        req.province = province; req.city = city; req.district = district; req.detailAddress = addr;
        req.needInstallation = needInstallation ? 1 : 0;
        repository.createOrder(req, new Repository.ResultCallback<Order>() {
            @Override public void onSuccess(Order data) {
                if (!isAdded()) return; progressBar.setVisibility(View.GONE); Utils.toast(getContext(), "下单成功");
            }
            @Override public void onError(String message) {
                if (!isAdded()) return; progressBar.setVisibility(View.GONE); Utils.toast(getContext(), message);
            }
        });
    }
}
