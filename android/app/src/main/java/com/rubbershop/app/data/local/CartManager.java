package com.rubbershop.app.data.local;

import com.rubbershop.app.data.model.Models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> items = new ArrayList<>();

    public static synchronized CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public void add(Product product, int quantity) {
        for (CartItem item : items) {
            if (Objects.equals(item.product.getId(), product.getId())) {
                item.quantity += quantity;
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public void removeById(Long productId) {
        for (int i = 0; i < items.size(); i++) {
            if (Objects.equals(items.get(i).product.getId(), productId)) {
                items.remove(i);
                return;
            }
        }
    }

    public void updateQuantity(int index, int quantity) {
        if (index >= 0 && index < items.size()) {
            items.get(index).quantity = quantity;
        }
    }

    public void remove(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public void clear() {
        items.clear();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getCount() {
        int total = 0;
        for (CartItem item : items) total += item.quantity;
        return total;
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) total += item.product.getPrice() * item.quantity;
        return total;
    }

    public static class CartItem {
        public Product product;
        public int quantity;
        CartItem(Product p, int q) { this.product = p; this.quantity = q; }
    }
}
