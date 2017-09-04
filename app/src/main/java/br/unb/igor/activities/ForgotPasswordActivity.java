package br.unb.igor.activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.unb.igor.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailForgotPassword;
    private Button buttonForgotPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailForgotPassword = findViewById(R.id.emailForgotPassword);
        buttonForgotPassword = findViewById(R.id.btnCriarConta);
        auth = FirebaseAuth.getInstance();

        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String email = emailForgotPassword.getText().toString();
            if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailForgotPassword.setError("Insira um e-mail válido");
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Email de redefinição de senha enviado!", Toast.LENGTH_SHORT).show();
                                callLoginActivity();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Falha no envio do email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out_320ms);
    }

    private void callLoginActivity(){
        finish();
    }

}
