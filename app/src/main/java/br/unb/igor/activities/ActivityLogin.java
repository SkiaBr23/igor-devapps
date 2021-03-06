package br.unb.igor.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import br.unb.igor.R;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.LocalStorage;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.User;

public class ActivityLogin extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static final String TAG = ActivityLogin.class.getName();

    private EditText editTextEmail;
    private EditText editTextSenha;
    private ImageView btnFacebook;
    private TextView criarConta;
    private TextView esqueciSenha;
    private Button btnEntrar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_SIGNUP = 0;
    private boolean loggedFacebook;
    private boolean loggedEmail;
    private boolean loggedGoogle;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private CallbackManager mCallbackManager;
    private LoginButton fbLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private ProgressDialog mProgressDialog;
    private TextView separador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        //Input
        editTextEmail = findViewById(R.id.emailLogin);
        editTextSenha = findViewById(R.id.senhaLogin);
        btnFacebook = findViewById(R.id.btnFacebook);
        criarConta = findViewById(R.id.txtCriarConta);
        esqueciSenha = findViewById(R.id.txtEsqueciSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        signInButton = findViewById(R.id.sign_in_button);
        separador = findViewById(R.id.txtSeparador);

        // Set Fira Sans (Regular) font
        Typeface firaSans = Typeface.createFromAsset(getAssets(), "FiraSans-Regular.ttf");
        Typeface firaSansBold = Typeface.createFromAsset(getAssets(), "FiraSans-Bold.ttf");
        editTextEmail.setTypeface(firaSans);
        editTextSenha.setTypeface(firaSans);
        criarConta.setTypeface(firaSans);
        esqueciSenha.setTypeface(firaSans);
        btnEntrar.setTypeface(firaSansBold);
        separador.setTypeface(firaSans);

        final SharedPreferences sharedPreferences = LocalStorage.get(this);
        String emailStringCache = sharedPreferences.getString(LocalStorage.KEY_EMAIL, "");
        editTextEmail.setText(emailStringCache);

        if (emailStringCache.isEmpty()) {
            editTextEmail.requestFocus();
        } else {
            editTextSenha.requestFocus();
        }

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(ActivityLogin.this,ActivityForgotPassword.class);
                startActivity(forgotPasswordIntent);
                overridePendingTransition(R.anim.fade_in_320ms, R.anim.fade_none);
            }
        });

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginButton.performClick();
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoggedIn()) {
//                    btnEntrar.setEnabled(false);
//                    onLoginSuccess(null);
                } else if (validate()) {
                    sharedPreferences
                        .edit()
                        .putString(LocalStorage.KEY_EMAIL, editTextEmail.getText().toString())
                        .apply();
                    loginWithPassword();
                }
            }
        });

        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSignupActivity();
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
                if (user.getProviders().contains("facebook.com") && AccessToken.getCurrentAccessToken() != null) {
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
         //Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // FACEBOOK
        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email", "public_profile");
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                Toast.makeText(ActivityLogin.this, R.string.msg_failed_to_login_with_facebook, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(ActivityLogin.this, R.string.msg_failed_to_login_with_facebook, Toast.LENGTH_LONG).show();
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                signOut();
            }
            }
        };
    }

    private boolean isLoggedIn() {
        return loggedGoogle || loggedFacebook || loggedEmail;
    }

    private void callSignupActivity() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), ActivitySignup.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        overridePendingTransition(R.anim.fade_in_320ms, R.anim.fade_none);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else if (mAuth.getCurrentUser() == null || loggedFacebook) {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (resultCode == RESULT_OK) {
                showProgressDialog(getString(R.string.msg_logging_in));
                handleSignInResult(result);
            } else {
                if (resultCode != RESULT_CANCELED) {
                    Toast.makeText(this, "Falha ao realizar login com Google", Toast.LENGTH_SHORT).show();
                    System.out.println(result.getStatus().toString());
                }
            }
        } else  if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String email = data.getStringExtra(User.PARCEL_KEY_EMAIL);
                    if (email != null && !email.isEmpty()) {
                        editTextEmail.setText(data.getStringExtra(User.PARCEL_KEY_EMAIL));
                        editTextSenha.setText("");
                        editTextSenha.requestFocus();
                    }
                }
                Toast.makeText(this, R.string.msg_successfully_registered, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        signOut();
                    }
                }, 200);
            } else {
//                Toast.makeText(this, R.string.msg_failed_to_register, Toast.LENGTH_SHORT).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            loggedGoogle = true;
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            loggedGoogle = false;
            hideProgressDialog();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        if (loggedGoogle) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        loggedGoogle = false;
                    }
                });
        }

        if (loggedFacebook) {
            LoginManager.getInstance().logOut();
            loggedFacebook = false;
        }

        loggedEmail = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
            mProgressDialog.setIndeterminate(true);
        }

        if (text != null)
            mProgressDialog.setMessage(text);
        else
            mProgressDialog.setMessage(getString(R.string.label_loading));

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFacebook:
                fbLoginButton.performClick();
                break;
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btnEntrar:
                if (isLoggedIn()) {
//                    btnEntrar.setEnabled(false);
//                    onLoginSuccess(null);
                } else {
                    loginWithPassword();
                }
                break;
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        hideProgressDialog();
                        Toast.makeText(ActivityLogin.this, R.string.msg_google_login_failed,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        onLoginSuccess(task.getResult().getUser());
                    }
                }
            });
    }

    public void loginWithPassword() {
        if (!validate()) {
            return;
        }
        btnEntrar.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.msg_authenticating));
        progressDialog.show();

        String email = editTextEmail.getText().toString();
        String password = editTextSenha.getText().toString();

        //myRef.authWithPassword("test@firebaseuser.com", "test1234", new AuthResultHandler("password"));
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (task.isSuccessful()) {
                        onLoginSuccess(null);
                    } else {
                        onLoginFailed(task.getException());
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                    }

                    progressDialog.cancel();
                }
            });
    }

    public void onLoginSuccess(final FirebaseUser gmailOrFacebookUser) {
        if (gmailOrFacebookUser != null) {
            insertGoogleOrFacebookUserAndLogin(gmailOrFacebookUser);
        } else {
            hideProgressDialog();
            Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in_320ms, R.anim.fade_out_320ms);
        }
    }

    public void onLoginFailed(Exception exception) {
        if (exception != null &&
            (exception instanceof FirebaseAuthInvalidUserException ||
             exception instanceof FirebaseAuthInvalidCredentialsException)) {
            Toast.makeText(this, R.string.msg_invalid_login, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.msg_login_not_possible, Toast.LENGTH_LONG).show();
        }
        this.btnEntrar.setEnabled(true);
    }

    private void insertGoogleOrFacebookUserAndLogin(FirebaseUser user) {
        String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : User.DEFAULT_PROFILE_PHOTO_URL;
        final User newUser = new User(user.getUid(), user.getDisplayName(), user.getEmail(), photoUrl);
        final OnCompleteHandler handler = new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                hideProgressDialog();
                Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in_320ms, R.anim.fade_out_320ms);
            }
        });
        DB.getUserInfoById(user.getUid(), new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                if (cancelled) {
                    return;
                }
                if (extra == null) {
                    DB.upsertUser(newUser, handler);
                } else {
                    handler.advance();
                }
            }
        }));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(ActivityLogin.this, R.string.msg_failed_to_login_with_facebook,
                                Toast.LENGTH_LONG).show();
                        loggedFacebook = false;
                    } else {
                        loggedFacebook = true;
                        onLoginSuccess(task.getResult().getUser());
                    }
                }
            });
    }

    public boolean validate() {

        String email = editTextEmail.getText().toString();
        String password = editTextSenha.getText().toString();

        if (email.isEmpty()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getString(R.string.msg_login_empty_email));
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.requestFocus();
            editTextEmail.setError(getString(R.string.msg_login_invalid_email));
            return false;
        } else {
            editTextEmail.setError(null);
        }

        if (password.isEmpty()) {
            editTextSenha.requestFocus();
            editTextSenha.setError(getString(R.string.msg_login_empty_password));
            return false;
        } else if (password.length() < 4 || password.length() > 10) {
            editTextSenha.requestFocus();
            editTextSenha.setError(getString(R.string.msg_password_invalid_length));
            return false;
        } else {
            editTextSenha.setError(null);
        }

        return true;
    }
}
