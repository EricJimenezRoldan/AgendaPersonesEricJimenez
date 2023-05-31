package com.example.agendapersonesjimenezeric;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Contacte> llistaContactes;
    private RecyclerView recyclerView;
    private ContacteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llistaContactes = llegirContactesDesdeArxiu();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContacteAdapter(llistaContactes);
        recyclerView.setAdapter(adapter);

        Button btnAfegirContacte = findViewById(R.id.btn_afegir_contacte);
        btnAfegirContacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obrirFormulariAfegirContacte();
            }
        });

        Button btnEsborrarContacte = findViewById(R.id.btn_esborrar_contacte);
        btnEsborrarContacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicioSeleccionada = adapter.getPosicioSeleccionada();
                if (posicioSeleccionada != RecyclerView.NO_POSITION) {
                    esborrarContacte(posicioSeleccionada);
                } else {
                    Toast.makeText(MainActivity.this, "Selecciona un contacte a esborrar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnEditarContacte = findViewById(R.id.btn_editar_contacte);
        btnEditarContacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicioSeleccionada = adapter.getPosicioSeleccionada();
                if (posicioSeleccionada != RecyclerView.NO_POSITION) {
                    obrirFormulariEditarContacte(posicioSeleccionada);
                } else {
                    Toast.makeText(MainActivity.this, "Selecciona un contacte a editar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<Contacte> llegirContactesDesdeArxiu() {
        List<Contacte> contactes = new ArrayList<>();

        try {
            FileInputStream fileInputStream = openFileInput("contactes.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] datos = line.split(",");
                if (datos.length == 3) {
                    String nom = datos[0];
                    String telefon = datos[1];
                    String email = datos[2];
                    Contacte contacte = new Contacte(nom, telefon, email);
                    contactes.add(contacte);
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contactes;
    }

    private void guardarContactesEnArxiu() {
        try {
            FileOutputStream fileOutputStream = openFileOutput("contactes.txt", MODE_PRIVATE);

            for (Contacte contacte : llistaContactes) {
                String linia = contacte.getNom() + "," + contacte.getTelefon() + "," + contacte.getEmail() + "\n";
                fileOutputStream.write(linia.getBytes());
            }

            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void afegirContacte(Contacte contacte) {
        llistaContactes.add(contacte);
        adapter.notifyDataSetChanged();
        guardarContactesEnArxiu();
    }

    private void esborrarContacte(int position) {
        if (position >= 0 && position < llistaContactes.size()) {
            llistaContactes.remove(position);
            adapter.notifyItemRemoved(position);
            guardarContactesEnArxiu();
        }
    }

    private void editarContacte(int position, Contacte contacte) {
        if (position >= 0 && position < llistaContactes.size()) {
            llistaContactes.set(position, contacte);
            adapter.notifyItemChanged(position);
            guardarContactesEnArxiu();
        }
    }

    private void obrirFormulariAfegirContacte() {
        Intent intent = new Intent(this, AfegirContacteActivity.class);
        startActivityForResult(intent, 1);
    }

    private void obrirFormulariEditarContacte(int position) {
        Contacte contacte = llistaContactes.get(position);
        Intent intent = new Intent(this, EditarContacteActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("contacte", contacte);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Agregar contacto
                Contacte contacte = (Contacte) data.getSerializableExtra("contacte");
                afegirContacte(contacte);
                Toast.makeText(this, "Contacte afegit", Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) { // Editar contacto
                int position = data.getIntExtra("position", -1);
                Contacte contacte = (Contacte) data.getSerializableExtra("contacte");
                if (position != -1) {
                    editarContacte(position, contacte);
                    Toast.makeText(this, "Contacte editat", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
