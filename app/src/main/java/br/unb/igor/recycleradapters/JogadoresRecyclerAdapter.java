package br.unb.igor.recycleradapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;

/**
 * Created by maxim on 04/09/2017.
 */

public class JogadoresRecyclerAdapter extends RecyclerView.Adapter<JogadoresViewHolder> {

    private Context context;
    private AdventureEditListener mListener;
    private List<String> jogadoresID;
    private String tipoExibicao;

    public JogadoresRecyclerAdapter(Context context, AdventureEditListener listener, List<String> jogadoresID, String tipoExibicao) {
        this.context = context;
        this.mListener = listener;
        this.jogadoresID = jogadoresID;
        setHasStableIds(true);
        this.tipoExibicao = tipoExibicao;
    }

    public void setJogadoresID (List<String> jogadoresID) {
        this.jogadoresID = jogadoresID;
    }

    @Override
    public JogadoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_jogadores_layout, null);
        JogadoresViewHolder jogadoresViewHolder = new JogadoresViewHolder(layoutView);
        return jogadoresViewHolder;
    }


    @Override
    public void onBindViewHolder(final JogadoresViewHolder holder, int position) {
        if (tipoExibicao.equals("pesquisaJogadores")) {
            holder.txtNomeJogador.setTextColor(ColorStateList.valueOf(Color.WHITE));
            holder.txtNomePersonagem.setTextColor(ColorStateList.valueOf(Color.WHITE));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = holder.getAdapterPosition();
                    //TODO: Incluir id do usuario na chamada do metodo depois
                    mListener.onAdicionaJogadorPesquisado(index);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
