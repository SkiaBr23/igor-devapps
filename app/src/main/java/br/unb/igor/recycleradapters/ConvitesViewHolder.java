package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConvitesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNomeMestre;
    public CircleImageView profileImageMestre;
    public ImageButton imgBtnAceitarConvite;
    public ImageButton imgBtnCancelarConvite;

    public ConvitesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        txtNomeMestre = itemView.findViewById(R.id.txtViewNomeMestreCard);
        profileImageMestre = itemView.findViewById(R.id.profileImageCard);
        imgBtnAceitarConvite = itemView.findViewById(R.id.imgBtnAceitarConvite);
        imgBtnCancelarConvite = itemView.findViewById(R.id.imgBtnCancelarConvite);
    }

    @Override
    public void onClick(View view) {
    }
}
