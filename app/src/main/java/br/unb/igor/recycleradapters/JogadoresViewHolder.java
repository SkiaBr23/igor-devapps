package br.unb.igor.recycleradapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;
import br.unb.igor.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by maxim on 04/09/2017.
 */

public class JogadoresViewHolder extends RecyclerView.ViewHolder {

    public TextView txtNomePersonagem;
    public TextView txtNomeJogador;
    public CircleImageView profileImageJogador;
    public Button btnInvite;

    public JogadoresViewHolder(View itemView) {
        super(itemView);
        txtNomeJogador = itemView.findViewById(R.id.txtNomeJogador);
        txtNomePersonagem = itemView.findViewById(R.id.txtNomePersonagem);
        profileImageJogador = itemView.findViewById(R.id.profileImageJogador);
        btnInvite = itemView.findViewById(R.id.btnInvite);
    }

}
