package com.rubbershop.app.ui.factory;

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

public class FactoryProfileFragment extends Fragment {
    private TextView tvName, tvPhone, tvCompany;
    private Repository repository;

    public FactoryProfileFragment() { super(R.layout.fragment_factory_profile); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        repository = new Repository();
        tvName = v.findViewById(R.id.tv_name);
        tvPhone = v.findViewById(R.id.tv_phone);
        tvCompany = v.findViewById(R.id.tv_company);
        Button btnLogout = v.findViewById(R.id.btn_logout);

        repository.getProfile(new Repository.ResultCallback<UserProfile>() {
            @Override public void onSuccess(UserProfile p) {
                if (isAdded()) {
                    tvName.setText(p.getRealName() != null ? p.getRealName() : "厂家");
                    tvPhone.setText(p.getPhone());
                    tvCompany.setText(p.getCompanyName() != null ? p.getCompanyName() : "-");
                }
            }
            @Override public void onError(String m) {
                if (isAdded()) Utils.toast(getContext(), m);
            }
        });
        btnLogout.setOnClickListener(v2 -> {
            TokenManager.clear();
            Navigation.findNavController(v).navigate(R.id.authFragment);
        });
    }
}
