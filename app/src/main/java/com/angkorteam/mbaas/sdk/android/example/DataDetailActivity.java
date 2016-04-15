package com.angkorteam.mbaas.sdk.android.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DataDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvMp3;
    private TextView tvImage;
    private TextView tvVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);

        Bundle bundle = getIntent().getExtras();

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvMp3 = (TextView) findViewById(R.id.tvMp3);
        tvImage = (TextView) findViewById(R.id.tvImage);
        tvVideo = (TextView) findViewById(R.id.tvVideo);

        tvTitle.setText(bundle.getString("title"));
        tvDescription.setText(bundle.getString("description"));
        tvMp3.setText(bundle.getString("mp3"));
        tvImage.setText(bundle.getString("image"));
        tvVideo.setText(bundle.getString("video"));





    }
}
