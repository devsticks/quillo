package io.quillo.quillo.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import io.quillo.quillo.R;

/**
 * Created by shkla on 2018/01/21.
 */

public class SelectPhotoDialog extends android.support.v4.app.DialogFragment{

    private static final String TAG = SelectPhotoDialog.class.getName();

    private static final int RC_PICKFILE = 1;
    private static final int RC_CAMERA = 2;



    public interface OnPhotoSelectedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoSelectedListener onPhotoSelectedListener;

    @BindView(R.id.gallery_tv)
    TextView galleryTextView;
    @BindView(R.id.camera_tv)
    TextView cameraTextView;

    public SelectPhotoDialog(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] options =  {"Chose from gallery", "Take Photo"};
        //TODO: Find out why this does not work
        /*builder.setMessage(R.string.dialog_select_photo)

                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            handleGalleryClick();
                        }else if (i ==  1){
                            handleCameraClick();
                        }
                    }
                });*/

        handleGalleryClick();
        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        try{
            onPhotoSelectedListener = (OnPhotoSelectedListener) getActivity();
        }catch(ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }

        super.onAttach(context);
    }

    @OnClick(R.id.gallery_tv)
    public void handleGalleryClick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, RC_PICKFILE);
    }

    @OnClick(R.id.camera_tv)
    public void handleCameraClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RC_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICKFILE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            onPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();


        } else if (requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            onPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();

        }

    }
}
