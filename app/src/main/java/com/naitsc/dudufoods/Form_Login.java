package com.naitsc.dudufoods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class Form_Login extends AppCompatActivity {

    private EditText edit_email, edit_senha;
    private Button bt_entrar;
    private TextView txtCriarConta, txt_msgErro;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);

        getSupportActionBar().hide();
        IniciarComponentes();

        txtCriarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Form_Login.this, Form_Cadastro.class);
                startActivity(intent);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()) {
                    txt_msgErro.setText("Preencha todos os campos!");

                } else {
                    txt_msgErro.setText("");
                    AutenticarUsuarios();

                }

            }
        });
    }

    public void AutenticarUsuarios() {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            iniciarTelaProdutos();

                        }
                    }, 2000);

                } else {
                    String erro;

                    try {
                        throw task.getException();

                    } catch (Exception e) {
                        erro = "Erro ao logar!";
                    }
                    txt_msgErro.setText(erro);
                }


            }

        });

    }

    public void iniciarTelaProdutos() {
        Intent intent = new Intent(Form_Login.this, Lista_Produtos.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioAtual != null) {
            iniciarTelaProdutos();
        }
    }

    public void IniciarComponentes() {
        txtCriarConta = findViewById(R.id.txtCriarConta);
        edit_email = findViewById(R.id.editEmail);
        edit_senha = findViewById(R.id.editSenha);
        bt_entrar = findViewById(R.id.btnEntrar);
        txt_msgErro = findViewById(R.id.txtMsgErro);
        progressBar = findViewById(R.id.progressBar);
    }
}