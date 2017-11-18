package br.unb.igor.recycleradapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Convite;

public class ConvitesRecyclerAdapter extends RecyclerView.Adapter<ConvitesViewHolder> {

    private Context context;
    private List<Convite> convites;
    private ListAdapterListener mListener;

    public interface ListAdapterListener { // create an interface
        void onClickCancelarConvite(Convite convite, int index);
        void onClickConfirmarConvite(Convite convite, int index);
    }

    public ConvitesRecyclerAdapter(Context context, List<Convite> convites, ListAdapterListener listAdapterListener) {
        this.context = context;
        this.convites = convites;
        this.mListener = listAdapterListener;
        setHasStableIds(true);
    }

    public void setConvites (List<Convite> convites) {
        this.convites = convites;
    }

    @Override
    public ConvitesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_invite, parent, false);
        ConvitesViewHolder convitesViewHolder = new ConvitesViewHolder(layoutView);
        return convitesViewHolder;
    }

    @Override
    public void onBindViewHolder(final ConvitesViewHolder holder, int position) {
        if (position >= this.convites.size()) {
            return;
        }
        final Convite convite = this.convites.get(holder.getAdapterPosition());

        holder.txtNomeMestre.setText(convite.getInvitedByName());
        if (convite.getUrlPhoto() != null) {
            if (!convite.getUrlPhoto().isEmpty()) {
                Picasso
                        .with(holder.profileImageMestre.getContext())
                        .load(convite.getUrlPhoto())
                        .transform(new CircleTransform())
                        .into(holder.profileImageMestre);
            }
        }

        holder.imgBtnAceitarConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Aceitar Convite");
                alertDialog.setMessage("Deseja realmente aceitar o convite?");
                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onClickConfirmarConvite(convite,holder.getAdapterPosition());
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
        });

        holder.imgBtnCancelarConvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Recusar Convite");
                alertDialog.setMessage("Deseja realmente recusar o convite?");
                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onClickCancelarConvite(convite, holder.getAdapterPosition());
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
        });
    }

    @Override
    public int getItemCount() {

        return this.convites != null ? this.convites.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }
}
