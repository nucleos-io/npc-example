package io.nucleos.npc.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nucleos.nuclearpore.backend.helper.MessageHelper;
import io.nucleos.nuclearpore.backend.manager.ConversationManager;
import io.nucleos.nuclearpore.backend.manager.NuclearPore;

public class ChatActivity extends AppCompatActivity implements
        NuclearPore.AuthLayerListener,
        ConversationManager.ConversationListener,
        ChatAdapter.OnItemClickListener, TextWatcher {

    private static final String TAG = ChatActivity.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.et_message)
    EditText mEtMessage;
    @Bind(R.id.tv_sender)
    TextView mTvSender;

    private String mUserId;
    private ChatAdapter mAdapter;
    private ArrayList<String> mParticipants;
    private ConversationManager mConversationManager;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_inbox);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mUserId = getIntent().getExtras().getString("userId", "userId");
        mParticipants = getIntent().getExtras().getStringArrayList("participants");
        mId = getIntent().getExtras().getString("chatId");

        mEtMessage.addTextChangedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NuclearPore
                .instance(this)
                .register(this);
        mConversationManager = NuclearPore
                .instance(this)
                .loadConversation(mId, mParticipants, this);
    }

    @Override
    protected void onDestroy() {
        NuclearPore
                .instance(this)
                .unregister();
        super.onDestroy();
    }

    @OnClick(R.id.button_send)
    public void send() {
        if (mEtMessage.getText() != null && !mEtMessage.getText().toString().trim().isEmpty()) {
            if (mConversationManager.isAutenticated()) {
                mConversationManager.sendMessage(mEtMessage.getText().toString().trim());
                mEtMessage.setText("");
                mConversationManager.finishedMessage();
            }
        }
    }

    @Override
    public void onUserAuthenticatedSuccess() {
        Log.d(TAG, "onUserAuthenticatedSuccess");
    }

    @Override
    public void onUserAuthenticatedFailed() {
        Log.d(TAG, "onUserAuthenticatedFailed");
    }

    @Override
    public void onConnectionDisconnected() {
        Log.d(TAG, "onConnectionDisconnected");
    }


    @Override
    public void notifyChanges(List<MessageHelper> allMessages) {
        if (mAdapter != null) {
            mAdapter.setMessages(allMessages);
            mAdapter.notifyDataSetChanged();
        } else {
            chargedConversationSuccessful(allMessages);
        }
    }

    @Override
    public void typingIndicator(String typingIndicator) {
        Log.d(TAG, "typingIndicator: " + typingIndicator);
        mTvSender.setText(typingIndicator);
    }

    @Override
    public void chargedConversationSuccessful(List<MessageHelper> allMessages) {
        if (allMessages == null) {
            allMessages = new ArrayList<>();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ChatAdapter(allMessages, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mConversationManager.startedMessage();
    }

}
