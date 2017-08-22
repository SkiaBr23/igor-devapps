package br.unb.igor.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import br.unb.igor.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextSenha;
    private CheckBox checkBoxConectado;
    private TextView criarConta;
    private TextView esqueciSenha;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText)findViewById(R.id.emailLogin);
        editTextSenha = (EditText)findViewById(R.id.senhaLogin);
        checkBoxConectado = (CheckBox)findViewById(R.id.checkBoxConectado);
        criarConta = (TextView)findViewById(R.id.txtCriarConta);
        esqueciSenha = (TextView)findViewById(R.id.txtEsqueciSenha);
        btnEntrar = (Button)findViewById(R.id.btnEntrar);

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
                Intent criarContaIntent = new Intent (LoginActivity.this, CriarContaActivity.class);
                startActivity(criarContaIntent);
                overridePendingTransition(R.anim.fade_in_one,R.anim.fade_out);
            }
        });
    }
}
