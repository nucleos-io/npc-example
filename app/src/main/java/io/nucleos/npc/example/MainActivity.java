package io.nucleos.npc.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nucleos.nuclearpore.backend.manager.NuclearPore;

public class MainActivity extends AppCompatActivity implements NuclearPore.AuthLayerListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.spinner)
    Spinner mSpinner;
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
        NuclearPore.logout();
        mButtonConnect.setEnabled(true);
    }

    @OnClick(R.id.button_connect)
    public synchronized void connect(View view) {
        String header = "";

        switch (mSpinner.getSelectedItemPosition()) {
            case 0:
                header = "Bearer 6ef2641565def33017a06b10b1dfc018";
                break;
            case 1:
                header = "Bearer 2afa99e00cc73f246d55b27d36ff7282";
                break;
        }

        mUserId = mSpinner.getSelectedItem().toString();
        mButtonConnect.setEnabled(false);
        NuclearPore
                .instance(this)
                .setAuthorization(header)
                .setUserId(mUserId)
                .loadLayerClient(this);
    }

    @Override
    public synchronized void onUserAuthenticatedSuccess() {
        Log.d(TAG, "onUserAuthenticatedSuccess: -------------------------------------------------------------------------------------------");
        NuclearPore
                .instance(this)
                .setListenerAuth(null);
        mButtonConnect.setEnabled(false);
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra("userId", mUserId);
        startActivity(intent);
        finish();
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
