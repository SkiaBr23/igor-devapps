package br.unb.igor.recycleradapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;

/**
 * Created by maxim on 04/09/2017.
 */

public class AventurasRecyclerAdapter extends RecyclerView.Adapter<AventurasViewHolder> {

    private Context context;
    private AdventureListener mListener;
    private List<Aventura> aventuras;
    private boolean isInEditMode = false;

    public AventurasRecyclerAdapter (Context context, AdventureListener listener, List<Aventura> aventuras) {
        this.context = context;
        this.mListener = listener;
        this.aventuras = aventuras;
        setHasStableIds(true);
    }

    public void setEditMode(boolean b) {
        if (b != isInEditMode) {
            isInEditMode = b;
            notifyDataSetChanged();
        }
    }

    @Override
    public AventurasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.aventuras_home_layout, null);
        AventurasViewHolder aventurasViewHolder = new AventurasViewHolder(layoutView);
        return aventurasViewHolder;
    }


    @Override
    public void onBindViewHolder(final AventurasViewHolder holder, int position) {
        if (position < aventuras.size()) {
            Aventura aventura = aventuras.get(holder.getAdapterPosition());
            String tituloAventura = aventuras.get(holder.getAdapterPosition()).getTitulo();
            holder.linearLayoutBackground.setBackgroundResource(ImageAssets.getBackgroundResource(aventura.getImagemFundo()));
            if (tituloAventura.length() >= 50) {
                tituloAventura = tituloAventura.substring(0, 47) + this.context.getResources().getString(R.string.strLonga);
            }
            holder.imgViewDeletar.setVisibility(isInEditMode ? View.VISIBLE : View.INVISIBLE);
            holder.txtViewTituloAventura.setText(tituloAventura);
            holder.seekBarSessoesAventura.setProgress(aventura.getProgresso());

            Sessao nextSession = aventura.getProximaSessao();

            if (nextSession == null) {
                holder.txtViewProximaSessao.setText(context.getString(R.string.msg_next_session_not_yet_scheduled));
            } else {
                holder.txtViewProximaSessao.setText(String.format(
                    context.getString(R.string.msg_next_session),
                    nextSession.getData(),
                    nextSession.getTitulo()
                ));
            }

            // Set Fira Sans (Regular) font
            Typeface firaSans = Typeface.createFromAsset(this.context.getAssets(), "FiraSans-Regular.ttf");
            holder.txtViewTituloAventura.setTypeface(firaSans);
            holder.txtViewProximaSessao.setTypeface(firaSans);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onSelectAdventure(aventuras.get(index), index);
                }
            });


            holder.seekBarSessoesAventura.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
            /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int index = holder.getAdapterPosition();
                    mListener.onRemoveAdventure(aventuras.get(index), index);
                    return false;
                }
            });*/
            holder.imgViewDeletar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isInEditMode) {
                        int index = holder.getAdapterPosition();
                        mListener.onRemoveAdventure(aventuras.get(index), index);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.aventuras.size();
    }

    @Override
    public long getItemId(int position) {
        return aventuras.get(position).getKey().hashCode();
    }
}
