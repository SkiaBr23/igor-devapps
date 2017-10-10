package br.unb.igor.recycleradapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Sessao;

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
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.convites_layout, null, false);
        ConvitesViewHolder convitesViewHolder = new ConvitesViewHolder(layoutView);
        return convitesViewHolder;
    }


    @Override
    public void onBindViewHolder(final ConvitesViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public long getItemId(int position) {
        //return sessoes.get(position).getNumeroSessao();
        //TODO: isso dá problema? @maximillianfx
        return (long) position;
    }
}
