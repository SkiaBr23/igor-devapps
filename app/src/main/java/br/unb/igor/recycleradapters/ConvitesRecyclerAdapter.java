package br.unb.igor.recycleradapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Convite;

public class ConvitesRecyclerAdapter extends RecyclerView.Adapter<ConvitesViewHolder> {

    private Context context;
    private List<Convite> convites;

    public ConvitesRecyclerAdapter(Context context, List<Convite> convites) {
        this.context = context;
        this.convites = convites;
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
        Convite convite = this.convites.get(holder.getAdapterPosition());

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
    }

    @Override
    public int getItemCount() {
        return this.convites.size();
    }

    @Override
    public long getItemId(int position) {
        //return sessoes.get(position).getNumeroSessao();
        //TODO: isso dá problema? @maximillianfx
        return (long) position;
    }
}
