package io.nucleos.npc.example;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nucleos.nuclearpore.backend.helper.MessageHelper;

/**
 * Created by Mariangela Salcedo (mariangela@nucleos.io) on 07-09-2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChatAdapter.class.getSimpleName();

    protected static final int SECTION_HEADER = 0;
    protected static final int SECTION_ITEM = 1;
    private static final int OTHERS_ITEMS = 0;

    protected List<MessageHelper> mList;
    protected OnItemClickListener mListener;
    protected Context mContext;

    public ChatAdapter(List<MessageHelper> list, OnItemClickListener listener, Context context) {
        mList = list;
        mListener = listener;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return SECTION_ITEM;
    }

    @Override
    public int getItemCount() {
        return mList.size() + OTHERS_ITEMS;
    }

    public void setMessages(List<MessageHelper> allMessages) {
        mList = null;
        mList = allMessages;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_status)
        public ImageView ivStatus;
        @Bind(R.id.tv_sender)
        public TextView tvName;
        @Bind(R.id.tv_message)
        public TextView tvMessage;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder");

        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.tvName.setText(mList.get(position).getMessageDetails().getSenderTV());
        viewHolder.tvMessage.setText(mList.get(position).getMessage());
        viewHolder.ivStatus.setImageResource(mList.get(position).getStatusImage());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v, position);
            }
        });
    }

    // Interface for receiving click events from list items
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

}
