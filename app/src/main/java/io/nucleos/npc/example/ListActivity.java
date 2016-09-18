package io.nucleos.npc.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.layer.sdk.messaging.Conversation;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nucleos.nuclearpore.backend.manager.NuclearPore;
import io.nucleos.nuclearpore.util.StringHelper;

public class ListActivity extends AppCompatActivity implements
        NuclearPore.CreateConversationListener,
        NuclearPore.LoadConversationsListener,
        ListAdapter.OnItemClickListener {

    private static final String TAG = ListActivity.class.getSimpleName();
    
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String mUserId;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_inbox);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mUserId = getIntent().getExtras().getString("userId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        NuclearPore
                .instance(this)
                .loadConversations(this);
    }

    @OnClick(R.id.fab)
    public void add() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("ADD A CONVERSATION");
        alertDialog.setMessage("Enter User ID");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("CREATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText() != null && !input.getText().toString().trim().isEmpty()) {
                            createOrFindConversation(input.getText().toString().trim());
                        }
                    }
                });

        alertDialog.show();
    }

    private void createOrFindConversation(String userId) {
        NuclearPore
                .instance(this)
                .createOrFind(userId, this);
    }

    @Override
    public void onCreateConversationSuccess(Conversation conversation) {
        Log.d(TAG, "onCreateConversationSuccess " + conversation.getParticipants());
        mAdapter.add(conversation);
    }

    @Override
    public void onCreateConversationFailed() {
        Log.d(TAG, "onCreateConversationFailed");
    }

    @Override
    public void onLoadConversationSuccess(List<Conversation> mConversationsList) {
        Log.d(TAG, "onLoadConversationSuccess: " + mConversationsList.size());
        for (Conversation c :
                mConversationsList) {
            Log.d(TAG, "onLoadConversationSuccess: " + StringHelper.toString(c.getParticipants()));
        }

        mAdapter = new ListAdapter(mConversationsList, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadConversationFailed() {
        Log.d(TAG, "onLoadConversationFailed");
    }

    @Override
    public void onClick(View view, int position, List<String> participants, String id) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", mUserId);
        intent.putStringArrayListExtra("participants", new ArrayList<String>(participants));
        intent.putExtra("chatId", id);
        startActivity(intent);
    }
}
