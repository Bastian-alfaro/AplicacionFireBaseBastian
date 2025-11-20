package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin, btnIrRegistro;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== VINCULAR XML =====
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        auth = FirebaseAuth.getInstance();

        // ===== BOTÓN LOGIN =====
        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String pass = txtPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(a -> {

                        String uid = auth.getCurrentUser().getUid();
                        String deviceActual = obtenerIdDelDispositivo();

                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("usuarios")
                                .child(uid)
                                .child("deviceId");

                        ref.get().addOnSuccessListener(snapshot -> {

                            if (snapshot.exists()) {
                                String deviceGuardado = snapshot.getValue(String.class);

                                // ⭐ YA HAY UN DISPOSITIVO REGISTRADO
                                if (!deviceGuardado.equals(deviceActual)) {

                                    Toast.makeText(this,
                                            "⚠ Esta cuenta ya está iniciada en otro dispositivo",
                                            Toast.LENGTH_LONG).show();

                                    auth.signOut(); // Bloquear el login
                                    return;
                                }

                                // ⭐ Es el mismo dispositivo → acceder permitido
                                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                                finish();

                            } else {
                                // ⭐ PRIMERA VEZ: Guardar deviceId y entrar
                                ref.setValue(deviceActual)
                                        .addOnSuccessListener(unused -> {
                                            startActivity(new Intent(MainActivity.this, ChatActivity.class));
                                            finish();
                                        });
                            }
                        });
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        // ===== IR A REGISTRO =====
        btnIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );
    }

    // ⭐ MÉTODO PARA OBTENER EL ID ÚNICO DEL DISPOSITIVO
    public String obtenerIdDelDispositivo() {
        return Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }
}
