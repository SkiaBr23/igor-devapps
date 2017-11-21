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

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentBooks;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Livro;

public class LivrosRecyclerAdapter extends RecyclerView.Adapter<LivrosViewHolder> {

    private Context context;
    private List<Livro> livros;
    private ListAdapterListener mListener;

    public interface ListAdapterListener { // create an interface
        void onClickBaixarLivro(Livro livro, int index);
    }

    public LivrosRecyclerAdapter(Context context, ListAdapterListener listAdapterListener) {
        this.context = context;
        this.livros = new ArrayList<>();
        this.mListener = listAdapterListener;
        setHasStableIds(true);
    }

    public void setLivros (List<Livro> livros) {
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
        holder.txtTituloLivro.setText(livro.getTitulo());
        if (livro.isDownloaded()) {
            holder.imgIconeStatusLivro.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_confirmar));
        } else {
            holder.imgIconeStatusLivro.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_download));
        }
        if (livro.getUrlFile() != null) {
            if (!livro.getUrlThumbnail().isEmpty()) {
                Picasso
                        .with(holder.imgCapaLivro.getContext())
                        .load(livro.getUrlThumbnail())
                        .into(holder.imgCapaLivro);
            }
        }

        holder.imgCapaLivro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!livro.isDownloaded()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle(livro.getTitulo());
                    alertDialog.setMessage("Deseja fazer o download do livro?");
                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListener.onClickBaixarLivro(livro, holder.getAdapterPosition());
                        }
                    });
                    alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = alertDialog.create();
                    dialog.show();
                }

            }

        });
    }

    @Override
    public int getItemCount() {

        return this.livros != null ? this.livros.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }
}
