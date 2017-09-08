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

public class AventurasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public SeekBar seekBarSessoesAventura;
    public TextView txtViewTituloAventura;
    public TextView txtViewProximaSessao;
    public LinearLayout linearLayoutBackground;

    public AventurasViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        seekBarSessoesAventura = (SeekBar)itemView.findViewById(R.id.seekBarSessoesAventura);
        txtViewTituloAventura = (TextView)itemView.findViewById(R.id.txtViewTituloAventura);
        txtViewProximaSessao = (TextView)itemView.findViewById(R.id.txtViewProximaSessao);
        linearLayoutBackground = (LinearLayout)itemView.findViewById(R.id.linearLayoutBackground);
    }

    @Override
    public void onClick(View view) {
    }
}
