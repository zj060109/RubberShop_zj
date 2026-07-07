package com.rubbershop.app.ui.custom;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.Customization;
import com.rubbershop.app.data.model.Models.CustomizationItem;
import com.rubbershop.app.data.model.Models.Order;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.List;

public class CustomDetailFragment extends Fragment {
    private TextView tvDesc, tvStatus, tvTotalPrice;
    private LinearLayout llItems;
    private Button btnBalance, btnCredit, btnCancel;
    private ProgressBar progressBar;
    private Repository repository;
    private long customId;

    public CustomDetailFragment() { super(R.layout.fragment_custom_detail); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        customId = getArguments() != null ? getArguments().getLong("customId") : 0;

        tvDesc = view.findViewById(R.id.tv_desc);
        tvStatus = view.findViewById(R.id.tv_status);
        tvTotalPrice = view.findViewById(R.id.tv_total_price);
        llItems = view.findViewById(R.id.ll_items);
        btnBalance = view.findViewById(R.id.btn_confirm_balance);
        btnCredit = view.findViewById(R.id.btn_confirm_credit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        progressBar = view.findViewById(R.id.progress);

        btnBalance.setOnClickListener(v -> confirm("balance"));
        btnCredit.setOnClickListener(v -> confirm("credit"));
        btnCancel.setOnClickListener(v -> cancelCustom());

        loadDetail();
    }

    private void loadDetail() {
        if (!isAdded()) return;
        progressBar.setVisibility(View.VISIBLE);
        repository.getCustomizationDetail(customId, new Repository.ResultCallback<Customization>() {
            @Override public void onSuccess(Customization c) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                tvDesc.setText(c.getDescription());
                tvStatus.setText(Utils.getCustomStatusLabel(c.getStatus()));
                tvTotalPrice.setText(Utils.toPrice(c.getTotalQuotedPrice()));

                llItems.removeAllViews();
                List<CustomizationItem> items = c.getItems();
                if (items != null) {
                    for (CustomizationItem item : items) {
                        if (!isAdded()) return;
                        TextView tv = new TextView(getContext());
                        tv.setText(item.getProductSpec() + "  x" + item.getQuantity() + "  " + Utils.toPrice(item.getUnitPrice()));
                        tv.setPadding(0, 4, 0, 4);
                        llItems.addView(tv);
                    }
                }

                boolean showConfirm = "quoted".equals(c.getStatus());
                boolean showCancel = !"confirmed".equals(c.getStatus())
                        && !"converted".equals(c.getStatus())
                        && !"cancelled".equals(c.getStatus());
                btnBalance.setVisibility(showConfirm ? View.VISIBLE : View.GONE);
                btnCredit.setVisibility(showConfirm ? View.VISIBLE : View.GONE);
                btnCancel.setVisibility(showCancel ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String msg) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), msg);
            }
        });
    }

    private void cancelCustom() {
        progressBar.setVisibility(View.VISIBLE);
        repository.cancelCustomization(customId, new Repository.ResultCallback<Object>() {
            @Override public void onSuccess(Object data) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), "已取消");
                loadDetail();
            }
            @Override public void onError(String msg) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), msg);
            }
        });
    }

    private void confirm(String paymentMethod) {
        progressBar.setVisibility(View.VISIBLE);
        repository.confirmCustomization(customId, paymentMethod, new Repository.ResultCallback<Order>() {
            @Override public void onSuccess(Order data) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), "定制订单已确认");
                loadDetail();
            }
            @Override public void onError(String msg) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                Utils.toast(getContext(), msg);
            }
        });
    }
}
