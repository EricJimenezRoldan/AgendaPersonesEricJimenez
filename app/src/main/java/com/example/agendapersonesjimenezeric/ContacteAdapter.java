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
    private int posicioSeleccionada = RecyclerView.NO_POSITION;
    private OnItemClickListener onItemClickListener;

    public ContacteAdapter(List<Contacte> llistaContactes) {
        this.llistaContactes = llistaContactes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public int getPosicioSeleccionada() {
        return posicioSeleccionada;
    }

    public void setPosicioSeleccionada(int posicioSeleccionada) {
        int posicioAnteriorSeleccionada = this.posicioSeleccionada;
        this.posicioSeleccionada = posicioSeleccionada;
        if (posicioAnteriorSeleccionada != RecyclerView.NO_POSITION) {
            notifyItemChanged(posicioAnteriorSeleccionada);
        }
        if (posicioSeleccionada != RecyclerView.NO_POSITION) {
            notifyItemChanged(posicioSeleccionada);
        }
    }

    @NonNull
    @Override
    public ContacteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_contacte, parent, false);
        return new ContacteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContacteViewHolder holder, int position) {
        Contacte contacte = llistaContactes.get(position);
        holder.txtNom.setText(contacte.getNom());
        holder.txtTelefon.setText(contacte.getTelefon());
        holder.txtEmail.setText(contacte.getEmail());

        if (posicioSeleccionada == position) {
            holder.itemView.setBackgroundResource(R.drawable.contacte_background_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.contacte_background);
        }
    }

    @Override
    public int getItemCount() {
        return llistaContactes.size();
    }

    class ContacteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNom;
        TextView txtTelefon;
        TextView txtEmail;

        ContacteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.txt_nom);
            txtTelefon = itemView.findViewById(R.id.txt_telefon);
            txtEmail = itemView.findViewById(R.id.txt_email);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        setPosicioSeleccionada(position);
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
