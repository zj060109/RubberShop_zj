package com.rubbershop.app.ui.merchant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.rubbershop.app.R;
import com.rubbershop.app.data.local.TokenManager;
import com.rubbershop.app.data.model.Models.UserProfile;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

public class MerchantProfileFragment extends Fragment {
    private TextView tvName, tvPhone, tvRole;
    private Repository repository;

    public MerchantProfileFragment() { super(R.layout.fragment_merchant_profile); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new Repository();
        tvName = v.findViewById(R.id.tv_name);
        tvPhone = v.findViewById(R.id.tv_phone);
        tvRole = v.findViewById(R.id.tv_role);
        Button btnCredit = v.findViewById(R.id.btn_credit);
        Button btnLogout = v.findViewById(R.id.btn_logout);

        repository.getProfile(new Repository.ResultCallback<UserProfile>() {
            @Override public void onSuccess(UserProfile p) {
                if (isAdded()) {
                    tvName.setText(p.getRealName() != null ? p.getRealName() : "商户");
                    tvPhone.setText(p.getPhone());
                    tvRole.setText("merchant".equals(p.getRole()) ? "商户" : "factory".equals(p.getRole()) ? "工厂" : "顾客");
                }
            }
            @Override public void onError(String m) {
                if (isAdded()) Utils.toast(getContext(), m);
            }
        });
        btnCredit.setOnClickListener(v2 -> Navigation.findNavController(v).navigate(R.id.merchantCreditFragment));
        btnLogout.setOnClickListener(v2 -> {
            TokenManager.clear();
            Navigation.findNavController(v).navigate(R.id.authFragment);
        });
    }
}
