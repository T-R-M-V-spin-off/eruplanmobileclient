package eruplan.unisa.eruplan.adapter;

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

    // 2. Quando creo l'Adapter nell'Activity, gli passo le richieste reali con questo costruttore
    public RichiestaAdapter(List<RichiestaEntity> richiesteList) {
        this.richiesteList = richiesteList;
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