package br.unb.igor.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.unb.igor.R;

/**
 * Created by maxim on 04/09/2017.
 */

public class AventurasRecyclerAdapter extends RecyclerView.Adapter<AventurasViewHolder> {

    private Context context;
    private ListAdapterListener mListener;


    public interface ListAdapterListener {
        void onAventuraSelected();
    }

    public AventurasRecyclerAdapter (Context context, ListAdapterListener listAdapterListener) {
        this.context = context;
        this.mListener = listAdapterListener;
    }

    @Override
    public AventurasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.aventuras_home_layout, null);
        AventurasViewHolder aventurasViewHolder = new AventurasViewHolder(layoutView);
        return aventurasViewHolder;
    }

    @Override
    public void onBindViewHolder(AventurasViewHolder holder, int position) {
        holder.linearLayoutBackground.setBackgroundResource(R.drawable.miniatura_krevast);
        holder.txtViewTituloAventura.setText("Titulo da aventura");
        holder.txtViewProximaSessao.setText("próxima sessão 21/10");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAventuraSelected();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
