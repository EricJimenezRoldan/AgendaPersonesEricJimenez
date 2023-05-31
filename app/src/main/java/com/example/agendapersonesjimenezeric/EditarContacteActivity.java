package com.example.agendapersonesjimenezeric;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditarContacteActivity extends AppCompatActivity {
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacte);

        final EditText editNom = findViewById(R.id.edit_nom);
        final EditText editTelefon = findViewById(R.id.edit_telefon);
        final EditText editEmail = findViewById(R.id.edit_email);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        Contacte contacte = (Contacte) intent.getSerializableExtra("contacte");

        if (contacte != null) {
            editNom.setText(contacte.getNom());
            editTelefon.setText(contacte.getTelefon());
            editEmail.setText(contacte.getEmail());
        }

        Button btnActualitzar = findViewById(R.id.btn_actualitzar);
        btnActualitzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = editNom.getText().toString();
                String telefon = editTelefon.getText().toString();
                String email = editEmail.getText().toString();

                Contacte contacteActualitzat = new Contacte(nom, telefon, email);

                Intent intent = new Intent();
                intent.putExtra("position", position);
                intent.putExtra("contacte", contacteActualitzat);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
