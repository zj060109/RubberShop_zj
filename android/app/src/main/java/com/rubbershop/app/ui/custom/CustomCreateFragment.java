package com.rubbershop.app.ui.custom;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.rubbershop.app.R;
import com.rubbershop.app.data.repository.Repository;
import com.rubbershop.app.util.Utils;

import java.io.*;
import java.util.*;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CustomCreateFragment extends Fragment {
    private EditText etDescription, etSpec, etQuantity;
    private Button btnSubmit, btnUpload;
    private LinearLayout llImages;
    private ProgressBar progressBar;
    private Repository repository;
    private final List<String> uploadedUrls = new ArrayList<>();

    public CustomCreateFragment() { super(R.layout.fragment_custom_create); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new Repository();
        etDescription = view.findViewById(R.id.et_description);
        etSpec = view.findViewById(R.id.et_spec);
        etQuantity = view.findViewById(R.id.et_quantity);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnUpload = view.findViewById(R.id.btn_upload);
        llImages = view.findViewById(R.id.ll_images);
        progressBar = view.findViewById(R.id.progress);

        btnUpload.setOnClickListener(v -> pickImages());

        btnSubmit.setOnClickListener(v -> {
            String desc = etDescription.getText().toString().trim();
            if (desc.isEmpty()) { Utils.toast(getContext(), "请描述定制需求"); return; }
            progressBar.setVisibility(View.VISIBLE);
            String spec = etSpec.getText().toString().trim();
            String qty = etQuantity.getText().toString().trim();
            StringBuilder fullDesc = new StringBuilder(desc);
            if (!spec.isEmpty()) fullDesc.append("\n规格要求：").append(spec);
            if (!qty.isEmpty()) fullDesc.append("\n预估数量：").append(qty);
            if (!uploadedUrls.isEmpty()) {
                fullDesc.append("\n样品图：");
                for (String url : uploadedUrls) fullDesc.append(url).append(" ");
            }
            repository.createCustomization(fullDesc.toString(), new Repository.ResultCallback<Object>() {
                @Override public void onSuccess(Object data) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    Utils.toast(getContext(), "定制需求已提交");
                    Navigation.findNavController(view).navigateUp();
                }
                @Override public void onError(String msg) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    Utils.toast(getContext(), msg);
                }
            });
        });
    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    List<Uri> uris = new ArrayList<>();
                    if (result.getData().getClipData() != null) {
                        int count = result.getData().getClipData().getItemCount();
                        for (int i = 0; i < count; i++) {
                            uris.add(result.getData().getClipData().getItemAt(i).getUri());
                        }
                    } else if (result.getData().getData() != null) {
                        uris.add(result.getData().getData());
                    }
                    for (Uri uri : uris) uploadImage(uri);
                }
            });

    private void uploadImage(Uri uri) {
        progressBar.setVisibility(View.VISIBLE);
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) { progressBar.setVisibility(View.GONE); return; }
            byte[] bytes = readBytes(is);
            is.close();

            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), bytes);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", "sample_" + System.currentTimeMillis() + ".jpg", requestBody);

            repository.uploadFile(part, new Repository.ResultCallback<String>() {
                @Override public void onSuccess(String url) {
                    if (!isAdded()) return;
                    uploadedUrls.add(url);
                    addImagePreview(url);
                    progressBar.setVisibility(View.GONE);
                }
                @Override public void onError(String msg) {
                    if (!isAdded()) return;
                    progressBar.setVisibility(View.GONE);
                    Utils.toast(getContext(), "上传失败");
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void addImagePreview(String url) {
        ImageView iv = new ImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, 120);
        lp.setMargins(0, 0, 12, 0);
        iv.setLayoutParams(lp);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String fullUrl = Utils.getImageUrl(url);
        Glide.with(this).load(fullUrl).placeholder(R.drawable.bg_avatar).into(iv);
        iv.setOnClickListener(v -> {
            Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            ImageView full = new ImageView(getContext());
            full.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(this).load(fullUrl).into(full);
            full.setOnClickListener(v2 -> dialog.dismiss());
            dialog.setContentView(full);
            dialog.show();
        });
        llImages.addView(iv);
    }

    private byte[] readBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int n;
        byte[] data = new byte[4096];
        while ((n = is.read(data, 0, data.length)) != -1) buffer.write(data, 0, n);
        return buffer.toByteArray();
    }
}
