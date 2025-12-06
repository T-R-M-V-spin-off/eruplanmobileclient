package eruplan.unisa.eruplan.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import eruplan.unisa.eruplan.R;
import eruplan.unisa.eruplan.entity.RichiestaEntity;

/**
 * Adapter per visualizzare le richieste a schermo usando il layout XML 'item_richiesta.xml'.
 * Utilizza il pattern "ViewHolder" (RichiestaViewHolder), che è obbligatorio per le RecyclerView
 */
public class RichiestaAdapter extends RecyclerView.Adapter<RichiestaAdapter.RichiestaViewHolder> {

    // 1. Lista che contiene gli oggetti Richiesta scaricati dal server
    private List<RichiestaEntity> richiesteList;

    // NUOVO: Listener per comunicare all'Activity quale riga è stata cliccata
    private OnItemSelectListener listener;

    // NUOVO: Variabile per ricordare l'indice della riga selezionata (-1 = nessuna)
    private int selectedPosition = -1;

    // NUOVO: Interfaccia che l'Activity dovrà implementare
    public interface OnItemSelectListener {
        void onItemSelected(RichiestaEntity richiesta);
    }

    // 2. Quando creo l'Adapter nell'Activity, gli passo le richieste reali E il listener per i click
    public RichiestaAdapter(List<RichiestaEntity> richiesteList, OnItemSelectListener listener) {
        this.richiesteList = richiesteList;
        this.listener = listener;
    }

    // Metodo che crea la struttura grafica della riga, ma ancora senza dati dentro.
    @NonNull
    @Override
    public RichiestaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate: prendo il file XML 'item_richiesta.xml' (lo stampo della richiesta vuoto)
        // e lo trasformo in un oggetto View che Android può manipolare.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_richiesta, parent, false);

        // Passo questa vista appena creata al ViewHolder e lo restituisco.
        return new RichiestaViewHolder(view);
    }

    // Metodo chiamato continuamente mentre si scorre la lista.
    // Prende un riquadro esistente e lo riempie con i dati veri per la posizione corrente.
    @Override
    public void onBindViewHolder(@NonNull RichiestaViewHolder holder, int position) {

        // Prendo l'oggetto Richiesta dalla lista Java alla posizione corretta.
        RichiestaEntity richiestaEntity = richiesteList.get(position);

        // Uso il ViewHolder per impostare i testi reali nelle TextView bianche della riga.
        // Prende mittente e data usando i Getter definiti nel POJO Richiesta.java.
        holder.tvNome.setText(richiestaEntity.getNomeMittente());
        holder.tvData.setText(richiestaEntity.getDataOra());

        // --- NUOVA LOGICA PER LA SELEZIONE VISIVA ---

        // Se questa riga è quella selezionata, le cambio colore (Grigio Chiaro)
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            // Altrimenti rimetto il background originale (quello definito nell'XML)
            holder.itemView.setBackgroundResource(R.drawable.bg_rounded_red);
        }

        // Gestione del click sull'intera riga
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Salvo la posizione precedente per aggiornarla (toglierle il grigio)
                int previousItem = selectedPosition;
                // 2. Salvo la nuova posizione corrente
                selectedPosition = holder.getAdapterPosition();

                // 3. Notifico l'adapter di ridisegnare le due righe coinvolte
                notifyItemChanged(previousItem);
                notifyItemChanged(selectedPosition);

                // 4. Avviso l'Activity che è stato selezionato questo oggetto
                if (listener != null) {
                    listener.onItemSelected(richiestaEntity);
                }
            }
        });
    }

    // Metodo che comunica alla RecyclerView quanti elementi totali ci sono nella lista Java.
    @Override
    public int getItemCount() {
        // Se la lista è nulla, restituisce 0, altrimenti la sua dimensione vera.
        return richiesteList != null ? richiesteList.size() : 0;
    }

    /**
     * Classe contenitore che memorizza i riferimenti alle TextView bianche.
     * Serve a non dover fare 'findViewById' ogni volta che si scorre la lista,
     * rendendo lo scrolling più fluido.
     */
    public static class RichiestaViewHolder extends RecyclerView.ViewHolder {

        // Variabili per memorizzare i riferimenti grafici
        TextView tvNome;
        TextView tvData;

        // Il costruttore del ViewHolder
        public RichiestaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Una volta sola, cerca gli ID XML dentro la vista della riga ('itemView')
            // e li salva nelle variabili sopra.
            tvNome = itemView.findViewById(R.id.tv_nome_mittente);
            tvData = itemView.findViewById(R.id.tv_data_ora);
        }
    }
}