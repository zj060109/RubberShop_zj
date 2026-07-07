package com.rubbershop.app.ui.factory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;
import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PurchaseFragment extends Fragment {
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private View emptyView;
    private TextView tvEmpty;
    private Repository repository;
    private PurchaseAdapter adapter;
    private String currentStatus = "";

    private static final String[] STATUS_LABELS = {"全部", "待报价", "已报价", "已付款", "已发货", "已收货"};
    private static final String[] STATUS_VALUES = {"", "pending", "quoted", "paid", "shipped", "received"};

    public PurchaseFragment() { super(R.layout.fragment_factory_purchase); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();

        tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView = view.findViewById(R.id.rv_purchases);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        emptyView = view.findViewById(R.id.tv_empty);

        for (String label : STATUS_LABELS) tabLayout.addTab(tabLayout.newTab().setText(label));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PurchaseAdapter();
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                currentStatus = STATUS_VALUES[tab.getPosition()];
                loadData();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        swipeRefresh.setOnRefreshListener(this::loadData);
        loadData();
    }

    private void loadData() {
        swipeRefresh.setRefreshing(true);
        repository.getPurchases(1, 200, currentStatus, new Repository.ResultCallback<PageResponse<Purchase>>() {
            @Override
            public void onSuccess(PageResponse<Purchase> data) {
                swipeRefresh.setRefreshing(false);
                List<Purchase> purchases = data.getRecords();
                if (purchases == null || purchases.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setData(purchases);
                }
            }

            @Override
            public void onError(String msg) {
                swipeRefresh.setRefreshing(false);
                Utils.toast(getContext(), msg);
            }
        });
    }

    private class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {
        private List<Purchase> data = new ArrayList<>();

        void setData(List<Purchase> data) { this.data = data; notifyDataSetChanged(); }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Purchase p = data.get(position);
            holder.tvOrderNo.setText(p.getOrderNo() != null ? p.getOrderNo() : "PUR-" + p.getId());
            holder.tvAmount.setText(Utils.toPrice(p.getTotalAmount()));
            holder.tvDate.setText("创建：" + Utils.toDateString(p.getCreatedAt()));

            String status = p.getStatus();
            holder.tvStatus.setText(Utils.getPurchaseStatusLabel(status));
            switch (status != null ? status : "") {
                case "pending": holder.tvStatus.setBackgroundColor(0xFF6366f1); break;
                case "quoted": holder.tvStatus.setBackgroundColor(0xFFf59e0b); break;
                case "paid": holder.tvStatus.setBackgroundColor(0xFF3b82f6); break;
                case "shipped": holder.tvStatus.setBackgroundColor(0xFF10b981); break;
                case "received": holder.tvStatus.setBackgroundColor(0xFF059669); break;
                default: holder.tvStatus.setBackgroundColor(0xFF94a3b8); break;
            }

            holder.llActions.setVisibility(View.GONE);
            holder.btnAction1.setVisibility(View.GONE);
            holder.btnAction2.setVisibility(View.GONE);

            if ("pending".equals(status)) {
                holder.llActions.setVisibility(View.VISIBLE);
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setText("报价");
                holder.btnAction1.setOnClickListener(v -> showQuoteDialog(p.getId()));
            } else if ("paid".equals(status)) {
                holder.llActions.setVisibility(View.VISIBLE);
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction1.setText("发货");
                holder.btnAction1.setOnClickListener(v -> showLogisticsDialog(p.getId()));
            }

            holder.itemView.setOnClickListener(v -> loadDetail(p.getId()));
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvOrderNo, tvStatus, tvAmount, tvDate;
            LinearLayout llActions;
            com.google.android.material.button.MaterialButton btnAction1, btnAction2;

            ViewHolder(View v) {
                super(v);
                tvOrderNo = v.findViewById(R.id.tv_order_no);
                tvStatus = v.findViewById(R.id.tv_status);
                tvAmount = v.findViewById(R.id.tv_amount);
                tvDate = v.findViewById(R.id.tv_date);
                llActions = v.findViewById(R.id.ll_actions);
                btnAction1 = v.findViewById(R.id.btn_action1);
                btnAction2 = v.findViewById(R.id.btn_action2);
            }
        }
    }

    private void showQuoteDialog(Long purchaseId) {
        repository.getPurchaseDetail(purchaseId, new Repository.ResultCallback<PurchaseDetailResponse>() {
            @Override
            public void onSuccess(PurchaseDetailResponse resp) {
                java.util.List<PurchaseItem> items = resp.getItems();
                if (items == null || items.isEmpty()) {
                    Utils.toast(getContext(), "暂无明细");
                    return;
                }
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(32, 16, 32, 0);

                java.util.List<android.widget.EditText> priceInputs = new java.util.ArrayList<>();
                for (PurchaseItem item : items) {
                    TextView tv = new TextView(getContext());
                    tv.setText(item.getProductName() + " " + (item.getSpec() != null ? item.getSpec() : "") + " ×" + item.getQuantity());
                    tv.setTextSize(13);
                    tv.setPadding(0, 8, 0, 4);
                    layout.addView(tv);

                    EditText et = new EditText(getContext());
                    et.setHint("单价（元）");
                    et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    et.setTag(item.getId()); // store item ID
                    if (item.getUnitPrice() != null && item.getUnitPrice() > 0) {
                        et.setText(String.format("%.2f", item.getUnitPrice()));
                    }
                    priceInputs.add(et);
                    layout.addView(et);
                }

                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setTitle("报价")
                    .setView(layout)
                    .setPositiveButton("提交报价", (dialog, which) -> {
                        java.util.List<java.util.Map<String, Object>> quoteItems = new java.util.ArrayList<>();
                        for (EditText et : priceInputs) {
                            String priceStr = et.getText().toString().trim();
                            if (priceStr.isEmpty()) { Utils.toast(getContext(), "请填写所有单价"); return; }
                            Long itemId = (Long) et.getTag();
                            java.util.Map<String, Object> qi = new java.util.HashMap<>();
                            qi.put("itemId", itemId);
                            qi.put("unitPrice", Double.parseDouble(priceStr));
                            quoteItems.add(qi);
                        }
                        java.util.Map<String, Object> body = new java.util.HashMap<>();
                        body.put("items", quoteItems);
                        repository.quotePurchase(purchaseId, body, new Repository.ResultCallback<Object>() {
                            @Override public void onSuccess(Object data) { Utils.toast(getContext(), "报价成功"); loadData(); }
                            @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
                        });
                    })
                    .setNegativeButton("取消", null)
                    .show();
            }
            @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
        });
    }

    private void showLogisticsDialog(Long purchaseId) {
        android.widget.EditText inputExpress = new android.widget.EditText(getContext());
        inputExpress.setHint("快递公司");
        android.widget.EditText inputTracking = new android.widget.EditText(getContext());
        inputTracking.setHint("快递单号");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 16, 48, 0);
        layout.addView(inputExpress);
        layout.addView(inputTracking);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
            .setTitle("填写物流信息")
            .setView(layout)
            .setPositiveButton("发货", (dialog, which) -> {
                String express = inputExpress.getText().toString().trim();
                String tracking = inputTracking.getText().toString().trim();
                if (express.isEmpty() || tracking.isEmpty()) {
                    Utils.toast(getContext(), "请填写完整信息");
                    return;
                }
                repository.updatePurchaseLogistics(purchaseId, express, tracking, new Repository.ResultCallback<Object>() {
                    @Override public void onSuccess(Object data) {
                        Utils.toast(getContext(), "发货成功");
                        loadData();
                    }
                    @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
                });
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void loadDetail(Long purchaseId) {
        repository.getPurchaseDetail(purchaseId, new Repository.ResultCallback<PurchaseDetailResponse>() {
            @Override
            public void onSuccess(PurchaseDetailResponse resp) {
                Purchase p = resp.getPurchase();
                if (p == null) { Utils.toast(getContext(), "采购单不存在"); return; }
                java.util.List<PurchaseItem> items = resp.getItems();

                StringBuilder sb = new StringBuilder();
                sb.append("单号：").append(p.getOrderNo()).append("\n");
                sb.append("金额：¥").append(String.format("%.2f", p.getTotalAmount() != null ? p.getTotalAmount() : 0))
                  .append("\n\n--- 明细 ---\n");
                if (items != null) {
                    for (PurchaseItem item : items) {
                        sb.append(item.getProductName()).append(" ×").append(item.getQuantity())
                          .append("  ¥").append(String.format("%.2f", item.getSubtotal() != null ? item.getSubtotal() : 0))
                          .append("\n");
                    }
                }

                new com.google.android.material.dialog.MaterialAlertDialogBuilder(getContext())
                    .setTitle("采购单详情")
                    .setMessage(sb.toString())
                    .setPositiveButton("确定", null)
                    .show();
            }
            @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
        });
    }
}
