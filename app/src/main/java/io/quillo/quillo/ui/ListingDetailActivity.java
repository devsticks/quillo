package io.quillo.quillo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import io.quillo.quillo.R;

/**
 * Created by Stickells on 13/01/2018.
 */

public class ListingDetailActivity extends AppCompatActivity {

    private static final String EXTRA_TEXTBOOK_NAME = "EXTRA_TEXTBOOK_NAME";
    private static final String EXTRA_TEXTBOOK_DESCRIPTION = "EXTRA_TEXTBOOK_DESCRIPTION";
    private static final String EXTRA_DRAWABLE = "EXTRA_DRAWABLE";

    private TextView textbookName;
    private TextView textbookDescription;
    private View coloredBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);

        Intent i = getIntent();
        String textbookNameExtra = i.getStringExtra(EXTRA_TEXTBOOK_NAME);
        String textbookDescriptionExtra = i.getStringExtra(EXTRA_TEXTBOOK_DESCRIPTION);
        int drawableResourceExtra = i.getIntExtra(EXTRA_DRAWABLE, 0);

        textbookName = (TextView) findViewById(R.id.lbl_textbook_name);
        textbookName.setText(textbookNameExtra);

        textbookDescription = (TextView) findViewById(R.id.lbl_textbook_description);
        textbookDescription.setText(textbookDescriptionExtra);

        coloredBackground = findViewById(R.id.imv_colored_background);
        coloredBackground.setBackgroundResource(
                drawableResourceExtra
        );

    }
}