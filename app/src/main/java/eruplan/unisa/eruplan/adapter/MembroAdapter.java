package eruplan.unisa.eruplan.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.entity.MembroEntity;

public class MembroAdapter extends RecyclerView.Adapter<MembroAdapter.MembroViewHolder> {

    private List<MembroEntity> membriList;
    private boolean showDeleteIcon = false;
    private OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public MembroAdapter(List<MembroEntity> membriList) {
        this.membriList = membriList;
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    public void setShowDeleteIcon(boolean show) {
        this.showDeleteIcon = show;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MembroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_membro, parent, false);
        return new MembroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembroViewHolder holder, int position) {
        MembroEntity membroEntity = membriList.get(position);
        String nomeCompleto = membroEntity.getNome() + " " + membroEntity.getCognome();
        holder.tvNomeCognome.setText(nomeCompleto);
        holder.tvCodiceFiscale.setText(membroEntity.getCodiceFiscale());

        holder.ivDeleteMembro.setVisibility(showDeleteIcon ? View.VISIBLE : View.GONE);

        holder.ivDeleteMembro.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Conferma eliminazione")
                    .setMessage("Vuoi eliminare questo membro del tuo nucleo?")
                    .setPositiveButton("Sì", (dialog, which) -> {
                        if (deleteListener != null) {
                            // Usa getAdapterPosition() per evitare problemi se la lista è cambiata
                            int currentPos = holder.getAdapterPosition();
                            if (currentPos != RecyclerView.NO_POSITION) {
                                deleteListener.onItemDelete(currentPos);
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return membriList != null ? membriList.size() : 0;
    }

    public static class MembroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeCognome;
        TextView tvCodiceFiscale;
        ImageView ivDeleteMembro;

        public MembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeCognome = itemView.findViewById(R.id.tv_nome_cognome);
            tvCodiceFiscale = itemView.findViewById(R.id.tv_codice_fiscale);
            ivDeleteMembro = itemView.findViewById(R.id.iv_delete_membro);
        }
    }
}
