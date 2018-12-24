package youth.electicsynery.com.managment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Created by Emeka on 7/12/2017.
 */

public class Registration extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String email,password,organization;
    private EditText nameEntered, passwordEntered, conPass;
    private ProgressDialog bar;
    private final static String TAG = "Ticketing";
    private DataStorage dataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        mAuth = FirebaseAuth.getInstance();
        dataStorage = new DataStorage(this);

        nameEntered = findViewById(R.id.et_reg_email);
        passwordEntered = findViewById(R.id.reg_password);
        conPass = findViewById(R.id.pass_con);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    veriEmail(user);
                    Toast.makeText(Registration.this,"Please check your email to verify your account",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Registration.this,Main2Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        ImageButton back = findViewById(R.id.quitPage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        Button submit_button = findViewById(R.id.submit);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar = new ProgressDialog(Registration.this);//creates a new progress dialog.
                bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//sets what direction the bar runs in.
                bar.setMessage("Signing Up");//shows a message.
                checkErrors();
            }
        });
    }

    private void checkErrors() {
        email = nameEntered.getText().toString();
        password = passwordEntered.getText().toString();
        String conpass = conPass.getText().toString();
        if (email.trim().isEmpty() || password.trim().isEmpty() || conpass.trim().isEmpty()
                || !conpass.trim().equals(password.trim())
                || password.trim().length() < 6
                || conpass.trim().length() < 6
                || !isValidEmailAddress(email.trim().toLowerCase())) {
            if (email.trim().isEmpty()){
                nameEntered.setError("Please enter an Email address");
                nameEntered.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        nameEntered.setError(null);
                    }
                });
            }
            if (!isValidEmailAddress(email.trim().toLowerCase())){
                nameEntered.setError("Email entered is invalid");
                nameEntered.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        nameEntered.setError(null);
                    }
                });
            }
            if (password.trim().isEmpty()) {
                passwordEntered.setError("Please enter a password");
                passwordEntered.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        passwordEntered.setError(null);
                    }
                });
            }
            if (password.trim().length() < 6) {
                passwordEntered.setError("Password must be at least 6 characters long");
                passwordEntered.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        passwordEntered.setError(null);
                    }
                });
            }
            if (conpass.trim().isEmpty()) {
                conPass.setError("Please confirm password entered");
                conPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        conPass.setError(null);
                    }
                });
            }
            if (conpass.trim().length() < 6) {
                conPass.setError("Password must be at least 6 characters long");
                conPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        passwordEntered.setError(null);
                    }
                });
            }
            if (!conpass.trim().equals(password.trim())) {
                conPass.setError("Passwords do not match");
                conPass.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        conPass.setError(null);
                    }
                });
            }
        }
        else {
            // Do action here
            bar.show();
            SignUP(email.toLowerCase().trim(),password);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void SignUP(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){

                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Registration.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void veriEmail(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            System.out.println("Address Exception: "+ex.getMessage());
            result = false;
        }
        return result;
    }
}
