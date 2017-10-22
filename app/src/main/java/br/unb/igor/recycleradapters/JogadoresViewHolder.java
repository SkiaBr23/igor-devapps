package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by maxim on 04/09/2017.
 */

public class JogadoresViewHolder extends RecyclerView.ViewHolder {

    public TextView txtNomePersonagem;
    public TextView txtNomeJogador;
    public TextView txtYouIndicator;
    public CircleImageView profileImageJogador;
    public Button btnInvite;

    public JogadoresViewHolder(View itemView) {
        super(itemView);
        txtNomeJogador = itemView.findViewById(R.id.txtUserEmail);
        txtNomePersonagem = itemView.findViewById(R.id.txtUserName);
        txtYouIndicator = itemView.findViewById(R.id.boxYouIndicator);
        profileImageJogador = itemView.findViewById(R.id.profileImageJogador);
        btnInvite = itemView.findViewById(R.id.btnInvite);
    }

}
