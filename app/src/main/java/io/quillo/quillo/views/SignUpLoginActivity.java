package io.quillo.quillo.views;

/**
 * Created by Stickells on 15/01/2018.
 */

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.os.Bundle;
    import android.support.v7.app.AppCompatActivity;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import butterknife.BindView;
    import butterknife.ButterKnife;
    import butterknife.OnClick;
    import io.quillo.quillo.R;

public class SignUpLoginActivity extends AppCompatActivity {
    private static final String TAG = "SignUpLoginActivity";
    private boolean isLoggingIn = true;

    @BindView(R.id.input_name) EditText inputName;
    @BindView(R.id.input_name_holder) View inputNameHolder;
    @BindView(R.id.input_email) EditText inputEmail;
    @BindView(R.id.input_email_holder) View inputEmailHolder;
    @BindView(R.id.input_password) EditText inputPassword;
    @BindView(R.id.btn_signup_login) Button signupLoginButton;
    @BindView(R.id.btn_register_toggle) Button signupToggle;
    @BindView(R.id.btn_login_toggle) Button loginToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_signup_login);
        ButterKnife.bind(this);

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
    }

    @OnClick(R.id.btn_register_toggle)
    public void handleRegisterToggleClick(View v) {
        isLoggingIn = false;

        loginToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.Primary));
        loginToggle.setTextColor(v.getContext().getResources().getColor(R.color.WHITE));
        signupToggle.setBackgroundColor(v.getContext().getResources().getColor(R.color.WHITE));
        signupToggle.setTextColor(v.getContext().getResources().getColor(R.color.Primary));

        inputNameHolder.setVisibility(View.VISIBLE);
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
        signupLoginButton.setText("LOGIN");
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpLoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        signupLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpLoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // TODO: Implement authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        signupLoginButton.setEnabled(true);
        setResult(RESULT_OK, null);

        // TODO WHERE TO FROM HERE...
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        signupLoginButton.setEnabled(true);
    }

    public void onLoginSuccess() {
        signupLoginButton.setEnabled(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signupLoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!isLoggingIn && (name.isEmpty() || name.length() < 3)) {
            inputName.setError("at least 3 characters");
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_SIGNUP) {
//            if (resultCode == RESULT_OK) {
//
//                // TODO: WHERE DO WE GO NOW?
//
//            }
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // disable going back to the MainActivity
        // moveTaskToBack(true);
    }
}