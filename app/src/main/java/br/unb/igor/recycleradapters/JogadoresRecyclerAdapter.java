package br.unb.igor.recycleradapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.model.User;

public class JogadoresRecyclerAdapter extends RecyclerView.Adapter<JogadoresViewHolder> {

    private AdventureEditListener mListener;
    private List<User> users;

    public JogadoresRecyclerAdapter(AdventureEditListener listener, List<User> users) {
        this.mListener = listener;
        this.users = users;
    }

    @Override
    public JogadoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_jogadores_layout, null);
        JogadoresViewHolder jogadoresViewHolder = new JogadoresViewHolder(layoutView);
        return jogadoresViewHolder;
    }

    @Override
    public void onBindViewHolder(final JogadoresViewHolder holder, int position) {
        if (users.size() <= position) {
            return;
        }
        User user = users.get(position);
        holder.txtNomeJogador.setTextColor(ColorStateList.valueOf(Color.WHITE));
        holder.txtNomeJogador.setText(user.getEmail());
        holder.txtNomePersonagem.setTextColor(ColorStateList.valueOf(Color.WHITE));
        holder.txtNomePersonagem.setText(user.getFullName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = holder.getAdapterPosition();
                mListener.onAdicionaJogadorPesquisado(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
