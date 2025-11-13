package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText txtMensaje;
    Button btnEnviar;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // SIN EdgeToEdge (esto evita el crash)

        // === VINCULACIÓN CON XML ===
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        // === CONEXIÓN A FIREBASE ===
        databaseReference = FirebaseDatabase.getInstance().getReference("mensajes");

        // === BOTÓN ENVIAR ===
        btnEnviar.setOnClickListener(v -> {
            String texto = txtMensaje.getText().toString().trim();

            if (texto.isEmpty()) {
                Toast.makeText(MainActivity.this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarMensaje(texto);
        });
    }

    private void enviarMensaje(String texto) {

        String id = databaseReference.push().getKey();

        Mensaje mensaje = new Mensaje(id, texto, System.currentTimeMillis());

        databaseReference.child(id).setValue(mensaje)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Mensaje enviado a Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
