package br.unb.igor.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.unb.igor.R;
import br.unb.igor.model.User;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static String TAG = LoginActivity.class.getName();

    private EditText editTextEmail;
    private EditText editTextSenha;
    private CheckBox checkBoxConectado;
    private ImageView btnFacebook;
    private String DEFAULT_PROFILE_PHOTO_URL = "http://www.alass.org/wp-content/uploads/default.png";
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
    private FirebaseDatabase database;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        //Input
        editTextEmail = (EditText) findViewById(R.id.emailLogin);
        editTextSenha = (EditText) findViewById(R.id.senhaLogin);
        btnFacebook = (ImageView) findViewById(R.id.btnFacebook);
        checkBoxConectado = (CheckBox) findViewById(R.id.checkBoxConectado);
        criarConta = (TextView) findViewById(R.id.txtCriarConta);
        esqueciSenha = (TextView) findViewById(R.id.txtEsqueciSenha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setSize(SignInButton.SIZE_STANDARD);

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPasswordIntent = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
                overridePendingTransition(R.anim.fade_in_one,R.anim.fade_in_one);
            }
        });

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
            if(loggedGoogle || loggedFacebook || loggedEmail) {
                userDataRegistered(mAuth.getCurrentUser());
                callMainActivity();
            } else {
                if (editTextEmail.getText().toString().length() == 0) {
                    editTextEmail.setError("Preencha com seu e-mail!");
                    editTextEmail.setTextColor(Color.BLACK);
                } else if (editTextSenha.getText().toString().length() == 0) {
                    editTextSenha.setError("Preencha com sua senha!");
                    editTextSenha.setTextColor(Color.BLACK);
                } else {
                    loginWithPassword();
                }
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

        // [START customize_button]
        // Set the dimensions of the sign-in button.
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // [END customize_button]


        // FACEBOOK


        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
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
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
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

    private void callSignupActivity() {
        // Start the Signup activity
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
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
        } else if(mAuth.getCurrentUser() == null || loggedFacebook) {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else  if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    // [END onActivityResult]
    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            loggedGoogle = true;
            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //  registerGoogleUserFirebase(acct);
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            loggedGoogle = false;
            updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        updateUI(false);

        if(loggedGoogle) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            updateUI(false);
                            loggedGoogle = false;
                            // [END_EXCLUDE]
                        }
                    });
        }

        if(loggedFacebook){
            LoginManager.getInstance().logOut();
        }
    }
    // [END signOut]


    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            //findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //this.getActivity().findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        } else {
            //mStatusTextView.setText(R.string.signed_out);

            //findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //this.getActivity().findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }

    public void callMainActivity(){
        Intent mainIntent = new Intent(LoginActivity.this,ActivityHome.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.fade_in_one,R.anim.fade_in_one);
        finish();
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

                //registerUserFirebase(mAuth.getCurrentUser());
                // Modo debug: retirar esta chamada para teste de login
                if(loggedGoogle || loggedFacebook || loggedEmail) {
                    userDataRegistered(mAuth.getCurrentUser());
                    callMainActivity();
                } else {
                    loginWithPassword();
                }
                break;
           /* case R.id.sign_out_button:
                signOut();
                break;*/
            //  case R.id.disconnect_button:
            //    revokeAccess();
            //    break;
        }
    }

    private void registerUserFirebase(FirebaseUser firebaseUser){
        String photoUrl;
        if(firebaseUser.getPhotoUrl() == null){
            setDefaultPhoto(firebaseUser);
            photoUrl = DEFAULT_PROFILE_PHOTO_URL;
        } else {
            photoUrl = firebaseUser.getPhotoUrl().toString();
        }
        User newUser = new User(firebaseUser.getUid(),firebaseUser.getDisplayName(),firebaseUser.getEmail(),photoUrl);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        DatabaseReference usersRef = myRef.child("users");
        usersRef.child(firebaseUser.getUid());
        usersRef.child(firebaseUser.getUid()).setValue(newUser);
        Log.d(TAG,"Gravado no db");
    }

    private void setDefaultPhoto(FirebaseUser user){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(DEFAULT_PROFILE_PHOTO_URL))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            userDataRegistered(mAuth.getCurrentUser());
                        }
                        // ...
                    }
                });
    }

    public void loginWithPassword() {
        Log.d(TAG, "LoginWithPassword");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        btnEntrar.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
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
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Login falhou!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed

                        if (mAuth.getCurrentUser() != null) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                        }
                        //
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    public void onLoginSuccess() {
        this.btnEntrar.setEnabled(true);
        userDataRegistered(mAuth.getCurrentUser());
        callMainActivity();
    }

    public void onLoginFailed() {
        Toast.makeText(this, "Login falhou!", Toast.LENGTH_LONG).show();

        this.btnEntrar.setEnabled(true);
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
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loggedFacebook = false;
                        } else {
                            loggedFacebook = true;
                            // mStatusTextView.setText(getString(R.string.signed_in_fmt, mAuth.getCurrentUser().getDisplayName()));
                        }

                        // ...
                    }
                });
    }

    public boolean validate() {
        boolean valid = true;

        String email = editTextEmail.getText().toString();
        String password = editTextSenha.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("insira um e-mail válido");
            valid = false;
        } else {
            editTextEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editTextSenha.setError("entre 4 e 10 caracteres");
            valid = false;
        } else {
            editTextSenha.setError(null);
        }

        return valid;
    }


    public void userDataRegistered(final FirebaseUser firebaseUser) {
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        mDatabase.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user == null){
                            registerUserFirebase(firebaseUser);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
