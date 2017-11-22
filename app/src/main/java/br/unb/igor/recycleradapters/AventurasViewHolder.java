package br.unb.igor.recycleradapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    public ImageView imgTrashBin;
    public LinearLayout containerDelete;
    public ConstraintLayout constraintLayoutBackground;
    public View overlayBlack;
    public Button btnLeaveAdventure;

    public AventurasViewHolder(View itemView) {
        super(itemView);
        seekBarSessoesAventura = itemView.findViewById(R.id.seekBarSessoesAventura);
        txtViewTituloAventura = itemView.findViewById(R.id.txtViewTituloAventura);
        txtViewProximaSessao = itemView.findViewById(R.id.txtViewProximaSessao);
        containerDelete = itemView.findViewById(R.id.containerDelete);
        imgTrashBin = itemView.findViewById(R.id.imgTrashBin);
        overlayBlack = itemView.findViewById(R.id.overlayBlack);
        constraintLayoutBackground = itemView.findViewById(R.id.layoutBackground);
        btnLeaveAdventure = itemView.findViewById(R.id.btnLeaveAdventure);
    }
}
