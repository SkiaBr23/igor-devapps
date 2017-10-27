package br.unb.igor.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import br.unb.igor.R;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.LocalStorage;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.User;

public class ActivitySignup extends AppCompatActivity {

    private static String TAG = ActivitySignup.class.getName();

    EditText _nameText;
    EditText _emailText;
    EditText _passwordText;
    Button _signupButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText _genderText;
    private EditText _birthDateText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        mAuth = FirebaseAuth.getInstance();

        this._nameText = findViewById(R.id.nomeUsuarioLoginCadastro);
        this._emailText = findViewById(R.id.emailLoginCadastro);
        this._passwordText = findViewById(R.id.senhaLoginCadastro);
        this._signupButton = findViewById(R.id.btnCriarConta);
        this._genderText = findViewById(R.id.sexoLoginCadastro);
        this._birthDateText = findViewById(R.id.dataNascimentoLoginCadastro);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onBackPressed() {
        // code here to show dialog
        finish();  // optional depending on your needs
        overridePendingTransition(0, R.anim.fade_out_320ms);
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

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(null);
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ActivitySignup.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.msg_registering_account));
        progressDialog.show();

        final String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        final String gender = _genderText.getText().toString();
        final String birthDate = _birthDateText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        onSignupFailed(task.getException());
                    } else {
                        setupNewUser(mAuth.getCurrentUser(), name, gender, birthDate);
                        onSignupSuccess(mAuth.getCurrentUser());
                    }

                    progressDialog.dismiss();
                }
            });
    }


    public void onSignupSuccess(FirebaseUser user) {
        _signupButton.setEnabled(true);
        final SharedPreferences sharedPreferences = LocalStorage.get(this);
        sharedPreferences
            .edit()
            .putString(LocalStorage.KEY_EMAIL, user.getEmail())
            .apply();
        Intent data = new Intent();
        data.putExtra(User.PARCEL_KEY_EMAIL, user.getEmail());
        setResult(RESULT_OK, data);
        finish();
    }

    private void setupNewUser(FirebaseUser user, String displayName, String gender, String birthday) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .setPhotoUri(Uri.parse(User.DEFAULT_PROFILE_PHOTO_URL))
            .build();
        user.updateProfile(profileUpdates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirebaseAuth.getInstance().signOut();
                            }
                        }, 200);
                    }
                }
            });
        final User newUser = new User(user.getUid(), displayName, user.getEmail(), User.DEFAULT_PROFILE_PHOTO_URL);
        newUser.setGender(gender);
        newUser.setBirthDate(birthday);
        final DB db = new DB(null, FirebaseDatabase.getInstance().getReference());
        db.upsertUser(newUser);
    }

    public void onSignupFailed(Exception reason) {
        if (reason != null) {
            String message = reason.getMessage();
            if (message.contains("WEAK_PASSWORD")) {
                message = getString(R.string.msg_password_too_weak);
            } else if (message.contains("The email address is already in use")) {
                message = getString(R.string.msg_email_already_in_use);
            } else if (message.contains("The email address is badly formatted")) {
                message = "O e-mail fornecido não é valido";
            } else if (message.contains("Password should be at least")) {
                message = "A senha fornecida é muito curta";
            } else {
                System.out.println(message);
                message = getString(R.string.msg_failed_to_register_account);
            }
            Toast.makeText(ActivitySignup.this, message, Toast.LENGTH_SHORT).show();
        }
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.requestFocus();
            _passwordText.setError("entre 4 e 10 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.requestFocus();
            _emailText.setError("insira um endereço de email válido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (name.isEmpty() || name.length() < 3) {
            _nameText.requestFocus();
            _nameText.setError("pelo menos 3 dígitos!");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        return valid;
    }
}
