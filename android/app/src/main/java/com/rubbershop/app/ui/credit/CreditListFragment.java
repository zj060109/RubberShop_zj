package com.rubbershop.app.ui.credit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.Receivable;
import com.rubbershop.app.data.model.Models.PageResponse;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreditListFragment extends Fragment {
    private RecyclerView rvList;
    private TabLayout tabLayout;
    private View tvEmpty;
    private Repository repository;
    private CreditAdapter adapter;
    private List<Receivable> receivables = new ArrayList<>();
    private String currentStatus = null;

    public CreditListFragment() { super(R.layout.fragment_credit_list); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        rvList = view.findViewById(R.id.rv_list);
        tabLayout = view.findViewById(R.id.tab_layout);
        tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new CreditAdapter(this::showRepaySheet);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("待还"));
        tabLayout.addTab(tabLayout.newTab().setText("部分还"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1: currentStatus = "unpaid"; break;
                    case 2: currentStatus = "partially_paid"; break;
                    default: currentStatus = null; break;
                }
                loadData();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadData();
    }

    private void loadData() {
        repository.getReceivables(1, 50, currentStatus, new Repository.ResultCallback<PageResponse<Receivable>>() {
            @Override public void onSuccess(PageResponse<Receivable> data) {
                receivables.clear();
                if (data.getRecords() != null) receivables.addAll(data.getRecords());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(receivables.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
        });
    }

    private void showRepaySheet(Receivable r) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheet = LayoutInflater.from(getContext()).inflate(R.layout.sheet_repay, null);
        TextView tvOwed = sheet.findViewById(R.id.tv_owed);
        TextView tvPaid = sheet.findViewById(R.id.tv_paid);
        TextView tvRemaining = sheet.findViewById(R.id.tv_remaining);
        EditText etAmount = sheet.findViewById(R.id.et_amount);
        EditText etRemark = sheet.findViewById(R.id.et_remark);
        Button btnSubmit = sheet.findViewById(R.id.btn_submit);

        double remaining = r.getAmountOwed() - r.getAmountPaid();
        tvOwed.setText("应收: " + Utils.toPrice(r.getAmountOwed()));
        tvPaid.setText("已还: " + Utils.toPrice(r.getAmountPaid()));
        tvRemaining.setText("剩余: " + Utils.toPrice(remaining));
        etAmount.setText(String.valueOf((int) remaining));

        btnSubmit.setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                if (amount <= 0) { Utils.toast(getContext(), "请输入有效金额"); return; }
                if (amount > remaining) { Utils.toast(getContext(), "不能超过剩余欠款"); return; }
                String remark = etRemark.getText().toString().trim();
                repository.repay(r.getId(), amount, "balance", remark.isEmpty() ? null : remark, new Repository.ResultCallback<Object>() {
                    @Override public void onSuccess(Object data) { Utils.toast(getContext(), "还款成功"); dialog.dismiss(); loadData(); }
                    @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
                });
            } catch (NumberFormatException e) { Utils.toast(getContext(), "请输入有效金额"); }
        });
        dialog.setContentView(sheet);
        dialog.show();
    }

    interface OnCreditClick { void onClick(Receivable r); }

    class CreditAdapter extends RecyclerView.Adapter<CreditAdapter.VH> {
        private final OnCreditClick listener;
        CreditAdapter(OnCreditClick l) { this.listener = l; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credit, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            Receivable r = receivables.get(pos);
            holder.tvAmount.setText("应收: " + Utils.toPrice(r.getAmountOwed()));
            holder.tvPaid.setText("已还: " + Utils.toPrice(r.getAmountPaid()));
            holder.tvStatus.setText(Utils.getReceivableStatusLabel(r.getStatus()));
            holder.tvTime.setText(Utils.toShortDateString(r.getCreatedAt()));
            holder.btnRepay.setVisibility(("unpaid".equals(r.getStatus()) || "partially_paid".equals(r.getStatus())) ? View.VISIBLE : View.GONE);
            holder.btnRepay.setOnClickListener(v -> listener.onClick(r));
            holder.itemView.setOnClickListener(v -> listener.onClick(r));
        }
        @Override public int getItemCount() { return receivables.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvAmount, tvPaid, tvStatus, tvTime;
            Button btnRepay;
            VH(View v) { super(v); tvAmount = v.findViewById(R.id.tv_amount); tvPaid = v.findViewById(R.id.tv_paid); tvStatus = v.findViewById(R.id.tv_status); tvTime = v.findViewById(R.id.tv_time); btnRepay = v.findViewById(R.id.btn_repay); }
        }
    }
}
