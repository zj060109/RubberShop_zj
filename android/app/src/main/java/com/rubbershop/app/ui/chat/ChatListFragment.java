package com.rubbershop.app.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rubbershop.app.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListFragment extends Fragment {

    private RecyclerView convRecycler;
    private TextView emptyView;
    private Repository repo;
    private List<ChatConversation> conversations = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        convRecycler = v.findViewById(R.id.convRecycler);
        emptyView = v.findViewById(R.id.emptyView);
        repo = new Repository();

        convRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        convRecycler.setAdapter(new ConvAdapter());

        loadConversations();

        return v;
    }

    private void loadConversations() {
        repo.getConversations(new Repository.ResultCallback<List<ChatConversation>>() {
            @Override
            public void onSuccess(List<ChatConversation> data) {
                conversations.clear();
                if (data != null) conversations.addAll(data);
                convRecycler.getAdapter().notifyDataSetChanged();
                emptyView.setVisibility(conversations.isEmpty() ? View.VISIBLE : View.GONE);
                convRecycler.setVisibility(conversations.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                emptyView.setVisibility(View.VISIBLE);
                convRecycler.setVisibility(View.GONE);
            }
        });
    }

    private void openChat(ChatConversation conv) {
        ChatDetailFragment detail = ChatDetailFragment.newInstance(conv.getId(),
                conv.getCustomerName(), conv.getCustomerPhone());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, detail, "chat_detail")
                .addToBackStack("chat_detail")
                .commit();
    }

    private class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conv, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChatConversation conv = conversations.get(position);
            String name = conv.getCustomerName();
            if (name == null || name.isEmpty()) name = conv.getCustomerPhone();
            if (name == null || name.isEmpty()) name = "unknown";
            holder.name.setText(name);
            holder.avatar.setText(name.substring(0, 1).toUpperCase());
            holder.lastMsg.setText(conv.getLastMessage() != null ? conv.getLastMessage() : "");
            holder.time.setText(formatTime(conv.getLastMessageTime()));
            int unread = conv.getUnreadCount();
            if (unread > 0) {
                holder.badge.setVisibility(View.VISIBLE);
                holder.badge.setText(String.valueOf(unread));
            } else {
                holder.badge.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(v -> openChat(conv));
        }

        @Override
        public int getItemCount() { return conversations.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView avatar, name, lastMsg, time, badge;
            VH(View v) {
                super(v);
                avatar = v.findViewById(R.id.convAvatar);
                name = v.findViewById(R.id.convName);
                lastMsg = v.findViewById(R.id.convLastMsg);
                time = v.findViewById(R.id.convTime);
                badge = v.findViewById(R.id.convBadge);
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
        } catch (Exception e) { return ""; }
    }
}
