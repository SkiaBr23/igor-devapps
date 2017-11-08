package br.unb.igor.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Jogada;

public class JogadasRecyclerAdapter extends RecyclerView.Adapter<JogadasViewHolder> {

    private Context context;
    private List<Jogada> jogadas;

    public JogadasRecyclerAdapter(Context context) {
        this.context = context;
        this.jogadas = new ArrayList<>();
        setHasStableIds(true);
    }

    public void setJogadas (List<Jogada> jogadas) {
        this.jogadas = jogadas;
    }

    public List<Jogada> getJogadas () {
        if (this.jogadas == null) {
            this.jogadas = new ArrayList<>();
        }
        return this.jogadas;
    }

    @Override
    public JogadasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jogada, parent, false);
        JogadasViewHolder jogadasViewHolder = new JogadasViewHolder(layoutView);
        return jogadasViewHolder;
    }


    @Override
    public void onBindViewHolder(final JogadasViewHolder holder, int position) {
        if (position >= this.jogadas.size()) {
            return;
        }
        Jogada jogada = this.jogadas.get(holder.getAdapterPosition());

        holder.txtJogadorPersonagem.setText(jogada.getNomeAutor());
        holder.txtDataJogada.setText(jogada.getTimeSentMin());
        holder.txtJogada.setText("Rolando " + jogada.getComando() + " = ");
        holder.txtResultado.setText(jogada.getResultado());
        if (!jogada.getUrlFotoAutor().isEmpty()) {
            Picasso
                    .with(holder.profileImage.getContext())
                    .load(jogada.getUrlFotoAutor())
                    .transform(new CircleTransform())
                    .into(holder.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.jogadas.size();
    }

    @Override
    public long getItemId(int position) {
        //return sessoes.get(position).getNumeroSessao();
        //TODO: isso dá problema? @maximillianfx
        return (long) position;
    }
}
