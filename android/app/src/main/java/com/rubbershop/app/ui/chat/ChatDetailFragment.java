package com.rubbershop.app.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rubbershop.app.R;
import com.rubbershop.app.data.local.TokenManager;
import com.rubbershop.app.data.model.Models.*;
import com.rubbershop.app.data.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDetailFragment extends Fragment {

    private static final String ARG_CONV_ID = "convId";
    private static final String ARG_NAME = "name";
    private static final String ARG_PHONE = "phone";

    private RecyclerView chatRecycler;
    private EditText msgInput;
    private View sendBtn, backBtn;
    private TextView titleView;
    private Repository repo;
    private Long conversationId;
    private List<ChatMessage> messages = new ArrayList<>();
    private ChatAdapter adapter;
    private Long myUserId;

    public static ChatDetailFragment newInstance(Long convId, String name, String phone) {
        ChatDetailFragment f = new ChatDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CONV_ID, convId);
        args.putString(ARG_NAME, name);
        args.putString(ARG_PHONE, phone);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        chatRecycler = v.findViewById(R.id.chatRecycler);
        msgInput = v.findViewById(R.id.msgInput);
        sendBtn = v.findViewById(R.id.sendBtn);
        repo = new Repository();
        myUserId = TokenManager.getUserId();

        Bundle args = getArguments();
        if (args != null) {
            conversationId = args.getLong(ARG_CONV_ID);
        }

        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter();
        chatRecycler.setAdapter(adapter);

        loadMessages();

        sendBtn.setOnClickListener(view -> sendMessage());

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            androidx.appcompat.widget.Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                String name = getArguments() != null ? getArguments().getString(ARG_NAME) : "Chat";
                if (name == null || name.isEmpty()) name = getArguments() != null ? getArguments().getString(ARG_PHONE) : "Chat";
                toolbar.setTitle(name != null ? name : "Chat");
                toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
                toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
            }
        }
    }

    private void loadMessages() {
        if (conversationId == null) return;
        repo.getMessages(conversationId, 1, 50, new Repository.ResultCallback<ChatMessagesResponse>() {
            @Override
            public void onSuccess(ChatMessagesResponse data) {
                if (data != null && data.getRecords() != null) {
                    messages.clear();
                    List<ChatMessage> recs = data.getRecords();
                    for (int i = recs.size() - 1; i >= 0; i--) messages.add(recs.get(i));
                    adapter.notifyDataSetChanged();
                    chatRecycler.scrollToPosition(messages.size() - 1);
                }
                repo.markRead(conversationId, null);
            }

            @Override
            public void onError(String message) { }
        });
    }

    private void sendMessage() {
        String text = msgInput.getText().toString().trim();
        if (text.isEmpty()) return;
        msgInput.setText("");
        repo.sendMessage(conversationId, text, new Repository.ResultCallback<ChatMessage>() {
            @Override
            public void onSuccess(ChatMessage data) {
                messages.add(data);
                adapter.notifyItemInserted(messages.size() - 1);
                chatRecycler.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onError(String message) {
                com.rubbershop.app.util.Utils.toast(requireContext(), "failed: " + message);
            }
        });
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChatMessage msg = messages.get(position);
            boolean isMe = msg.getSenderId() != null && msg.getSenderId().equals(myUserId);
            holder.msgText.setText(msg.getContent());
            holder.msgTime.setText(formatTime(msg.getCreatedAt()));

            androidx.constraintlayout.widget.ConstraintLayout.LayoutParams lp =
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) holder.msgBubble.getLayoutParams();
            if (isMe) {
                holder.msgBubble.setBackgroundResource(R.drawable.chat_bubble_self);
                holder.msgText.setTextColor(0xFFFFFFFF);
                holder.msgTime.setTextColor(0xCCFFFFFF);
                lp.horizontalBias = 1.0f;
            } else {
                holder.msgBubble.setBackgroundResource(R.drawable.chat_bubble_other);
                holder.msgText.setTextColor(0xFF1E293B);
                holder.msgTime.setTextColor(0xFF94A3B8);
                lp.horizontalBias = 0.0f;
            }
            holder.msgBubble.setLayoutParams(lp);
        }

        @Override
        public int getItemCount() { return messages.size(); }

        class VH extends RecyclerView.ViewHolder {
            View msgBubble;
            android.widget.TextView msgText, msgTime;
            VH(View v) {
                super(v);
                msgBubble = v.findViewById(R.id.msgBubble);
                msgText = v.findViewById(R.id.msgText);
                msgTime = v.findViewById(R.id.msgTime);
            }
        }
    }

    private String formatTime(String t) {
        if (t == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date d = sdf.parse(t);
            SimpleDateFormat out = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            return out.format(d);
        } catch (Exception e) { return t; }
    }

    private int dpToPx(int dp) {
        return (int)(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}
