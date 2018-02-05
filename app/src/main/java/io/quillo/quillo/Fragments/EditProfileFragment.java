package io.quillo.quillo.Fragments;

import android.Manifest;
import android.content.DialogInterface;
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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;

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
    @BindView(R.id.input_password)
    EditText passwordInput;
    private Person person;
    private boolean userDidReauthenticate = false;
    private String oldPassword = "";

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

    @Override
    public void onResume() {
        super.onResume();
        showPasswordInputDialog();

    }

    private void showPasswordInputDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity(), android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("Verify your password");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.password_dialog, (ViewGroup)getView(), false);
        final EditText dialogPasswordInput = (EditText)view.findViewById(R.id.input_password);
        dialogPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        dialogPasswordInput.setHint("Password");

        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final String password = dialogPasswordInput.getText().toString();
                FirebaseUser user = FirebaseHelper.getCurrentFirebaseUser();
                if (user == null){
                    getActivity().getSupportFragmentManager().popBackStack();
                    return;
                }
                if(password == null || password.isEmpty()){
                    Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                    showPasswordInputDialog();
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d(EditProfileFragment.class.getName(), "User re authed");
                            passwordInput.setText(password);
                            userDidReauthenticate = true;
                            oldPassword = password;
                            dialogInterface.dismiss();
                        }else{
                            Toast.makeText(getContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                            showPasswordInputDialog();
                        }
                    }
                });


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        builder.show();

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
        if(!userDidReauthenticate){
            showPasswordInputDialog();
            return;
        }
        if(fieldsAreValid()){

            final FirebaseUser currentUser = FirebaseHelper.getCurrentFirebaseUser();
            person.setName(nameInput.getText().toString());
            person.setEmail(emailInput.getText().toString());
            person.setPhoneNumber(phoneInput.getText().toString());
            person.setUniversityUid(universityInput.getText().toString());

            AuthCredential authCredential = EmailAuthProvider.getCredential(currentUser.getEmail(), oldPassword);
            currentUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        ((MainActivity)getActivity()).quilloDatabase.updatePerson(person, getBytesFromBitmap(getBitmapFromPhoto(), 90), new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                if ((Boolean) o) {
                                    currentUser.updatePassword(passwordInput.getText().toString());
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    ((MainActivity) getActivity()).showProfileUpdateSuccess();
                                    ((MainActivity) getActivity()).saveUniversityUidToSharedPrefrences(universityInput.getText().toString());
                                    //Hide progress bar
                                }else{
                                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                    }
                });
            }



    }

    private boolean fieldsAreValid(){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String number = phoneInput.getText().toString();
        String password = passwordInput.getText().toString();
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

        if (password.isEmpty()){
            passwordInput.setError("Enter a password longer than 6 characters");
            return false;
        }

        //TODO: Make sure it is an actual number with a try catch
        if(number.length()> 12){
            phoneInput.setError("Enter a valid number");
            return false;
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
