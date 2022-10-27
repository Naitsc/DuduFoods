package com.naitsc.dudufoods;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil_Usuario extends AppCompatActivity {

    private CircleImageView foto_usuario;
    private TextView nome_usuario, email_usuario;
    private Button bt_editarPerfil;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        InicarComponentes();

        bt_editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil_Usuario.this,Editar_Perfil.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Users").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Glide.with(getApplicationContext()).load(documentSnapshot.getString("foto")).into(foto_usuario);
                    nome_usuario.setText(documentSnapshot.getString("nome"));
                    email_usuario.setText(email);

                }
            }
        });
    }

    public void InicarComponentes() {
        foto_usuario = findViewById(R.id.foto_perfil);
        nome_usuario = findViewById(R.id.nome_usuario);
        email_usuario = findViewById(R.id.email_usuario);
        bt_editarPerfil = findViewById(R.id.bt_perfil);
    }
}