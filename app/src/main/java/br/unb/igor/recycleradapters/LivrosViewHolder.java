package br.unb.igor.recycleradapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.unb.igor.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class LivrosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgCapaLivro;
    public ImageView imgIconeStatusLivro;

    public LivrosViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        imgCapaLivro = itemView.findViewById(R.id.imgCapaLivro);
        imgIconeStatusLivro = itemView.findViewById(R.id.imgIconeStatusLivro);
    }

    @Override
    public void onClick(View view) {
    }
}
