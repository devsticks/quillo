package io.quillo.quillo.handlers;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Database;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.views.HomeSearchActivity;

/**
 * Created by Stickells on 17/01/2018.
 */

public class ContactOptionsDialogController {
    private Activity view;
    private Person seller;
    private Listing listing;

    public ContactOptionsDialogController (Person seller, Listing listing, Activity view) {
        this.view = view;
        this.seller = seller;
        this.listing = listing;
    }

    public void showContactOptionsDialog() {
        boolean wrapInScrollView = false;
        new MaterialDialog.Builder(view)
                .title("Contact " + seller.getName())
                .customView(R.layout.dialog_contact_options, wrapInScrollView)
                .negativeText("Cancel")
                .show();
    }

}
