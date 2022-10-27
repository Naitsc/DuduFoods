package com.naitsc.dudufoods;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {

    private CircleImageView ftoUser;
    private Button btFto, btCadastrar;
    private EditText editNome, editEmail, editSenha;
    private TextView txtMsgErro;

    private String usuarioID;
    private Uri mSelecionarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        IniciarComponentes();
        editNome.addTextChangedListener(cadastroTxtWatcher);
        editEmail.addTextChangedListener(cadastroTxtWatcher);
        editSenha.addTextChangedListener(cadastroTxtWatcher);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CadastrarUsers(view);

            }
        });

        btFto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelecionarFtoGallery();

            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        mSelecionarUri = data.getData();

                        try {
                            ftoUser.setImageURI(mSelecionarUri);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                }
            }
    );

    public void CadastrarUsers(View view) {

        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    SalvarDadosUsers();
                    Snackbar snackbar = Snackbar.make(view, "Cadastro realizado com sucesso!", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                    snackbar.show();
                } else {

                    String erro;

                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Coloque uma senha com no minimo 6 caracteres!";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email invalido!";

                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Está conta já foi cadastrada!";

                    } catch (FirebaseNetworkException e) {
                        erro = "Sem conexão com a internet!";

                    } catch (Exception e) {
                        erro = "Erro ao cadastrar usuário";

                    }
                    txtMsgErro.setText(erro);
                }

            }
        });


    }

    public void SelecionarFtoGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);

    }

    public void SalvarDadosUsers() {

        String nomeArquivo = UUID.randomUUID().toString();

        final StorageReference reference = FirebaseStorage.getInstance().getReference("/img/" + nomeArquivo);
        reference.putFile(mSelecionarUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String fto = uri.toString();

                                //Iniciar o banco de dados - Firestore
                                String nome = editNome.getText().toString();
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                Map<String,Object> usuarios = new HashMap<>();
                                usuarios.put("nome",nome);
                                usuarios.put("foto", fto);

                                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                DocumentReference documentReference = db.collection("Users").document(usuarioID);
                                documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.i("db", "Sucesso!!!!");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("dbErro", "Eroo ao salvar!!!!" + e.toString());

                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void IniciarComponentes() {
        ftoUser = findViewById(R.id.ftoUsuario);
        btFto = findViewById(R.id.btSelecionarFto);
        btCadastrar = findViewById(R.id.btnCadastrar);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        txtMsgErro = findViewById(R.id.txtMsgErro);

    }

    TextWatcher cadastroTxtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
                btCadastrar.setEnabled(true);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.dark_red));

            } else {
                btCadastrar.setEnabled(false);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.gray));

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}