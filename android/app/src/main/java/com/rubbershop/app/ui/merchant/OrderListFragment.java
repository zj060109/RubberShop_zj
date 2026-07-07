package com.rubbershop.app.ui.merchant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.*;

public class OrderListFragment extends Fragment {
    private RecyclerView rvList;
    private TabLayout tabLayout;
    private View tvEmpty;
    private Repository repository;
    private boolean isCustomMode = false;
    private String currentStatus = null;
    private OrderAdapter orderAdapter;
    private CustomAdapter customAdapter;
    private final List<Order> orders = new ArrayList<>();
    private final List<Customization> customizations = new ArrayList<>();

    public OrderListFragment() { super(R.layout.fragment_merchant_order_list); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new Repository();
        rvList = v.findViewById(R.id.rv_list);
        tabLayout = v.findViewById(R.id.tab_layout);
        tvEmpty = v.findViewById(R.id.tv_empty);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));

        orderAdapter = new OrderAdapter(o -> {
            Bundle args = new Bundle(); args.putLong("orderId", o.getId());
            Navigation.findNavController(v).navigate(R.id.orderDetailFragment, args);
        });
        customAdapter = new CustomAdapter(c -> {
            Bundle args = new Bundle(); args.putLong("customId", c.getId());
            Navigation.findNavController(v).navigate(R.id.customDetailFragment, args);
        });
        rvList.setAdapter(orderAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("普通订单"));
        tabLayout.addTab(tabLayout.newTab().setText("定制订单"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab t) {
                isCustomMode = t.getPosition() == 1;
                if (isCustomMode) {
                    rvList.setAdapter(customAdapter);
                    loadCustoms();
                } else {
                    rvList.setAdapter(orderAdapter);
                    loadOrders();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });
        loadOrders();
    }

    private void loadOrders() {
        repository.getOrders(1, 20, currentStatus, new Repository.ResultCallback<PageResponse<Order>>() {
            @Override public void onSuccess(PageResponse<Order> r) {
                orders.clear();
                if (r.getRecords() != null) orders.addAll(r.getRecords());
                orderAdapter.notifyDataSetChanged();
                tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String m) { Utils.toast(getContext(), m); }
        });
    }

    private void loadCustoms() {
        repository.getCustomizations(1, 50, currentStatus, new Repository.ResultCallback<PageResponse<Customization>>() {
            @Override public void onSuccess(PageResponse<Customization> r) {
                customizations.clear();
                if (r.getRecords() != null) customizations.addAll(r.getRecords());
                customAdapter.notifyDataSetChanged();
                tvEmpty.setVisibility(customizations.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String m) { Utils.toast(getContext(), m); }
        });
    }

    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {
        private final OnOrderClick l;
        OrderAdapter(OnOrderClick l) { this.l = l; }
        @Override public VH onCreateViewHolder(ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_order_card, p, false)); }
        @Override public void onBindViewHolder(VH h, int p) {
            Order o = orders.get(p);
            h.tvNo.setText(o.getOrderNo()); h.tvStatus.setText(Utils.getOrderStatusLabel(o.getStatus()));
            h.tvAmount.setText(Utils.toPrice(o.getActualAmount())); h.tvTime.setText(Utils.toShortDateString(o.getCreatedAt()));
            h.itemView.setOnClickListener(v -> l.onClick(o));
            boolean showAccept = "paid".equals(o.getStatus());
            boolean showShip = "accepted".equals(o.getStatus());
            h.btnAccept.setVisibility(showAccept ? View.VISIBLE : View.GONE);
            h.btnShip.setVisibility(showShip ? View.VISIBLE : View.GONE);
            h.btnAccept.setOnClickListener(v -> repository.updateOrderStatus(o.getId(), "accepted", new Repository.ResultCallback<Object>() {
                @Override public void onSuccess(Object d) { loadOrders(); } @Override public void onError(String m) { Utils.toast(getContext(), m); }
            }));
            h.btnShip.setOnClickListener(v -> showShipSheet(o));
        }
        @Override public int getItemCount() { return orders.size(); }
        class VH extends RecyclerView.ViewHolder { TextView tvNo, tvStatus, tvAmount, tvTime; Button btnAccept, btnShip;
            VH(View v) { super(v); tvNo=v.findViewById(R.id.tv_no); tvStatus=v.findViewById(R.id.tv_status); tvAmount=v.findViewById(R.id.tv_amount); tvTime=v.findViewById(R.id.tv_time); btnAccept=v.findViewById(R.id.btn_accept); btnShip=v.findViewById(R.id.btn_ship); }
        }
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.VH> {
        private final OnCustomClick l;
        CustomAdapter(OnCustomClick l) { this.l = l; }
        @Override public VH onCreateViewHolder(ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_custom, p, false)); }
        @Override public void onBindViewHolder(VH h, int p) {
            Customization c = customizations.get(p);
            h.tvStatus.setText(Utils.getCustomStatusLabel(c.getStatus()));
            h.tvDesc.setText(c.getDescription() != null && c.getDescription().length() > 60 ? c.getDescription().substring(0, 60) : c.getDescription());
            h.tvTime.setText(Utils.toShortDateString(c.getCreatedAt()));
            h.itemView.setOnClickListener(v -> l.onClick(c));
        }
        @Override public int getItemCount() { return customizations.size(); }
        class VH extends RecyclerView.ViewHolder { TextView tvStatus, tvDesc, tvTime;
            VH(View v) { super(v); tvStatus=v.findViewById(R.id.tv_status); tvDesc=v.findViewById(R.id.tv_desc); tvTime=v.findViewById(R.id.tv_time); }
        }
    }

    interface OnOrderClick { void onClick(Order o); }
    interface OnCustomClick { void onClick(Customization c); }

    private void showShipSheet(Order o) {
        BottomSheetDialog d = new BottomSheetDialog(requireContext());
        View sv = LayoutInflater.from(getContext()).inflate(R.layout.sheet_ship, null);
        EditText etCompany = sv.findViewById(R.id.et_company), etNo = sv.findViewById(R.id.et_no);
        Button btn = sv.findViewById(R.id.btn_submit);
        btn.setOnClickListener(v -> {
            String c = etCompany.getText().toString().trim(), n = etNo.getText().toString().trim();
            if (c.isEmpty() || n.isEmpty()) { Utils.toast(getContext(), "请填写完整"); return; }
            repository.shipOrder(o.getId(), c, n, new Repository.ResultCallback<Object>() {
                @Override public void onSuccess(Object obj) { Utils.toast(getContext(), "已发货"); d.dismiss(); loadOrders(); }
                @Override public void onError(String m) { Utils.toast(getContext(), m); }
            });
        });
        d.setContentView(sv); d.show();
    }
}
