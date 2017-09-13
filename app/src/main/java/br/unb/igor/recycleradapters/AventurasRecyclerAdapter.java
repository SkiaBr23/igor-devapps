package br.unb.igor.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.listeners.AdventureListener;
import br.unb.igor.model.Aventura;

/**
 * Created by maxim on 04/09/2017.
 */

public class AventurasRecyclerAdapter extends RecyclerView.Adapter<AventurasViewHolder> {

    private Context context;
    private AdventureListener mListener;
    private List<Aventura> aventuras;
    private boolean isInEditMode = false;
    private static String PROXIMA_SESSAO = "próxima sessão ";

    public AventurasRecyclerAdapter (Context context, AdventureListener listener, List<Aventura> aventuras) {
        this.context = context;
        this.mListener = listener;
        this.aventuras = aventuras;
    }

    public void setEditMode(boolean b) {
        if (b != isInEditMode) {
            isInEditMode = b;
            notifyDataSetChanged();
        }
    }

    @Override
    public AventurasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.aventuras_home_layout, null);
        AventurasViewHolder aventurasViewHolder = new AventurasViewHolder(layoutView);
        return aventurasViewHolder;
    }

    @Override
    public void onBindViewHolder(final AventurasViewHolder holder, int position) {
        if (position < aventuras.size()) {
            Aventura aventura = aventuras.get(holder.getAdapterPosition());
            String tituloAventura = aventura.getTituloAventura();
            holder.linearLayoutBackground.setBackgroundResource(aventuras.get(holder.getAdapterPosition()).getImageResource());
            if (aventuras.get(holder.getAdapterPosition()).getTituloAventura().length() > 50) {
                tituloAventura = aventuras.get(holder.getAdapterPosition()).getTituloAventura().substring(0,45) + this.context.getResources().getString(R.string.strLonga);
            } else {
                tituloAventura = aventuras.get(holder.getAdapterPosition()).getTituloAventura();
            }
            holder.imgViewDeletar.setVisibility(isInEditMode ? View.VISIBLE : View.INVISIBLE);
            holder.txtViewTituloAventura.setText(tituloAventura);
            holder.txtViewProximaSessao.setText(PROXIMA_SESSAO + aventura.getDataProximaSessao());
            holder.seekBarSessoesAventura.setProgress(aventura.getProgresso());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onSelectAdventure(aventuras.get(index), index);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onRemoveAdventure(aventuras.get(index), index);
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
