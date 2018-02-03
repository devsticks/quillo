package io.quillo.quillo.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.utils.RotateBitmap;

/**
 * Created by shkla on 2018/01/21.
 */

public class SelectPhotoDialog extends DialogFragment{

    private static final String TAG = SelectPhotoDialog.class.getName();

    private static final int RC_PICKFILE = 1;
    private static final int RC_CAMERA = 2;

    public interface OnPhotoSelectedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    OnPhotoSelectedListener onPhotoSelectedListener;

    @BindView(R.id.gallery_tv)
    View galleryButton;
    @BindView(R.id.camera_tv)
    View cameraButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_photo, null);
        ButterKnife.bind(this, view);
        builder.setView(view);
        builder.setTitle("Select Image");

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }



    @Override
    public void onAttach(Context context) {
        try{
            onPhotoSelectedListener = (OnPhotoSelectedListener) getTargetFragment();
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
    private Uri imageUri;
    private  ContentValues contentValues;
    @OnClick(R.id.camera_tv)
    public void handleCameraClick() {
        contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "From your camera");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(intent, RC_CAMERA);

        /*FotoAppartCamera fotoAppartCamera = new FotoAppartCamera();
        fotoAppartCamera.setCameraListner(new FotoAppartCamera.CameraListner() {
            @Override
            public void onPhotoTaken(BitmapPhoto bitmap) {
                onPhotoSelectedListener.getImageBitmap (bitmap.bitmap);
            }
        });
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_holder, fotoAppartCamera)
                .addToBackStack(AddEditListingFragment.FRAGMENT_NAME)
                .commit();*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICKFILE && resultCode == Activity.RESULT_OK) {

            Uri selectedImageUri = data.getData();
            onPhotoSelectedListener.getImagePath(selectedImageUri);
            getDialog().dismiss();

        } else if (requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK) {


            /*try{
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                onPhotoSelectedListener.getImageBitmap(thumbnail);
            }catch (Exception e){
                e.printStackTrace();
            }*/
            RotateBitmap rotateBitmap = new RotateBitmap();
            try {
                Bitmap rotatedBitmap = rotateBitmap.HandleSamplingAndRotationBitmap(getActivity(), imageUri);
                onPhotoSelectedListener.getImageBitmap(rotatedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            getDialog().dismiss();

        }
    }

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    public Uri getImageUri(Bitmap bitmap){
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Listing", null);
        return Uri.parse(path);
    }
}
