package br.unb.igor.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.unb.igor.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextSenha;
    private CheckBox checkBoxConectado;
    private ImageView btnFacebook;
    private TextView criarConta;
    private TextView esqueciSenha;
    private Button btnEntrar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static String TAG = "LoginActivity";

    private boolean loggedFacebook;
    private boolean loggedEmail;
    private boolean loggedGoogle;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.emailLogin);
        editTextSenha = (EditText) findViewById(R.id.senhaLogin);
        btnFacebook = (ImageView) findViewById(R.id.btnFacebook);
        checkBoxConectado = (CheckBox) findViewById(R.id.checkBoxConectado);
        criarConta = (TextView) findViewById(R.id.txtCriarConta);
        esqueciSenha = (TextView) findViewById(R.id.txtEsqueciSenha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
            }
        });
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextEmail.getText().toString().length() == 0) {
                    editTextEmail.setError("Preencha com seu e-mail!");
                    editTextEmail.setTextColor(Color.BLACK);
                } else if (editTextSenha.getText().toString().length() == 0) {
                    editTextSenha.setError("Preencha com sua senha!");
                    editTextSenha.setTextColor(Color.BLACK);
                } else {
                    //LOGAR
                    Intent homeIntent = new Intent (LoginActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in_one,R.anim.fade_out);
                }
            }
        });

        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent criarContaIntent = new Intent(LoginActivity.this, CriarContaActivity.class);
                startActivity(criarContaIntent);
                overridePendingTransition(R.anim.fade_in_one, R.anim.fade_out);
            }
        });


        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    if (user.getProviders().contains("facebook.com")/* && AccessToken.getCurrentAccessToken() != null */) {
                        loggedFacebook = true;
                    } else if (user.getProviders().contains("google.com")) {
                        loggedGoogle = true;
                    } else {
                        if (mAuth.getCurrentUser().getDisplayName() != null) {
                            //mStatusTextView.setText(getString(R.string.signed_in_fmt, mAuth.getCurrentUser().getDisplayName()));
                        } else {
                            // mStatusTextView.setText(getString(R.string.signed_in_fmt, mAuth.getCurrentUser().getEmail()));
                        }
                        loggedEmail = true;
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    loggedGoogle = false;
                    loggedEmail = false;
                    loggedFacebook = false;
                }
                // ...
            }
        };
        
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
       // mGoogleApiClient = new GoogleApiClient.Builder(this)
         //       .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
         //       .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
         //       .build();
        // [END build_client]

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]
    }
}
