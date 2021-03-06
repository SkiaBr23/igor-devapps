package br.unb.igor.recycleradapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class JogadasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtJogadorPersonagem;
    public CircleImageView profileImage;
    public TextView txtResultado;
    public TextView txtJogada;
    public TextView txtChance;
    public TextView txtChancePeloMenos;
    public TextView txtDataJogada;
    public ConstraintLayout cardJogada;

    public JogadasViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        txtJogadorPersonagem = itemView.findViewById(R.id.txtJogadorPersonagem);
        profileImage = itemView.findViewById(R.id.profileImage);
        txtResultado = itemView.findViewById(R.id.txtResultado);
        txtJogada = itemView.findViewById(R.id.txtJogada);
        txtChance = itemView.findViewById(R.id.txtChance);
        txtChancePeloMenos = itemView.findViewById(R.id.txtChancePeloMenos);
        txtDataJogada = itemView.findViewById(R.id.txtDataJogada);
        cardJogada = itemView.findViewById(R.id.cardJogada);

    }

    @Override
    public void onClick(View view) {
    }
}
