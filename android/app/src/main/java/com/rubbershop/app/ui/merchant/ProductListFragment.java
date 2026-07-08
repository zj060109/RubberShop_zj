package com.rubbershop.app.ui.merchant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rubbershop.app.R;
import com.rubbershop.app.data.model.Models.CategoryTree;
import com.rubbershop.app.data.model.Models.Product;
import com.rubbershop.app.data.model.Models.PageResponse;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.util.*;

public class ProductListFragment extends Fragment {
    private RecyclerView rvProducts, rvCategories;
    private EditText etSearch;
    private ImageButton btnSearch;
    private SwipeRefreshLayout swipe;
    private ProgressBar progressBar;
    private View emptyView;
    private Repository repository;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private final List<Product> products = new ArrayList<>();
    private final List<CategoryTree> categories = new ArrayList<>();
    private int page = 1;
    private String keyword = null;
    private Long categoryId = null;

    public ProductListFragment() { super(R.layout.fragment_merchant_product_list); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new Repository();
        rvProducts = v.findViewById(R.id.rv_products);
        rvCategories = v.findViewById(R.id.rv_categories);
        etSearch = v.findViewById(R.id.et_search);
        btnSearch = v.findViewById(R.id.btn_search);
        swipe = v.findViewById(R.id.swipe);
        progressBar = v.findViewById(R.id.progress);
        emptyView = v.findViewById(R.id.empty_view);

        productAdapter = new ProductAdapter();
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);

        categoryAdapter = new CategoryAdapter();
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        btnSearch.setOnClickListener(v2 -> { keyword = etSearch.getText().toString().trim(); loadProducts(true); });
        swipe.setOnRefreshListener(() -> loadProducts(true));
        loadCategories();
        loadProducts(true);
    }

    private void loadProducts(boolean reset) {
        if (reset) page = 1;
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        repository.getProducts(page, 20, keyword, categoryId, new Repository.ResultCallback<PageResponse<Product>>() {
            @Override public void onSuccess(PageResponse<Product> r) {
                progressBar.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                if (reset) products.clear();
                if (r.getRecords() != null) products.addAll(r.getRecords());
                productAdapter.notifyDataSetChanged();
                emptyView.setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String m) {
                progressBar.setVisibility(View.GONE);
                swipe.setRefreshing(false);
                Utils.toast(getContext(), m);
            }
        });
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
            @Override public void onError(String m) {}
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

    class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
        @Override public VH onCreateViewHolder(ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_product, p, false));
        }
        @Override public void onBindViewHolder(VH h, int p) {
            Product o = products.get(p);
            String name = o.getSpec() != null ? o.getSpec() : o.getName();
            h.tvName.setText(name);
            h.tvPrice.setText(Utils.toPrice(o.getPrice()));
            String stockText = "库存: " + o.getStock();
            if ("off".equals(o.getStatus())) stockText += " (已下架)";
            h.tvStock.setText(stockText);
            h.tvStock.setTextColor(o.getStock() <= (o.getWarningStock() != null ? o.getWarningStock() : 10)
                    ? getResources().getColor(R.color.danger, null)
                    : getResources().getColor(R.color.text_muted, null));
        }
        @Override public int getItemCount() { return products.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvStock;
            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_name);
                tvPrice = v.findViewById(R.id.tv_price);
                tvStock = v.findViewById(R.id.tv_stock);
            }
        }
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_chip, parent, false));
        }
        @Override public void onBindViewHolder(VH holder, int pos) {
            CategoryTree c = categories.get(pos);
            holder.chip.setText(c == null ? "全部" : c.getName());
            holder.chip.setOnClickListener(v -> {
                categoryId = c != null ? c.getId() : null;
                loadProducts(true);
            });
        }
        @Override public int getItemCount() { return categories.size(); }
        class VH extends RecyclerView.ViewHolder {
            com.google.android.material.chip.Chip chip;
            VH(View v) { super(v); chip = v.findViewById(R.id.chip); }
        }
    }

    interface FragmentCallback<T> { void onItem(T item); }
}
