package br.unb.igor.recycleradapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.model.User;

public class JogadoresRecyclerAdapter extends RecyclerView.Adapter<JogadoresViewHolder> {

    private AdventureListener mListener;
    private DisplayInfo mode;
    private ListAdapterListener mListenerList;
    public Context context;

    public interface ListAdapterListener { // create an interface
        void onClickKickUsuario(User user, int index);
    }

    public static class DisplayInfo {
        public boolean isMaster = false;
        public boolean canPerformActions = false;
        public ColorStateList textColor = ColorStateList.valueOf(Color.BLACK);
        public String currentUserId = null;
        public List<User> users = new ArrayList<>();
        public Set<String> alreadyInvitedIds = new ArraySet<>();
        public Set<String> alreadyJoinedIds = new ArraySet<>();
    }

    public JogadoresRecyclerAdapter(Context ctx, AdventureListener listener, DisplayInfo mode, ListAdapterListener listAdapterListener) {
        this.mListener = listener;
        this.mode = mode;
        this.mListenerList = listAdapterListener;
        this.context = ctx;

    }

    public DisplayInfo getDisplayInfo() {
        return mode;
    }

    @Override
    public JogadoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_player, parent, false);
        JogadoresViewHolder jogadoresViewHolder = new JogadoresViewHolder(layoutView);
        return jogadoresViewHolder;
    }

    @Override
    public void onBindViewHolder(final JogadoresViewHolder holder, int position) {
        if (position >= getItemCount()) {
            return;
        }
        final User user = mode.users.get(position);
        holder.txtNomeJogador.setTextColor(mode.textColor);
        holder.txtNomeJogador.setText(user.getEmail());
        holder.txtNomePersonagem.setTextColor(mode.textColor);
        holder.txtNomePersonagem.setText(user.getFullName());
        if (mode.currentUserId != null && mode.currentUserId.equals(user.getUserId())) {
            holder.txtYouIndicator.setVisibility(View.VISIBLE);
        }
        if (mode.isMaster && mode.canPerformActions) {
            updateInviteButton(holder, user);
            holder.btnInvite.setVisibility(View.VISIBLE);
            holder.txtInvitationSent.setVisibility(View.GONE);
        } else {
            holder.btnInvite.setVisibility(View.GONE);
            if (mode.alreadyInvitedIds.contains(user.getUserId())) {
                holder.txtInvitationSent.setVisibility(View.VISIBLE);
            } else {
                holder.txtInvitationSent.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onViewUserInfo(user);
            }
        });

        if (user.getProfilePictureUrl() != null) {
            Picasso
                    .with(holder.profileImageJogador.getContext())
                    .load(user.getProfilePictureUrl())
                    .into(holder.profileImageJogador);
        }
    }

    @Override
    public int getItemCount() {
        return mode != null && mode.users != null ? mode.users.size() : 0;
    }

    private void updateInviteButton(final JogadoresViewHolder holder, final User user) {
        final boolean isInvited = mode.alreadyInvitedIds.contains(user.getUserId());
        final boolean hasJoined = mode.alreadyJoinedIds.contains(user.getUserId());
        Context ctx = holder.btnInvite.getContext();
        if (hasJoined) {
            holder.btnInvite.setBackgroundResource(0);
            holder.btnInvite.setBackgroundColor(ContextCompat.getColor(ctx, R.color.btnDisinviteColor));
            holder.btnInvite.setText(R.string.label_kick_out);
            holder.btnInvite.setTextColor(Color.WHITE);
        } else if (isInvited) {
            holder.btnInvite.setBackgroundResource(R.drawable.rectangle_outline_disinvite);
            holder.btnInvite.setText(R.string.label_disinvite);
            holder.btnInvite.setTextColor(ContextCompat.getColor(ctx, R.color.btnDisinviteColor));
        } else {
            holder.btnInvite.setBackgroundResource(R.drawable.rectangle_outline_invite);
            holder.btnInvite.setText(R.string.label_invite);
            holder.btnInvite.setTextColor(ContextCompat.getColor(ctx, R.color.btnInviteColor));
        }
        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasJoined) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Expulsar jogador");
                    alertDialog.setMessage("Deseja realmente expulsar o jogador?");
                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mListenerList.onClickKickUsuario(user, holder.getAdapterPosition());
                            //mListener.onUserKickedOut(user);
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
                } else {
                    mListener.onUserInvitation(user, !isInvited);
                }
                updateInviteButton(holder, user);
            }
        });
    }
}
