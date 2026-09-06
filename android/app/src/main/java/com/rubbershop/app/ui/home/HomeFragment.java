package com.rubbershop.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.rubbershop.app.R;
import com.rubbershop.app.data.local.CartManager;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.*;

public class HomeFragment extends Fragment {
    private RecyclerView rvProducts, rvCategories;
    private EditText etSearch;
    private ImageButton btnSearch;
    private SwipeRefreshLayout swipeRefresh;
    private View tvEmpty;
    private ProgressBar progressBar;
    private Repository repository;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private final List<Product> products = new ArrayList<>();
    private final List<CategoryTree> categories = new ArrayList<>();
    private int page = 1;
    private String keyword = null;
    private Long categoryId = null;

    public HomeFragment() { super(R.layout.fragment_home); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        rvProducts = view.findViewById(R.id.rv_products);
        rvCategories = view.findViewById(R.id.rv_categories);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        ImageButton btnCart = view.findViewById(R.id.btn_cart);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        tvEmpty = view.findViewById(R.id.tv_empty);
        progressBar = view.findViewById(R.id.progress);

        productAdapter = new ProductAdapter(p -> {
            if (p.getId() == null) return;
            Bundle args = new Bundle();
            args.putLong("productId", p.getId());
            Navigation.findNavController(view).navigate(R.id.productDetailFragment, args);
        });
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter(c -> {
            categoryId = c != null ? c.getId() : null;
            loadProducts(true);
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        btnSearch.setOnClickListener(v -> { keyword = etSearch.getText().toString().trim(); loadProducts(true); });
        btnCart.setOnClickListener(v -> showCartSheet(view));
        swipeRefresh.setOnRefreshListener(() -> loadProducts(true));

        loadCategories();
        loadProducts(true);
    }

    private void showCartSheet(View parentView) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(getContext()).inflate(R.layout.sheet_cart, null);
        LinearLayout llItems = sheet.findViewById(R.id.ll_items);
        TextView tvTotal = sheet.findViewById(R.id.tv_total);
        Button btnCheckout = sheet.findViewById(R.id.btn_checkout);
        Button btnClear = sheet.findViewById(R.id.btn_clear);

        refreshCartSheet(llItems, tvTotal, btnCheckout);

        btnClear.setOnClickListener(v -> {
            CartManager.getInstance().clear();
            refreshCartSheet(llItems, tvTotal, btnCheckout);
        });

        btnCheckout.setOnClickListener(v -> {
            List<CartManager.CartItem> cartItems = CartManager.getInstance().getItems();
            if (cartItems.isEmpty()) { Utils.toast(getContext(), "购物车为空"); return; }
            dialog.dismiss();
            showCartAddressSheet(cartItems);
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void showCartAddressSheet(List<CartManager.CartItem> cartItems) {
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

        double sum = 0;
        for (CartManager.CartItem ci : cartItems) sum += ci.product.getPrice() * ci.quantity;
        final double total = sum;
        tvTotal.setText("应付：" + Utils.toPrice(total));

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
            List<OrderItemRequest> orderItems = new ArrayList<>();
            for (CartManager.CartItem ci : cartItems)
                orderItems.add(new OrderItemRequest(ci.product.getId(), ci.quantity));
            OrderCreateRequest req = new OrderCreateRequest(orderItems, "balance");
            req.receiverName = name; req.receiverPhone = phone;
            req.province = etProvince.getText().toString().trim();
            req.city = etCity.getText().toString().trim();
            req.district = etDistrict.getText().toString().trim();
            req.detailAddress = addr;
            req.needInstallation = needInstallation ? 1 : 0;

            if (needInsurance) {
                double insuranceFee = Math.max(total * 0.01, 5.0);
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("快递保价确认")
                    .setMessage("保价费用 ¥" + String.format("%.2f", insuranceFee) + " 将随订单一同支付。\n快递丢失可获全额赔付。")
                    .setPositiveButton("确认购买", (d, w) -> submitCartOrder(req))
                    .setNegativeButton("不需要", (d, w) -> submitCartOrder(req))
                    .show();
            } else {
                submitCartOrder(req);
            }
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    private void submitCartOrder(OrderCreateRequest req) {
        repository.createOrder(req, new Repository.ResultCallback<Order>() {
            @Override public void onSuccess(Order data) {
                if (!isAdded()) return;
                Utils.toast(getContext(), "下单成功");
                CartManager.getInstance().clear();
                loadProducts(true);
            }
            @Override public void onError(String msg) { if (!isAdded()) return; Utils.toast(getContext(), msg); }
        });
    }

    private void refreshCartSheet(LinearLayout llItems, TextView tvTotal, Button btnCheckout) {
        llItems.removeAllViews();
        List<CartManager.CartItem> items = CartManager.getInstance().getItems();
        double total = 0;
        for (CartManager.CartItem ci : items) {
            total += ci.product.getPrice() * ci.quantity;
            View row = LayoutInflater.from(getContext()).inflate(R.layout.item_cart_row, llItems, false);
            ((TextView) row.findViewById(R.id.tv_pname)).setText(ci.product.getName());
            ((TextView) row.findViewById(R.id.tv_pqty)).setText("x" + ci.quantity);
            ((TextView) row.findViewById(R.id.tv_psubtotal)).setText(Utils.toPrice(ci.product.getPrice() * ci.quantity));
            final Long pid = ci.product.getId();
            row.findViewById(R.id.btn_remove).setOnClickListener(v -> {
                CartManager.getInstance().removeById(pid);
                refreshCartSheet(llItems, tvTotal, btnCheckout);
            });
            llItems.addView(row);
        }
        tvTotal.setText(Utils.toPrice(total));
        btnCheckout.setEnabled(!items.isEmpty());
    }

    private void loadCategories() {
        repository.getCategories(new Repository.ResultCallback<List<CategoryTree>>() {
            @Override public void onSuccess(List<CategoryTree> data) {
                categories.clear();
                categories.add(null);
                if (data != null) {
                    for (CategoryTree c : data) {
                        categories.add(c);
                        addChildren(c);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }
            @Override public void onError(String message) {}
        });
    }

    private void addChildren(CategoryTree parent) {
        if (parent.getChildren() != null) {
            for (CategoryTree child : parent.getChildren()) {
                categories.add(child);
                addChildren(child);
            }
        }
    }

    private void loadProducts(boolean reset) {
        if (reset) page = 1;
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        repository.getProducts(page, 10, keyword, categoryId, new Repository.ResultCallback<PageResponse<Product>>() {
            @Override public void onSuccess(PageResponse<Product> data) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                if (reset) products.clear();
                if (data.getRecords() != null) products.addAll(data.getRecords());
                productAdapter.notifyDataSetChanged();
                tvEmpty.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Utils.toast(getContext(), message);
            }
        });
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
        private final FragmentCallback<Product> listener;
        ProductAdapter(FragmentCallback<Product> l) { this.listener = l; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            Product p = products.get(pos);
            String brand = p.getBrand() != null ? p.getBrand() + " " : "";
            String model = p.getModel() != null ? p.getModel() : "";
            String spec = p.getSpec() != null ? p.getSpec() : "";
            holder.tvName.setText(brand + model);
            holder.tvSpec.setText(spec);
            holder.tvPrice.setText(Utils.toPrice(p.getPrice()));
            holder.tvStock.setText("库存: " + p.getStock());
            holder.itemView.setOnClickListener(v -> listener.onItem(p));
        }
        @Override public int getItemCount() { return products.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvSpec, tvPrice, tvStock;
            VH(View v) { super(v); tvName = v.findViewById(R.id.tv_name); tvSpec = v.findViewById(R.id.tv_spec); tvPrice = v.findViewById(R.id.tv_price); tvStock = v.findViewById(R.id.tv_stock); }
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private final FragmentCallback<CategoryTree> listener;
        CategoryAdapter(FragmentCallback<CategoryTree> l) { this.listener = l; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            CategoryTree c = categories.get(pos);
            holder.chip.setText(c == null ? "全部" : c.getName());
            holder.chip.setOnClickListener(v -> listener.onItem(c));
        }
        @Override public int getItemCount() { return categories.size(); }
        class VH extends RecyclerView.ViewHolder {
            com.google.android.material.chip.Chip chip;
            VH(View v) { super(v); chip = v.findViewById(R.id.chip); }
        }
    }

    interface FragmentCallback<T> { void onItem(T item); }
}
