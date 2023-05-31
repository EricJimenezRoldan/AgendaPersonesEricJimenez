package com.example.agendapersonesjimenezeric;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AfegirContacteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir_contacte);

        final EditText editNom = findViewById(R.id.edit_nom);
        final EditText editTelefon = findViewById(R.id.edit_telefon);
        final EditText editEmail = findViewById(R.id.edit_email);

        Button btnGuardar = findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = editNom.getText().toString();
                String telefon = editTelefon.getText().toString();
                String email = editEmail.getText().toString();

                Contacte contacte = new Contacte(nom, telefon, email);

                Intent intent = new Intent();
                intent.putExtra("contacte", contacte);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
