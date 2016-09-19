package io.nucleos.npc.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.layer.atlas.AtlasMessageComposer;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.AtlasTypingIndicator;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.location.LocationSender;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.text.TextSender;
import com.layer.atlas.messagetypes.threepartimage.CameraSender;
import com.layer.atlas.messagetypes.threepartimage.GallerySender;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.atlas.typingindicators.BubbleTypingIndicatorFactory;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.nucleos.nuclearpore.backend.manager.ConversationManager;
import io.nucleos.nuclearpore.backend.manager.NuclearPore;
import io.nucleos.nuclearpore.model.RecordSender;

public class ChatActivity extends AppCompatActivity{

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static String ID;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recyclerView)
    AtlasMessagesRecyclerView mRecyclerView;
    @Bind(R.id.tv_sender)
    AtlasTypingIndicator mTvSender;
    @Bind(R.id.message_composer)
    AtlasMessageComposer messageComposer;


    private String mUserId;
    private ArrayList<String> mParticipants;
    private ConversationManager mConversationManager;
    private String mId;

    public static String getId() {
        return ID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_inbox);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mId = getIntent().getExtras().getString("chatId");

        Conversation conversation = NuclearPore.instance(this).getConversation(mId);

        LayerClient layerClient = NuclearPore.instance(this).getLayerClient();
        Picasso picasso = App.getPicasso(this, layerClient);

        mRecyclerView
                .init(layerClient, picasso)
                .setConversation(conversation)
                .addCellFactories(
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(this, layerClient, picasso),
                        new LocationCellFactory(this, picasso));

        messageComposer
                .init(layerClient)
                .setTextSender(new TextSender())
                .addAttachmentSenders(
                        new CameraSender("Camera", R.drawable.ic_photo_camera_white_24dp, this),
                        new GallerySender("Gallery", R.drawable.ic_photo_white_24dp, this),
                        new RecordSender("Record Audio", R.drawable.ic_place_white_24dp, this))
                .setConversation(conversation);

        mTvSender = new AtlasTypingIndicator(this)
                .init(layerClient)
                .setTypingIndicatorFactory(new BubbleTypingIndicatorFactory())
                .setTypingActivityListener(new AtlasTypingIndicator.TypingActivityListener() {
                    public void onTypingActivityChange(AtlasTypingIndicator typingIndicator, boolean active) {
                        mRecyclerView.setFooterView(active ? typingIndicator : null);
                    }
                }).setConversation(conversation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ID = mId;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ID = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        messageComposer.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
