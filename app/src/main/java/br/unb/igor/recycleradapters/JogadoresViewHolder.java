package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by maxim on 04/09/2017.
 */

public class JogadoresViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNomePersonagem;
    public TextView txtNomeJogador;
    public CircleImageView profileImageJogador;

    public JogadoresViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        txtNomeJogador = (TextView)itemView.findViewById(R.id.txtNomeJogador);
        txtNomePersonagem = (TextView)itemView.findViewById(R.id.txtNomePersonagem);
        profileImageJogador = (CircleImageView) itemView.findViewById(R.id.profileImageJogador);
    }

    @Override
    public void onClick(View view) {
    }
}
