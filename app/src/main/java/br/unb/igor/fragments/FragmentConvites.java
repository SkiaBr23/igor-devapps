package br.unb.igor.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
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
    private AdventureListener mListener;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_convites, container, false);

        recyclerViewListaConvites = root.findViewById(R.id.recyclerViewListaConvites);
        layoutManagerConvites = new LinearLayoutManager(getActivity());
        recyclerViewListaConvites.setLayoutManager(layoutManagerConvites);
        convitesRecyclerAdapter = new ConvitesRecyclerAdapter(getActivity(), ((ActivityHome) getActivity()).getInvitations(), new ConvitesRecyclerAdapter.ListAdapterListener() {
            @Override
            public void onClickCancelarConvite(Convite convite) {
                convitesRecyclerAdapter.notifyDataSetChanged();
                mListener.onClickCancelarConvite(convite);
            }

            @Override
            public void onClickConfirmarConvite(int indexConvite) {
                //TODO: Colocar aqui a chamada para aceitar o convite no firebase
                //TODO: Depois de atualizar no firebase, remover o item da lista e dar um notify
            }
        });
        recyclerViewListaConvites.setAdapter(convitesRecyclerAdapter);

        ((ActivityHome)getActivity()).markInvitationsAsSeen();

        return root;
    }


}
