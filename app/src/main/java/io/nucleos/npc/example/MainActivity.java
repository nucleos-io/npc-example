package io.nucleos.npc.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nucleos.nuclearpore.backend.manager.InboxManager;

public class MainActivity extends AppCompatActivity implements InboxManager.LayerListener {

    @Bind(R.id.id_layer)
    EditText mEditTextIdLayer;
    @Bind(R.id.button_connect)
    Button mButtonConnect;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_inbox);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        InboxManager.logout();
        mButtonConnect.setEnabled(true);
    }

    @OnClick(R.id.button_connect)
    public synchronized void connect(View view) {
        if (mEditTextIdLayer.getText() != null &&
                !mEditTextIdLayer.getText().toString().trim().isEmpty()) {
            mUserId = mEditTextIdLayer.getText().toString().trim();
            mButtonConnect.setEnabled(false);
            InboxManager
                    .instance(this, mUserId, this)
                    .loadLayerClient();
        }
    }

    @Override
    public void onUserAuthenticatedSuccess() {
        mButtonConnect.setEnabled(false);
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("userId", mUserId);
        startActivity(intent);
    }

    @Override
    public void onUserAuthenticatedFailed() {
        mButtonConnect.setEnabled(true);

    }

    @Override
    public void onConnectionDisconnected() {
        mButtonConnect.setEnabled(true);

    }
}
