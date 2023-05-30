package com.example.agendapersonesjimenezeric;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContacteAdapter extends RecyclerView.Adapter<ContacteAdapter.ContacteViewHolder> {
    private List<Contacte> llistaContactes;

    public ContacteAdapter(List<Contacte> llistaContactes) {
        this.llistaContactes = llistaContactes;
    }

    @NonNull
    @Override
    public ContacteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_contacte, parent, false);
        return new ContacteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContacteViewHolder holder, int position) {
        Contacte contacte = llistaContactes.get(position);
        holder.txtNom.setText(contacte.getNom());
        holder.txtTelefon.setText(contacte.getTelefon());
        holder.txtEmail.setText(contacte.getEmail());
    }

    @Override
    public int getItemCount() {
        return llistaContactes.size();
    }

    public static class ContacteViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNom;
        public TextView txtTelefon;
        public TextView txtEmail;

        public ContacteViewHolder(View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.txt_nom);
            txtTelefon = itemView.findViewById(R.id.txt_telefon);
            txtEmail = itemView.findViewById(R.id.txt_email);
        }
    }
}
