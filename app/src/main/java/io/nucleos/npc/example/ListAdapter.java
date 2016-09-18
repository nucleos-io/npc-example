package io.nucleos.npc.example;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.layer.sdk.messaging.Conversation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nucleos.nuclearpore.util.StringHelper;

/**
 * Created by Mariangela Salcedo (mariangela@nucleos.io) on 07-09-2016.
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ListAdapter.class.getSimpleName();

    protected static final int SECTION_HEADER = 0;
    protected static final int SECTION_ITEM = 1;
    private static final int OTHERS_ITEMS = 0;

    protected List<Conversation> mList;
    protected OnItemClickListener mListener;
    protected Context mContext;

    public ListAdapter(List<Conversation> list, OnItemClickListener listener, Context context) {
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

    public void add(Conversation conversation) {
        mList.add(conversation);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivAvatar)
        public ImageView ivAvatar;
        @Bind(R.id.tvName)
        public TextView tvName;
        @Bind(R.id.ivSubtitle)
        public ImageView ivSubtitle;
        @Bind(R.id.tvSubtitle)
        public TextView tvSubtitle;
        @Bind(R.id.tvTime)
        public TextView tvTime;
        @Bind(R.id.tvNotification)
        public TextView tvNotification;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_conversation_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder");

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.tvNotification.setVisibility(View.INVISIBLE);

        viewHolder.tvName.setText(StringHelper.toString(mList.get(position).getParticipants()));
        viewHolder.tvSubtitle.setText(StringHelper.toString(mList.get(position).getTotalMessageCount()) + " messages");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v, position, mList.get(position).getParticipants(), mList.get(position).getId().toString());
            }
        });
    }

    // Interface for receiving click events from list items
    public interface OnItemClickListener {
        void onClick(View view, int position, List<String> participants, String id);
    }

}
