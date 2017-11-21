package br.unb.igor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.DBFeedListener;
import br.unb.igor.model.Convite;
import br.unb.igor.recycleradapters.ConvitesRecyclerAdapter;

public class FragmentConvites extends Fragment {

    public static String TAG = FragmentConvites.class.getName();

    private RecyclerView recyclerViewListaConvites;
    private ConvitesRecyclerAdapter convitesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerConvites;
    private AdventureListener mListener;

    private final DBFeedListener feedListener = new DBFeedListener() {
        @Override
        public void onInvitationAdded(Convite invite, int index) {
            convitesRecyclerAdapter.notifyItemInserted(index);
            ((ActivityHome)getActivity()).markInvitationsAsSeen();
        }

        @Override
        public void onInvitationRemoved(Convite invite, int index) {
            convitesRecyclerAdapter.notifyItemRemoved(index);
            ((ActivityHome)getActivity()).markInvitationsAsSeen();
        }
    };

    public FragmentConvites() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdventureListener) {
            mListener = (AdventureListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ActivityHome)getActivity()).removeDBFeedListener(feedListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_convites, container, false);

        ((ActivityHome)getActivity()).addDBFeedListener(feedListener);

        recyclerViewListaConvites = root.findViewById(R.id.recyclerViewListaConvites);
        layoutManagerConvites = new LinearLayoutManager(getActivity());
        recyclerViewListaConvites.setLayoutManager(layoutManagerConvites);
        convitesRecyclerAdapter = new ConvitesRecyclerAdapter(getActivity(),
                ((ActivityHome) getActivity()).getCurrentUser().getConvites(),
                new ConvitesRecyclerAdapter.ListAdapterListener() {
                    @Override
                    public void onClickCancelarConvite(Convite convite, int index) {
                        updateRecycler(index);
                        mListener.onClickCancelarConvite(convite, index);
                    }

                    @Override
                    public void onClickConfirmarConvite(Convite convite, int index) {
                        updateRecycler(index);
                        mListener.onClickConfirmarConvite(convite,index);
                    }
        });
        recyclerViewListaConvites.setAdapter(convitesRecyclerAdapter);

        ((ActivityHome)getActivity()).markInvitationsAsSeen();

        return root;
    }

    public void updateRecycler (int index) {
        ((ActivityHome) getActivity()).getCurrentUser().getConvites().remove(index);
        this.convitesRecyclerAdapter.notifyItemRemoved(index);
    }
}
