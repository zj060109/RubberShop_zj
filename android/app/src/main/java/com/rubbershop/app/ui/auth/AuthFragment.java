package com.rubbershop.app.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.rubbershop.app.R;
import com.rubbershop.app.data.local.TokenManager;
import com.rubbershop.app.data.model.Models.LoginResponse;
import com.rubbershop.app.data.repository.Repository;

public class AuthFragment extends Fragment {
    private EditText etPhone, etPassword, etName;
    private View layoutRegister;
    private TextView tvToggle, tvTitle;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private Repository repository;
    private boolean isRegisterMode = false;

    public AuthFragment() { super(R.layout.fragment_auth); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new Repository();
        etPhone = view.findViewById(R.id.et_phone);
        etPassword = view.findViewById(R.id.et_password);
        etName = view.findViewById(R.id.et_name);
        layoutRegister = view.findViewById(R.id.layout_register);
        tvToggle = view.findViewById(R.id.tv_toggle);
        tvTitle = view.findViewById(R.id.tv_title);
        btnSubmit = view.findViewById(R.id.btn_submit);
        progressBar = view.findViewById(R.id.progress);

        String savedPhone = TokenManager.getSavedPhone();
        String savedPwd = TokenManager.getSavedPassword();
        if (savedPhone != null && savedPwd != null) {
            etPhone.setText(savedPhone);
            etPassword.setText(savedPwd);
            TokenManager.clearSavedCredentials();
        }

        btnSubmit.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "请填写手机号和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(true);

            if (isRegisterMode) {
                String name = etName.getText().toString().trim();
                repository.register(phone, password, name.isEmpty() ? null : name,
                        new Repository.ResultCallback<LoginResponse>() {
                            @Override public void onSuccess(LoginResponse data) {
                                TokenManager.saveRegisteredCredentials(phone, password);
                                switchToLogin();
                                setLoading(false);
                            }
                            @Override public void onError(String msg) { AuthFragment.this.onError(msg); }
                        });
            } else {
                repository.login(phone, password,
                        new Repository.ResultCallback<LoginResponse>() {
                            @Override public void onSuccess(LoginResponse data) { onLoginSuccess(data); }
                            @Override public void onError(String message) { onError(message); }
                        });
            }
        });

        tvToggle.setOnClickListener(v -> {
            isRegisterMode = !isRegisterMode;
            applyMode();
        });
    }

    private void switchToLogin() {
        isRegisterMode = false;
        applyMode();
        String phone = etPhone.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();
        etPhone.setText(phone);
        etPassword.setText(pwd);
    }

    private void applyMode() {
        layoutRegister.setVisibility(isRegisterMode ? View.VISIBLE : View.GONE);
        btnSubmit.setText(isRegisterMode ? "注册" : "登录");
        tvTitle.setText(isRegisterMode ? "创建账号" : "欢迎回来");
        tvToggle.setText(isRegisterMode ? "已有账号？去登录" : "没有账号？去注册");
    }

    private void onLoginSuccess(LoginResponse data) {
        setLoading(false);
        if (data != null && data.getToken() != null) {
            TokenManager.save(data.getToken(), data.getUserId(), data.getRole());
        }
        int dest;
        String role = TokenManager.getRole();
        if ("merchant".equals(role)) {
            dest = R.id.merchantProductListFragment;
        } else if ("factory".equals(role)) {
            dest = R.id.factoryPurchaseFragment;
        } else {
            dest = R.id.customerHomeFragment;
        }
        NavController nav = Navigation.findNavController(requireView());
        NavOptions opts = new NavOptions.Builder()
                .setPopUpTo(R.id.authFragment, true)
                .build();
        nav.navigate(dest, null, opts);
    }

    private void onError(String message) {
        setLoading(false);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
    }
}
