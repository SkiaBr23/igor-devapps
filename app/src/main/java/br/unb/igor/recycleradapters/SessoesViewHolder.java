package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class SessoesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtDataSessao;
    public TextView txtTituloSessao;
    public LinearLayout linearLayoutBackground;

    public SessoesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        txtDataSessao = (TextView)itemView.findViewById(R.id.txtDataSessao);
        txtTituloSessao = (TextView)itemView.findViewById(R.id.txtTituloSessao);
        linearLayoutBackground = (LinearLayout)itemView.findViewById(R.id.layoutBackground);
    }

    @Override
    public void onClick(View view) {
    }
}
