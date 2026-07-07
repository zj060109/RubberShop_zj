package com.rubbershop.app.ui.custom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.Customization;
import com.rubbershop.app.data.model.Models.PageResponse;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CustomListFragment extends Fragment {
    private RecyclerView rvList;
    private Button btnCreate;
    private View tvEmpty;
    private Repository repository;
    private CustomAdapter adapter;
    private List<Customization> items = new ArrayList<>();

    public CustomListFragment() { super(R.layout.fragment_custom_list); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        rvList = view.findViewById(R.id.rv_list);
        btnCreate = view.findViewById(R.id.btn_create);
        tvEmpty = view.findViewById(R.id.tv_empty);

        adapter = new CustomAdapter(c -> {
            Bundle args = new Bundle();
            args.putLong("customId", c.getId());
            Navigation.findNavController(view).navigate(R.id.customDetailFragment, args);
        });
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(adapter);

        btnCreate.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.customCreateFragment));
        loadData();
    }

    private void loadData() {
        repository.getCustomizations(1, 50, null, new Repository.ResultCallback<PageResponse<Customization>>() {
            @Override public void onSuccess(PageResponse<Customization> data) {
                items.clear();
                if (data.getRecords() != null) items.addAll(data.getRecords());
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String msg) { Utils.toast(getContext(), msg); }
        });
    }

    interface OnCustomClick { void onClick(Customization c); }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.VH> {
        private final OnCustomClick listener;
        CustomAdapter(OnCustomClick l) { this.listener = l; }
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            Customization c = items.get(pos);
            holder.tvStatus.setText(Utils.getCustomStatusLabel(c.getStatus()));
            holder.tvDesc.setText(c.getDescription() != null && c.getDescription().length() > 60 ? c.getDescription().substring(0, 60) : c.getDescription());
            holder.tvTime.setText(Utils.toShortDateString(c.getCreatedAt()));
            holder.itemView.setOnClickListener(v -> listener.onClick(c));
        }
        @Override public int getItemCount() { return items.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvStatus, tvDesc, tvTime;
            VH(View v) { super(v); tvStatus = v.findViewById(R.id.tv_status); tvDesc = v.findViewById(R.id.tv_desc); tvTime = v.findViewById(R.id.tv_time); }
        }
    }
}
