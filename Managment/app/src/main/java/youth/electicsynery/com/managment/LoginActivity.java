package youth.electicsynery.com.managment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import static youth.electicsynery.com.managment.Constants.checkVerified;
import static youth.electicsynery.com.managment.Constants.getLog;
import static youth.electicsynery.com.managment.Constants.setLog;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "Ticket";
    private ProgressBar bar;
    private EditText passField;
    private Button login_button;
    private String email,password;
    private EditText nameEntered, passwordEntered;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passField = findViewById(R.id.password_editText);
        progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);

        bar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (getLog(LoginActivity.this)){
                        checkVerified = false;
                        Intent intent = new Intent(LoginActivity.this,VerifyActivity.class);
                        startActivity(intent);
                    }
                    else if (checkVerified){
                        if (user.isEmailVerified()){
                            // User is signed in
                            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
                            System.out.println("user_id is: " + user.getUid());
                            System.out.println("display name is: " + user.getDisplayName());
                            System.out.println("email is: " + user.isEmailVerified());
                            setLog(LoginActivity.this,true);
                            intent.putExtra("user_id",user.getUid());
                            startActivity(intent);
                            finish();
                        }
                        else {
                            checkVerified = false;
                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                    else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
                }

            }
        };

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,Registration.class);
                startActivity(intent);
                finish();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        passField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (passField.getRight() - passField.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        if (passField.getTransformationMethod() == null){
                            passField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_red_eye_black_24dp, 0);
                        }
                        else {
                            passField.setTransformationMethod(null);
                            passField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_black_24dp, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bar = new ProgressDialog(LoginActivity.this);//creates a new progress dialog.
                //bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);//sets what direction the bar runs in.
                //bar.setMessage("Signing Up");//shows a message.

                nameEntered = findViewById(R.id.et_username);
                passwordEntered = findViewById(R.id.password_editText);

                email = nameEntered.getText().toString();
                password = passwordEntered.getText().toString();
                if (email.trim().isEmpty()) {
                    nameEntered.setError("Please enter an email address");
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
                }else {
                    // Do action here
                    //bar.show();
                    bar.setVisibility(View.VISIBLE);
                    login_button.setVisibility(View.GONE);
                    checkVerified = true;
                    SignIn(email,password);
                }
            }
        });

        //yourTextView.setTransformationMethod(new DoNothingTransformation());
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

    private void SignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //bar.dismiss();
                        bar.setVisibility(View.GONE);
                        login_button.setVisibility(View.VISIBLE);
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        try{
                            Log.d(TAG,task.getResult().toString());
                        }
                        catch (RuntimeExecutionException e){
                            e.printStackTrace();
                            //Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed_login,
                                    Toast.LENGTH_SHORT).show();
                        }


                        // ...
                    }
                });
    }
}
