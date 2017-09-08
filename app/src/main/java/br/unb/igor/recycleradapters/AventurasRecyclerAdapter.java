package br.unb.igor.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.model.Aventura;

/**
 * Created by maxim on 04/09/2017.
 */

public class AventurasRecyclerAdapter extends RecyclerView.Adapter<AventurasViewHolder> {

    private Context context;
    private ListAdapterListener mListener;
    private List<Aventura> aventuras;
    private static String PROXIMA_SESSAO = "próxima sessão ";


    public interface ListAdapterListener {
        void onAventuraSelected();
        void onAventuraDelete(Aventura aventura);
    }

    public AventurasRecyclerAdapter (Context context, ListAdapterListener listAdapterListener) {
        this.context = context;
        this.mListener = listAdapterListener;

        this.aventuras = new ArrayList<>();
    }

    @Override
    public AventurasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.aventuras_home_layout, null);
        AventurasViewHolder aventurasViewHolder = new AventurasViewHolder(layoutView);
        return aventurasViewHolder;
    }

    public void setAventuras (List<Aventura> aventuras){
        this.aventuras = aventuras;
    }

    public List<Aventura> getAventuras () {
        if (this.aventuras == null) {
            this.aventuras = new ArrayList<>();
        }
        return this.aventuras;
    }

    @Override
    public void onBindViewHolder(final AventurasViewHolder holder, int position) {
        if (position < aventuras.size()) {
            String tituloAventura;
            holder.linearLayoutBackground.setBackgroundResource(aventuras.get(holder.getAdapterPosition()).getImageResource());
            if (aventuras.get(holder.getAdapterPosition()).getTituloAventura().length() > 50) {
                tituloAventura = aventuras.get(holder.getAdapterPosition()).getTituloAventura().substring(0,45) + this.context.getResources().getString(R.string.strLonga);
            } else {
                tituloAventura = aventuras.get(holder.getAdapterPosition()).getTituloAventura();
            }
            holder.txtViewTituloAventura.setText(tituloAventura);
            holder.txtViewProximaSessao.setText(PROXIMA_SESSAO + aventuras.get(holder.getAdapterPosition()).getProximaSessao());
            holder.seekBarSessoesAventura.setProgress(aventuras.get(holder.getAdapterPosition()).getProgresso());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAventuraSelected();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onAventuraDelete(aventuras.get(holder.getAdapterPosition()));
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.aventuras.size();
    }
}
