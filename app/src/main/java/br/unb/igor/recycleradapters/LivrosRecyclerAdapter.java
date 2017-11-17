package br.unb.igor.recycleradapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Livro;

public class LivrosRecyclerAdapter extends RecyclerView.Adapter<LivrosViewHolder> {

    private Context context;
    private List<Livro> livros;
    private ListAdapterListener mListener;

    public interface ListAdapterListener { // create an interface

    }

    public LivrosRecyclerAdapter(Context context, ListAdapterListener listAdapterListener) {
        this.context = context;
        this.mListener = listAdapterListener;
        setHasStableIds(true);
    }

    public void setLivros (List<Livro> thumbnails) {
        this.livros = livros;
    }

    @Override
    public LivrosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_book, parent, false);
        LivrosViewHolder livrosViewHolder = new LivrosViewHolder(layoutView);
        return livrosViewHolder;
    }


    @Override
    public void onBindViewHolder(final LivrosViewHolder holder, int position) {
        if (position >= this.livros.size()) {
            return;
        }
        final Livro livro = this.livros.get(holder.getAdapterPosition());

        if (livro.isDownloaded()) {
            holder.imgIconeStatusLivro.setBackgroundResource(R.drawable.ic_confirmar);
        } else {
            holder.imgIconeStatusLivro.setBackgroundResource(R.drawable.ic_download);
        }
        if (livro.getUrlFile() != null) {
            if (!livro.getUrlFile().isEmpty()) {
                Picasso
                        .with(holder.imgCapaLivro.getContext())
                        .load(livro.getUrlFile())
                        .transform(new CircleTransform())
                        .into(holder.imgCapaLivro);
            }
        }

        holder.imgCapaLivro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {

        return this.livros != null ? this.livros.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        //return sessoes.get(position).getNumeroSessao();
        //TODO: isso dá problema? @maximillianfx
        return (long) position;
    }
}
