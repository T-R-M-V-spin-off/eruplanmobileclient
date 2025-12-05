package eruplan.unisa.eruplan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.entity.MembroEntity;

public class MembroAdapter extends RecyclerView.Adapter<MembroAdapter.MembroViewHolder> {

    private List<MembroEntity> membriList;

    public MembroAdapter(List<MembroEntity> membriList) {
        this.membriList = membriList;
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
    }

    @Override
    public int getItemCount() {
        return membriList != null ? membriList.size() : 0;
    }

    public static class MembroViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeCognome;
        TextView tvCodiceFiscale;

        public MembroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeCognome = itemView.findViewById(R.id.tv_nome_cognome);
            tvCodiceFiscale = itemView.findViewById(R.id.tv_codice_fiscale);
        }
    }
}