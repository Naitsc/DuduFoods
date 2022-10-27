package com.naitsc.dudufoods;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editar_Perfil extends AppCompatActivity {

    private CircleImageView fotoUsuario;
    private EditText editNome;
    private Button btAtualizarDados, btSelecionarFto;
    private Uri mSelecionarUri;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        iniciarComponentes();

        btSelecionarFto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelecionarFtoGallery();

            }
        });

        btAtualizarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = editNome.getText().toString();

                if (nome.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
                    snackbar.show();

                } else {
                    AtualizarDadosPerfil(view);

                }

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
                            fotoUsuario.setImageURI(mSelecionarUri);
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }

                }
            }
    );

    public void SelecionarFtoGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);

    }

    public void AtualizarDadosPerfil(View view) {
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

                                Map<String, Object> usuarios = new HashMap<>();
                                usuarios.put("nome", nome);
                                usuarios.put("foto", fto);

                                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                db.collection("Users").document(usuarioID)
                                        .update("nome", nome, "foto", fto)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Snackbar snackbar = Snackbar.make(view, "Sucesso ao atualizar os dados!", Snackbar.LENGTH_SHORT)
                                                        .setAction("OK", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                finish();
                                                            }
                                                        });
                                                snackbar.show();
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    public void iniciarComponentes() {
        fotoUsuario = findViewById(R.id.FotoUsuario);
        editNome = findViewById(R.id.editNome);
        btAtualizarDados = findViewById(R.id.btAtualizarDados);
        btSelecionarFto = findViewById(R.id.btEditFto);
    }
}