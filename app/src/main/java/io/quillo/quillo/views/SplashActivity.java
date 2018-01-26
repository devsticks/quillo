package io.quillo.quillo.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;

/**
 * Created by Stickells on 17/01/2018.
 */


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_start)
    public void handleStartClick (View v) {
        //Intent i = new Intent(this, HomeSearchActivity.class);
        //startActivity(i);
    }
}
