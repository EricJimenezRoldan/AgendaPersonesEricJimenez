package com.example.agendapersonesjimenezeric;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Contacte> llistaContactes;
    private RecyclerView recyclerView;
    private ContacteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llistaContactes = obtenirContactes(); // Implementa el mètode per llegir els contactes des de l'arxiu de dades

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContacteAdapter(llistaContactes);
        recyclerView.setAdapter(adapter);
    }

    // Implementa els mètodes per afegir, editar i esborrar contactes

    // Implementa el mètode per llegir els contactes des de l'arxiu de dades

    // Implementa el mètode per guardar els contactes a l'arxiu de dades
}
