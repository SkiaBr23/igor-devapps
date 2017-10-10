package br.unb.igor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.model.Convite;
import br.unb.igor.recycleradapters.ConvitesRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentConvites extends Fragment {

    public static String TAG = FragmentConvites.class.getName();

    private RecyclerView recyclerViewListaConvites;
    private ConvitesRecyclerAdapter convitesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerConvites;
    private List<Convite> convites;


    public FragmentConvites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.convites, container, false);

        recyclerViewListaConvites = (RecyclerView)root.findViewById(R.id.recyclerViewListaConvites);

        layoutManagerConvites = new LinearLayoutManager(getActivity());
        recyclerViewListaConvites.setLayoutManager(layoutManagerConvites);
        convitesRecyclerAdapter = new ConvitesRecyclerAdapter(getActivity(), getConvites());
        recyclerViewListaConvites.setAdapter(convitesRecyclerAdapter);
        return root;
    }

    public List<Convite> getConvites() {
        if (this.convites == null) {
            this.convites = new ArrayList<>();
        }
        return this.convites;
    }
}
