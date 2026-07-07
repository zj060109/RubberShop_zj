package com.rubbershop.app.ui.order;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.List;

public class OrderDetailFragment extends Fragment {
    private TextView tvOrderNo, tvStatus, tvAmount, tvPayment, tvReceiver, tvAddress, tvExpress;
    private TextView tvPaidTime, tvShipTime, tvFinishTime;
    private LinearLayout llItems, llTimeline;
    private Button btnReceive, btnCancel;
    private ProgressBar progressBar;
    private Repository repository;
    private long orderId;

    public OrderDetailFragment() { super(R.layout.fragment_order_detail); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        orderId = getArguments() != null ? getArguments().getLong("orderId") : 0;

        tvOrderNo = view.findViewById(R.id.tv_order_no);
        tvStatus = view.findViewById(R.id.tv_status);
        tvAmount = view.findViewById(R.id.tv_amount);
        tvPayment = view.findViewById(R.id.tv_payment);
        tvReceiver = view.findViewById(R.id.tv_receiver);
        tvAddress = view.findViewById(R.id.tv_address);
        tvExpress = view.findViewById(R.id.tv_express);
        tvPaidTime = view.findViewById(R.id.tv_paid_time);
        tvShipTime = view.findViewById(R.id.tv_ship_time);
        tvFinishTime = view.findViewById(R.id.tv_finish_time);
        llItems = view.findViewById(R.id.ll_items);
        llTimeline = view.findViewById(R.id.ll_timeline);
        btnReceive = view.findViewById(R.id.btn_receive);
        btnCancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress);

        btnReceive.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            repository.receiveOrder(orderId, new Repository.ResultCallback<Object>() {
                @Override public void onSuccess(Object data) { Utils.toast(getContext(), "已确认收货"); loadDetail(); }
                @Override public void onError(String msg) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), msg); }
            });
        });

        btnCancel.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            repository.cancelOrder(orderId, new Repository.ResultCallback<Object>() {
                @Override public void onSuccess(Object data) { Utils.toast(getContext(), "订单已取消"); loadDetail(); }
                @Override public void onError(String msg) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), msg); }
            });
        });

        loadDetail();
    }

    private void loadDetail() {
        progressBar.setVisibility(View.VISIBLE);
        repository.getOrderDetail(orderId, new Repository.ResultCallback<OrderDetailResponse>() {
            @Override public void onSuccess(OrderDetailResponse resp) {
                progressBar.setVisibility(View.GONE);
                Order o = resp.getOrder();
                if (o == null) return;

                tvOrderNo.setText(o.getOrderNo());
                tvStatus.setText(Utils.getOrderStatusLabel(o.getStatus()));
                tvAmount.setText(Utils.toPrice(o.getActualAmount()));
                tvPayment.setText("credit".equals(o.getPaymentMethod()) ? "赊账" : "余额");
                tvReceiver.setText((o.getReceiverName() != null ? o.getReceiverName() : "-") + " " + (o.getReceiverPhone() != null ? o.getReceiverPhone() : ""));
                tvAddress.setText(o.getProvince() + " " + o.getCity() + " " + o.getDistrict() + " " + o.getDetailAddress());
                tvExpress.setText(o.getExpressCompany() != null ? o.getExpressCompany() + " " + o.getTrackingNo() : "暂无");
                tvPaidTime.setText(Utils.toDateString(o.getPaidAt()));
                tvShipTime.setText(Utils.toDateString(o.getShippedAt()));
                tvFinishTime.setText(Utils.toDateString(o.getFinishedAt()));

                llItems.removeAllViews();
                List<OrderItem> items = resp.getItems();
                if (items != null) {
                    for (OrderItem item : items) {
                        TextView tv = new TextView(getContext());
                        tv.setText(item.getProductName() + " x" + item.getQuantity() + "  " + Utils.toPrice(item.getSubtotal()));
                        tv.setPadding(0, 4, 0, 4);
                        llItems.addView(tv);
                    }
                }

                llTimeline.removeAllViews();
                List<OrderStatusLog> logs = resp.getStatusLogs();
                if (logs != null) {
                    for (OrderStatusLog log : logs) {
                        TextView tv = new TextView(getContext());
                        tv.setText(Utils.toShortDateString(log.getCreatedAt()) + "  " + Utils.getOrderStatusLabel(log.getToStatus()) + "  " + (log.getRemark() != null ? log.getRemark() : ""));
                        tv.setPadding(0, 4, 0, 4);
                        tv.setTextColor(getResources().getColor(R.color.gray_dark, null));
                        llTimeline.addView(tv);
                    }
                }

                btnReceive.setVisibility("shipped".equals(o.getStatus()) ? View.VISIBLE : View.GONE);
                String st = o.getStatus();
                btnCancel.setVisibility(("paid".equals(st) || "accepted".equals(st) || "shipped_to_merchant".equals(st)) ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String msg) {
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), msg);
            }
        });
    }
}
