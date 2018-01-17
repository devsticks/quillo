package io.quillo.quillo.handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import de.cketti.mailto.EmailIntentBuilder;
import io.quillo.quillo.R;
import io.quillo.quillo.data.Listing;
import io.quillo.quillo.data.Person;

/**
 * Created by Stickells on 17/01/2018.
 */

public class ContactOptionsDialogController {
    private Activity view;
    private Person seller;
    private Listing listing;

    public ContactOptionsDialogController(Person seller, Listing listing, Activity view) {
        this.view = view;
        this.seller = seller;
        this.listing = listing;
    }

    public void showContactOptionsDialog() {
        boolean wrapInScrollView = false;
        MaterialDialog d = new MaterialDialog.Builder(view)
                .title("Contact " + seller.getName())
                .customView(R.layout.dialog_contact_options, wrapInScrollView)
                .negativeText("Cancel")
//                .negativeColor(Resources.getSystem().getColor(R.color.LightText))
                .show();

        View mCall = (View) d.findViewById(R.id.btn_dialog_call);
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);

                //TODO get rid of this once real phone numbers are loaded
                seller.setPhoneNumber("+27843392938");
                callIntent.setData(Uri.parse("tel:"+ seller.getPhoneNumber()));

                view.getContext().startActivity(callIntent);
            }
        });

        View mEmail = (View) d.findViewById(R.id.btn_dialog_email);
        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean success = EmailIntentBuilder.from(view.getContext())
                        .to(seller.getEmail())
                        .subject("Quillo Enquiry")
                        .body("Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.")
                        .start();
                if (!success) {
                    Toast.makeText(view.getContext(),
                            "Email failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        View mText = (View) d.findViewById(R.id.btn_dialog_text);
        mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //TODO get rid of this once real phone numbers are loaded
                    seller.setPhoneNumber("+27843392938");
                    Uri uri = Uri.parse("smsto:"+seller.getPhoneNumber());
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    smsIntent.putExtra("sms_body", "Hi " + seller.getName() + ". I'd like to enquire about your ad for " + listing.getName() + " on Quillo.");
                    view.getContext().startActivity(smsIntent);
                } catch (Exception e) {
                    Toast.makeText(view.getContext(),
                            "SMS failed, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

}
