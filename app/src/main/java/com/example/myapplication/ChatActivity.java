package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    EditText txtMensajeChat;
    Button btnEnviarChat, btnLogout;
    ListView listaMensajes;

    ArrayList<Mensaje> lista;
    MensajeAdapter adapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        txtMensajeChat = findViewById(R.id.txtMensajeChat);
        btnEnviarChat = findViewById(R.id.btnEnviarChat);
        btnLogout = findViewById(R.id.btnLogout);
        listaMensajes = findViewById(R.id.listaMensajes);

        lista = new ArrayList<>();
        adapter = new MensajeAdapter(this, lista);
        listaMensajes.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("mensajes");

        // ===== ENVIAR MENSAJE =====
        btnEnviarChat.setOnClickListener(v -> {
            String texto = txtMensajeChat.getText().toString().trim();

            if (texto.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = databaseReference.push().getKey();
            Mensaje m = new Mensaje(id, texto, System.currentTimeMillis());

            databaseReference.child(id).setValue(m);
            txtMensajeChat.setText("");
        });

        // ===== LEER MENSAJES =====
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                lista.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Mensaje m = ds.getValue(Mensaje.class);
                    lista.add(m);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // ===== BOTÓN CERRAR SESIÓN =====
        btnLogout.setOnClickListener(v -> {

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Borrar deviceId del usuario en Firebase
            FirebaseDatabase.getInstance()
                    .getReference("usuarios")
                    .child(uid)
                    .child("deviceId")
                    .removeValue();

            // Cerrar sesión
            FirebaseAuth.getInstance().signOut();

            // Volver al login
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        });
    }

    // ===== PREVENIR QUE EL USUARIO ENTRE SIN LOGIN =====
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }
    }
}
