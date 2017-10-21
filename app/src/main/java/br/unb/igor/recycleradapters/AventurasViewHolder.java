package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class AventurasViewHolder extends RecyclerView.ViewHolder {

    public SeekBar seekBarSessoesAventura;
    public TextView txtViewTituloAventura;
    public TextView txtViewProximaSessao;
    public ImageView imgViewDeletar;
    public LinearLayout linearLayoutBackground;

    public AventurasViewHolder(View itemView) {
        super(itemView);
        seekBarSessoesAventura = itemView.findViewById(R.id.seekBarSessoesAventura);
        txtViewTituloAventura = itemView.findViewById(R.id.txtViewTituloAventura);
        txtViewProximaSessao = itemView.findViewById(R.id.txtViewProximaSessao);
        imgViewDeletar = itemView.findViewById(R.id.btnDeletar);
        linearLayoutBackground = itemView.findViewById(R.id.linearLayoutBackground);
    }
}
