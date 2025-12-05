package eruplan.unisa.eruplan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.entity.AppoggioEntity;

public class AppoggioAdapter extends RecyclerView.Adapter<AppoggioAdapter.AppoggioViewHolder> {

    private List<AppoggioEntity> appoggi;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(AppoggioEntity appoggio);
    }

    public AppoggioAdapter(List<AppoggioEntity> appoggi, OnDeleteClickListener listener) {
        this.appoggi = appoggi;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppoggioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appoggio, parent, false);
        return new AppoggioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppoggioViewHolder holder, int position) {
        AppoggioEntity appoggio = appoggi.get(position);
        holder.viaPiazzaText.setText(appoggio.getViaPiazza() + ", " + appoggio.getCivico());
        holder.comuneText.setText(appoggio.getComune() + " (" + appoggio.getProvincia() + ")");

        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(appoggio));
    }

    @Override
    public int getItemCount() {
        return appoggi.size();
    }

    static class AppoggioViewHolder extends RecyclerView.ViewHolder {
        TextView viaPiazzaText, comuneText;
        Button deleteButton;

        public AppoggioViewHolder(@NonNull View itemView) {
            super(itemView);
            viaPiazzaText = itemView.findViewById(R.id.via_piazza_civico);
            comuneText = itemView.findViewById(R.id.comune_cap_provincia);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
