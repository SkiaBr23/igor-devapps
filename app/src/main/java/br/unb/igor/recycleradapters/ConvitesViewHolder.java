package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConvitesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtNomeMestre;
    public CircleImageView profileImageMestre;

    public ConvitesViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        txtNomeMestre = (TextView)itemView.findViewById(R.id.txtViewNomeMestreCard);
        profileImageMestre = (CircleImageView) itemView.findViewById(R.id.profileImageCard);
    }

    @Override
    public void onClick(View view) {
    }
}