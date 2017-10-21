package br.unb.igor.recycleradapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.model.Sessao;

public class SessoesRecyclerAdapter extends RecyclerView.Adapter<SessoesViewHolder> {

    private Context context;
    private AdventureListener mListener;
    private List<Sessao> sessoes;

    public SessoesRecyclerAdapter(Context context, AdventureListener listener, List<Sessao> sessoes) {
        this.context = context;
        this.mListener = listener;
        this.sessoes = sessoes;
        setHasStableIds(true);
    }

    public void setSessoes (List<Sessao> sessoes) {
        this.sessoes = sessoes;
    }

    @Override
    public SessoesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_sessoes_layout, null);
        SessoesViewHolder sessoesViewHolder = new SessoesViewHolder(layoutView);
        return sessoesViewHolder;
    }

    @Override
    public void onBindViewHolder(final SessoesViewHolder holder, int position) {
        if (position < sessoes.size()) {
            String tituloSessao = sessoes.get(holder.getAdapterPosition()).getTitulo();
            String dataSessao = sessoes.get(holder.getAdapterPosition()).getData().substring(0,5);
            if (tituloSessao.length() > 23) {
                tituloSessao = tituloSessao.substring(0,20) + this.context.getResources().getString(R.string.strLonga);
            }
            holder.txtTituloSessao.setText(tituloSessao);

            holder.txtDataSessao.setText(dataSessao);

            // Set Fira Sans (Regular) font
            Typeface firaSans = Typeface.createFromAsset(this.context.getAssets(), "FiraSans-Regular.ttf");
            holder.txtDataSessao.setTypeface(firaSans);
            holder.txtTituloSessao.setTypeface(firaSans);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onSelectSessao(sessoes.get(index), index);
                }
            });
            /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onRemoveAdventure(aventuras.get(index), index);
                    return false;
                }
            });*/

        }
    }

    @Override
    public int getItemCount() {
        return sessoes.size();
    }

    @Override
    public long getItemId(int position) {
        //return sessoes.get(position).getNumeroSessao();
        //TODO: isso dá problema? @maximillianfx
        return (long) position;
    }
}
