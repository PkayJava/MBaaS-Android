package com.angkorteam.mbaas.sdk.android.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.angkorteam.mbaas.sdk.android.library.SocketBroadcastReceiver;
import com.angkorteam.mbaas.sdk.android.library.MBaaS;
import com.angkorteam.mbaas.sdk.android.library.MBaaSClient;

public class ChatActivity extends AppCompatActivity implements SocketBroadcastReceiver.SocketReceiver {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.editText = (EditText) findViewById(R.id.editText);

        MBaaS.getInstance().getClient().registerReceiver(this, this);

        Button backOfficeButton = (Button) findViewById(R.id.backOfficeButton);
        if (backOfficeButton != null) {
            backOfficeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MBaaS.getInstance().promptLogin(ChatActivity.class);
                }
            });
        }

        Button adminButton = (Button) findViewById(R.id.adminButton);
        if (adminButton != null) {
            adminButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MBaaS.getInstance().promptLogin(ChatActivity.class);
                }
            });
        }

        Button sendAdmin = (Button) findViewById(R.id.sendAdmin);
        if (sendAdmin != null) {
            sendAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MBaaS.getInstance().getClient().sendPrivateMessage("c34ccf4e-5919-4ac0-a1b7-eaa0f3245efe", editText.getText().toString());
                }
            });
        }

        Button sendBackOffice = (Button) findViewById(R.id.sendBackOffice);
        if (sendBackOffice != null) {
            sendBackOffice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MBaaS.getInstance().getClient().sendPrivateMessage("f74b33c3-e3ed-4810-b691-90e3d18ab2ba", editText.getText().toString());
                }
            });
        }
    }

    @Override
    public void onMessage(String userId, String message) {
        Log.i("MBaaS", userId + " " + message);
    }
}
