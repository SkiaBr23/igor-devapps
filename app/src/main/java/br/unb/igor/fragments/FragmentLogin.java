package br.unb.igor.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.unb.igor.R;

public class FragmentLogin extends Fragment {

    private static String TAG = FragmentLogin.class.getName();

    private EditText editTextEmail;
    private EditText editTextSenha;
    private CheckBox checkBoxConectado;
    private ImageView btnFacebook;
    private TextView criarConta;
    private TextView esqueciSenha;
    private Button btnEntrar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean loggedFacebook;
    private boolean loggedEmail;
    private boolean loggedGoogle;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.login, container, false);

        editTextEmail = (EditText) root.findViewById(R.id.emailLogin);
        editTextSenha = (EditText) root.findViewById(R.id.senhaLogin);
        btnFacebook = (ImageView) root.findViewById(R.id.btnFacebook);
        checkBoxConectado = (CheckBox) root.findViewById(R.id.checkBoxConectado);
        criarConta = (TextView) root.findViewById(R.id.txtCriarConta);
        esqueciSenha = (TextView) root.findViewById(R.id.txtEsqueciSenha);
        btnEntrar = (Button) root.findViewById(R.id.btnEntrar);
        signInButton = (SignInButton) root.findViewById(R.id.sign_in_button);

        signInButton.setSize(SignInButton.SIZE_STANDARD);

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
            }
            }
        });

        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getActivity()
                .getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in_one, R.animator.fade_out)
                .replace(R.id.main_container, new FragmentCriarConta())
                .addToBackStack(null)
                .commit();
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

        return root;
    }
}
