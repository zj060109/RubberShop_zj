package com.rubbershop.app.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rubbershop.app.R;
import com.rubbershop.app.data.local.TokenManager;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.io.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileFragment extends Fragment {
    private TextView tvName, tvPhone, tvBalance, tvPoints, tvCreditLimit, tvIdentity;
    private ImageView ivAvatar;
    private Button btnRecharge, btnPassword, btnIdentity, btnCustom, btnCredit, btnLogout;
    private ProgressBar progressBar;
    private Repository repository;

    public ProfileFragment() { super(R.layout.fragment_profile); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        tvName = view.findViewById(R.id.tv_name); tvPhone = view.findViewById(R.id.tv_phone);
        tvBalance = view.findViewById(R.id.tv_balance); tvPoints = view.findViewById(R.id.tv_points);
        tvCreditLimit = view.findViewById(R.id.tv_credit_limit); tvIdentity = view.findViewById(R.id.tv_identity);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        ImageButton btnAvatar = view.findViewById(R.id.btn_avatar);
        btnRecharge = view.findViewById(R.id.btn_recharge); btnPassword = view.findViewById(R.id.btn_password);
        btnIdentity = view.findViewById(R.id.btn_identity); btnCustom = view.findViewById(R.id.btn_custom);
        btnCredit = view.findViewById(R.id.btn_credit); btnLogout = view.findViewById(R.id.btn_logout);
        progressBar = view.findViewById(R.id.progress);

        btnAvatar.setOnClickListener(v -> pickAvatarImage());
        btnRecharge.setOnClickListener(v -> showRechargeDialog());
        btnPassword.setOnClickListener(v -> showPasswordDialog());
        btnIdentity.setOnClickListener(v -> showIdentityDialog());
        btnCustom.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.customListFragment));
        btnCredit.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.creditListFragment));
        btnLogout.setOnClickListener(v -> { TokenManager.clear(); Navigation.findNavController(view).navigate(R.id.authFragment); });
        loadProfile();
    }

    private final ActivityResultLauncher<Intent> avatarPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) uploadAvatar(uri);
                }
            });

    private void pickAvatarImage() {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 0);
        // Use simplified approach with startActivityForResult
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        avatarPickerLauncher.launch(intent);
    }

    private void uploadAvatar(Uri uri) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) { progressBar.setVisibility(View.GONE); return; }
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int n; byte[] d = new byte[4096];
            while ((n = is.read(d, 0, d.length)) != -1) buf.write(d, 0, n);
            is.close();
            RequestBody rb = RequestBody.create(MediaType.parse("image/*"), buf.toByteArray());
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", "avatar_" + System.currentTimeMillis() + ".jpg", rb);
            repository.uploadFile(part, new Repository.ResultCallback<String>() {
                @Override public void onSuccess(String url) {
                    if (!isAdded()) return;
                    Glide.with(ProfileFragment.this).load(Utils.getImageUrl(url)).circleCrop().into(ivAvatar);
                    repository.updateProfile(new ProfileUpdateRequest(null, url, null, null, null, null, null, null), new Repository.ResultCallback<Object>() {
                        @Override public void onSuccess(Object d) { Utils.toast(getContext(), "头像更新成功"); }
                        @Override public void onError(String m) { Utils.toast(getContext(), m); }
                    });
                    progressBar.setVisibility(View.GONE);
                }
                @Override public void onError(String msg) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), msg); }
            });
        } catch (Exception e) { progressBar.setVisibility(View.GONE); }
    }

    private void loadProfile() {
        if (!isAdded()) return;
        progressBar.setVisibility(View.VISIBLE);
        repository.getProfile(new Repository.ResultCallback<UserProfile>() {
            @Override public void onSuccess(UserProfile p) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                tvName.setText(p.getRealName() != null ? p.getRealName() : (p.getPhone() != null ? p.getPhone() : "用户"));
                tvPhone.setText(p.getPhone());
                tvBalance.setText(Utils.toPrice(p.getBalance()));
                tvPoints.setText((p.getPoints() != null ? p.getPoints() : 0) + "分");
                tvCreditLimit.setText(Utils.toPrice(p.getCreditLimit()));
                if (p.getAvatar() != null) Glide.with(ProfileFragment.this).load(Utils.getImageUrl(p.getAvatar())).circleCrop().into(ivAvatar);
                repository.getIdentityStatus(new Repository.ResultCallback<IdentityVerification>() {
                    @Override public void onSuccess(IdentityVerification iv) { if (isAdded()) tvIdentity.setText("已认证"); }
                    @Override public void onError(String m) { if (isAdded()) tvIdentity.setText("未认证"); }
                });
            }
            @Override public void onError(String msg) { if (!isAdded()) return; progressBar.setVisibility(View.GONE); Utils.toast(getContext(), msg); }
        });
    }

    private void showRechargeDialog() {
        EditText input = new EditText(getContext()); input.setHint("充值金额");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        new MaterialAlertDialogBuilder(requireContext()).setTitle("余额充值").setView(input)
                .setPositiveButton("确认", (d, w) -> {
                    try { double a = Double.parseDouble(input.getText().toString()); if (a > 0) { progressBar.setVisibility(View.VISIBLE); repository.recharge(a, new Repository.ResultCallback<Object>() { @Override public void onSuccess(Object x) { Utils.toast(getContext(), "充值成功"); loadProfile(); } @Override public void onError(String m) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), m); } }); } } catch (NumberFormatException e) { Utils.toast(getContext(), "请输入有效金额"); }
                }).setNegativeButton("取消", null).show();
    }

    private void showPasswordDialog() {
        LinearLayout c = new LinearLayout(getContext()); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(48, 16, 48, 0);
        EditText o = new EditText(getContext()); o.setHint("旧密码"); o.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText n = new EditText(getContext()); n.setHint("新密码"); n.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        c.addView(o); c.addView(n);
        new MaterialAlertDialogBuilder(requireContext()).setTitle("修改密码").setView(c)
                .setPositiveButton("确认", (d, w) -> { progressBar.setVisibility(View.VISIBLE); repository.changePassword(o.getText().toString(), n.getText().toString(), new Repository.ResultCallback<Object>() { @Override public void onSuccess(Object x) { Utils.toast(getContext(), "密码修改成功"); progressBar.setVisibility(View.GONE); } @Override public void onError(String m) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), m); } }); })
                .setNegativeButton("取消", null).show();
    }

    private void showIdentityDialog() {
        LinearLayout c = new LinearLayout(getContext()); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(48, 16, 48, 0);
        EditText n = new EditText(getContext()); n.setHint("真实姓名"); EditText i = new EditText(getContext()); i.setHint("身份证号");
        c.addView(n); c.addView(i);
        new MaterialAlertDialogBuilder(requireContext()).setTitle("实名认证").setView(c)
                .setPositiveButton("提交", (d, w) -> { progressBar.setVisibility(View.VISIBLE); repository.submitIdentity(i.getText().toString(), n.getText().toString(), null, new Repository.ResultCallback<Object>() { @Override public void onSuccess(Object x) { Utils.toast(getContext(), "实名认证成功"); loadProfile(); } @Override public void onError(String m) { progressBar.setVisibility(View.GONE); Utils.toast(getContext(), m); } }); })
                .setNegativeButton("取消", null).show();
    }
}
