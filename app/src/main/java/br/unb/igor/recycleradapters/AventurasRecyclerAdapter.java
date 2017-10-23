package br.unb.igor.recycleradapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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

    private AdventureListener mListener;
    private List<Aventura> aventuras;
    private String userId;
    private boolean isInEditMode = false;
    private static Animation wobble = null;

    public AventurasRecyclerAdapter (AdventureListener listener, List<Aventura> aventuras, String currentUserId) {
        this.mListener = listener;
        this.aventuras = aventuras;
        this.userId = currentUserId;
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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_adventure, parent, false);
        AventurasViewHolder aventurasViewHolder = new AventurasViewHolder(layoutView);
        return aventurasViewHolder;
    }


    @Override
    public void onBindViewHolder(final AventurasViewHolder holder, int position) {
        if (position >= aventuras.size()) {
            return;
        }
        Context ctx = holder.constraintLayoutBackground.getContext();
        Aventura aventura = aventuras.get(holder.getAdapterPosition());
        holder.constraintLayoutBackground.setBackgroundResource(ImageAssets.getBackgroundResource(aventura.getImagemFundo()));

        holder.txtViewTituloAventura.setText(aventura.getTitulo());
        holder.seekBarSessoesAventura.setProgress(aventura.getProgresso());

        Sessao nextSession = aventura.getProximaSessao();

        if (nextSession == null) {
            holder.txtViewProximaSessao.setText(ctx.getString(R.string.msg_next_session_not_yet_scheduled));
        } else {
            holder.txtViewProximaSessao.setText(String.format(
                ctx.getString(R.string.msg_next_session),
                nextSession.getData(),
                nextSession.getTitulo()
            ));
        }

        // Set Fira Sans (Regular) font
        Typeface firaSans = Typeface.createFromAsset(ctx.getAssets(), "FiraSans-Regular.ttf");
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


        boolean isMaster = userId != null && userId.equals(aventura.getMestreUserId());

        holder.overlayBlack.setVisibility(View.GONE);

        if (isInEditMode) {
            if (isMaster) {
                holder.containerDelete.setVisibility(View.VISIBLE);
                holder.containerDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isInEditMode) {
                            int index = holder.getAdapterPosition();
                            mListener.onRemoveAdventure(aventuras.get(index), index);
                        }
                    }
                });
                if (wobble == null) {
                    wobble = AnimationUtils.loadAnimation(ctx, R.anim.wobble_infinite);
                }
                holder.imgTrashBin.startAnimation(wobble);
            } else {
                holder.overlayBlack.setVisibility(View.VISIBLE);
            }
        } else {
            holder.containerDelete.setVisibility(View.GONE);
            holder.imgTrashBin.clearAnimation();
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
