package io.quillo.quillo.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.IntentExtras;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.utils.FirebaseHelper;

/**
 * Created by shkla on 2018/01/27.
 */

//TODO: Clean up UI

public class EditProfileFragment extends Fragment implements SelectPhotoDialog.OnPhotoSelectedListener {
    private static final int RC_PERMISSIONS = 22;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.input_name)
    EditText nameInput;
    @BindView(R.id.input_email)
    EditText emailInput;
    @BindView(R.id.input_phone)
    EditText phoneInput;
    @BindView(R.id.input_university)
    AutoCompleteTextView universityInput;

    private Person person;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        ButterKnife.bind(this, view);

        person = (Person)this.getArguments().getSerializable(IntentExtras.EXTRA_SELLER);
        bindPersonToViews();
        universityInput.setAdapter(FirebaseHelper.getSupportedUniversitiesAdapter(getActivity()));


        return view;
    }


    private void bindPersonToViews(){
        nameInput.setText(person.getName());
        emailInput.setText(person.getEmail());
        if(person.getPhoneNumber() != null) {
            phoneInput.setText(person.getPhoneNumber());
        }
        universityInput.setText(person.getUniversityUid());

        if(person.getImageUrl() != null){
            Glide.with(getContext()).load(person.getImageUrl()).into(profileImage);
        }
    }

    @OnClick({R.id.profile_image, R.id.fab_add_photo})
    public void handleProfileImageClick(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        1);
            }
            if(getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        SelectPhotoDialog dialog = new SelectPhotoDialog();
        dialog.setTargetFragment(EditProfileFragment.this, 1);
        dialog.show(getActivity().getSupportFragmentManager(), "Select Photo");
    }
    private void verifyPermissions() {
        String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext().getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_PERMISSIONS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    @OnClick(R.id.save_btn)
    public void handleSaveButtonClick(){
        //TODO: Put a progress bar
        if(fieldsAreValid()){
            person.setName(nameInput.getText().toString());
            person.setEmail(emailInput.getText().toString());
            person.setPhoneNumber(phoneInput.getText().toString());
            person.setUniversityUid(universityInput.getText().toString());

            ((MainActivity)getActivity()).quilloDatabase.updatePerson(person, getBytesFromBitmap(getBitmapFromPhoto(), 100), new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_SHORT);
                    getActivity().getSupportFragmentManager().popBackStack();
                    //Hide progress bar
                }
            });
        }

    }

    private boolean fieldsAreValid(){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String number = phoneInput.getText().toString();
        String university = universityInput.getText().toString();

        //TODO: Error handling with floating text labels

        ArrayList<String> supportedUniversities = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.universities)));

        if(name.isEmpty() || name.equals(" ")){
            nameInput.setError("Enter a name");
            return false;
        }

        if(email.isEmpty()){
            emailInput.setError("Enter a valid email");
            return  false;

        }



        if(university.isEmpty()|| !supportedUniversities.contains(university)){
            return  false;

        }

        return  true;

    }


    @Override
    public void getImagePath(Uri imagePath) {
        Glide.with(this).load(imagePath).into(profileImage);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        profileImage.setImageBitmap(bitmap);
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    private Bitmap getBitmapFromPhoto(){
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        return profileImage.getDrawingCache();
    }
}
