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


public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_landing);
        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



    }

    @OnClick(R.id.btn_start)
    public void handleStartClick (View v) {
        saveUniversityToPrefrences();

    }





    private void saveUniversityToPrefrences(){

    }


}
