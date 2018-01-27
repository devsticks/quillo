package io.quillo.quillo.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.quillo.quillo.R;
import io.quillo.quillo.controllers.MainActivity;
import io.quillo.quillo.data.FirebaseHelper;
import io.quillo.quillo.data.Person;
import io.quillo.quillo.interfaces.PersonListener;

/**
 * Created by shkla on 2018/01/26.
 */

public class LoginSignupFragment extends Fragment {
    private static final String TAG = "SignUpLoginActivity";
    private boolean isLoggingIn = true;
    private FirebaseAuth auth;

    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.input_name_holder) View inputNameHolder;
    @BindView(R.id.input_university_holder) View inputUniversityHolder;
    @BindView(R.id.input_university) EditText inputUniversity;
    @BindView(R.id.input_email) EditText inputEmail;
    @BindView(R.id.input_email_holder) View inputEmailHolder;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_signup_login)
    Button signupLoginButton;
    @BindView(R.id.btn_register_toggle) Button signupToggle;
    @BindView(R.id.btn_login_toggle) Button loginToggle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_login, container, false);
        ButterKnife.bind(this, view);
        setUpView(view);
        return view;
    }

    public void setUpView(View view){
        signupLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggingIn) {
                    login();
                } else {
                    signup();
                }
            }
        });

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String universityUid = sharedPreferences.getString(getString(R.string.shared_pref_university_key), null);
        if(universityUid != null){
            inputUniversity.setText(universityUid);
        }
    }

    @OnClick(R.id.btn_register_toggle)
    public void handleRegisterToggleClick(View v) {
        isLoggingIn = false;

        loginToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.Primary));
        loginToggle.setTextColor(v.getContext().getResources().getColor(R.color.WHITE));
        signupToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.WHITE));
        signupToggle.setTextColor(v.getContext().getResources().getColor(R.color.Primary));

        inputNameHolder.setVisibility(View.VISIBLE);
        inputUniversityHolder.setVisibility(View.VISIBLE);
//        TODO GET FOCUS SWAPPING TO WORK
//        inputNameHolder.requestFocus();
//        inputName.requestFocus();
        signupLoginButton.setText("REGISTER");
    }

    @OnClick(R.id.btn_login_toggle)
    public void handleLoginToggleClick(View v) {
        isLoggingIn = true;

        signupToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.Primary));
        signupToggle.setTextColor(v.getContext().getResources().getColor(R.color.WHITE));
        loginToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.WHITE));
        loginToggle.setTextColor(v.getContext().getResources().getColor(R.color.Primary));

//        TODO GET FOCUS SWAPPING TO WORK
//        inputEmail.requestFocus();
        inputNameHolder.setVisibility(View.GONE);
        inputUniversityHolder.setVisibility(View.GONE);
        signupLoginButton.setText("LOGIN");
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!registerFieldsAreValid()) {
            onSignupFailed();
            return;
        }

        signupLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // TODO: Implement your own signup logic here.

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    updateUserName(name);
                    progressDialog.cancel();
                }else{
                    onSignupFailed();
                    progressDialog.cancel();
                }

            }
        });
    }

    private void updateUserName(String name){
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        auth.getCurrentUser().updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    onSignupSuccess();
                }
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!loginFieldsAreValid()) {
            onLoginFailed();
            return;
        }

        signupLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onLoginSuccess();
                    progressDialog.cancel();
                }else{
                    onLoginFailed();
                    progressDialog.cancel();
                }
            }
        });

        // TODO: Implement authentication logic here.

    }

    public void onSignupSuccess() {
        signupLoginButton.setEnabled(true);



        String university = inputUniversity.getText().toString();

        FirebaseUser user = auth.getCurrentUser();
        //TODO Link with an actual uni UID
        Person person = new Person(user.getUid(), user.getDisplayName(), user.getEmail(), university);
        ((MainActivity)getActivity()).quilloDatabase.addPerson(person);

        Toast.makeText(getActivity(), "Welcome: " + auth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(university);

        getActivity().getSupportFragmentManager().popBackStack();
    }

    public void onSignupFailed() {
        Toast.makeText(getActivity(), "Signup failed", Toast.LENGTH_LONG).show();

        signupLoginButton.setEnabled(true);
    }

    public void onLoginSuccess() {
        signupLoginButton.setEnabled(true);
        final String personUid = FirebaseHelper.getCurrentUserUid();

        ((MainActivity)getActivity()).quilloDatabase.loadPerson(personUid, new PersonListener() {
            @Override
            public void onPersonLoaded(Person person) {
                String universityUid = person.getUniversityUid();
                ((MainActivity)getActivity()).saveUniversityUidToSharedPrefrences(universityUid);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void onLoginFailed() {
        Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
        signupLoginButton.setEnabled(true);
    }

    private boolean loginFieldsAreValid(){

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            return false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            return false;
        } else {
            inputPassword.setError(null);
        }


        return true;

    }
    //TODO : Make a method for checking password and email to minimise code re use
    private boolean registerFieldsAreValid(){
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String name = inputName.getText().toString();
        String university = inputName.getText().toString();

        ArrayList<String> supportedUniversities  = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.universities)));

        if (name.isEmpty() || name.length() < 3){
            inputName.setError("at least 3 characters");
            return false;
        }

        if(!supportedUniversities.contains(university)){
            inputUniversity.setError("Not a supported uni");
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            return false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            return false;
        } else {
            inputPassword.setError(null);
        }

        return true;

    }



}
