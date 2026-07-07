package com.rubbershop.app.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.Order;
import com.rubbershop.app.data.model.Models.PageResponse;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {
    private RecyclerView rvOrders;
    private TabLayout tabLayout;
    private View tvEmpty;
    private Repository repository;
    private OrderAdapter adapter;
    private List<Order> orders = new ArrayList<>();
    private String currentStatus = null;

    public OrderListFragment() { super(R.layout.fragment_order_list); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        rvOrders = view.findViewById(R.id.rv_orders);
        tabLayout = view.findViewById(R.id.tab_layout);
        tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new OrderAdapter(o -> {
            Bundle args = new Bundle();
            args.putLong("orderId", o.getId());
            Navigation.findNavController(view).navigate(R.id.orderDetailFragment, args);
        });
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrders.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("待接单"));
        tabLayout.addTab(tabLayout.newTab().setText("运输中"));
        tabLayout.addTab(tabLayout.newTab().setText("已完成"));
        tabLayout.addTab(tabLayout.newTab().setText("已取消"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1: currentStatus = "paid"; break;
                    case 2: currentStatus = "shipped"; break;
                    case 3: currentStatus = "completed"; break;
                    case 4: currentStatus = "cancelled"; break;
                    default: currentStatus = null; break;
                }
                loadOrders();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadOrders();
    }

    private void loadOrders() {
        repository.getOrders(1, 50, currentStatus, new Repository.ResultCallback<PageResponse<Order>>() {
            @Override public void onSuccess(PageResponse<Order> data) {
                orders.clear();
                if (data.getRecords() != null) orders.addAll(data.getRecords());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                Utils.toast(getContext(), message);
            }
        });
    }

    interface OnOrderClick { void onClick(Order o); }

    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {
        private final OnOrderClick listener;
        OrderAdapter(OnOrderClick l) { this.listener = l; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            Order o = orders.get(pos);
            holder.tvOrderNo.setText(o.getOrderNo() != null && o.getOrderNo().length() > 8 ? o.getOrderNo().substring(o.getOrderNo().length() - 8) : o.getOrderNo());
            holder.tvStatus.setText(Utils.getOrderStatusLabel(o.getStatus()));
            holder.tvAmount.setText(Utils.toPrice(o.getActualAmount()));
            holder.tvTime.setText(Utils.toShortDateString(o.getCreatedAt()));
            holder.itemView.setOnClickListener(v -> listener.onClick(o));
        }
        @Override public int getItemCount() { return orders.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvOrderNo, tvStatus, tvAmount, tvTime;
            VH(View v) { super(v); tvOrderNo = v.findViewById(R.id.tv_order_no); tvStatus = v.findViewById(R.id.tv_status); tvAmount = v.findViewById(R.id.tv_amount); tvTime = v.findViewById(R.id.tv_time); }
        }
    }
}
