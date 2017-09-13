package br.unb.igor.fragments;

import android.content.res.ColorStateList;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.listeners.AdventureListener;
import br.unb.igor.model.Aventura;
import br.unb.igor.recycleradapters.AventurasRecyclerAdapter;

public class FragmentHome extends Fragment {

    public static String TAG = FragmentHome.class.getName();

    private FloatingActionButton btnCriarAventura;
    private FloatingActionButton btnModoEdicao;
    private FloatingActionButton btnConfirmarAlteracao;

    private Fragment mFragmentCriarAventura;
    private RecyclerView recyclerViewAventurasHome;
    private AventurasRecyclerAdapter aventurasRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Aventura> aventuras;
    private AdventureListener mListener;
    private boolean isInEditMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aventuras = ((ActivityHome)getActivity()).getAdventures();
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
        final View root = inflater.inflate(R.layout.home, container, false);

        btnModoEdicao = root.findViewById(R.id.btnModoEdicao);
        btnCriarAventura = root.findViewById(R.id.btnCriarAventura);
        btnConfirmarAlteracao = root.findViewById(R.id.btnConfirmarAlteracao);
        btnCriarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (mFragmentCriarAventura == null) {
                mFragmentCriarAventura = new FragmentCriarAventura();
            }

            getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, mFragmentCriarAventura)
                .addToBackStack(TAG)
                .commit();
            }
        });

        btnConfirmarAlteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditMode(true);
            }
        });

        recyclerViewAventurasHome = (RecyclerView)root.findViewById(R.id.recyclerViewAventurasHome);
        recyclerViewAventurasHome.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAventurasHome.setLayoutManager(layoutManager);
        aventurasRecyclerAdapter = new AventurasRecyclerAdapter(getActivity(), mListener, aventuras);
        recyclerViewAventurasHome.setAdapter(aventurasRecyclerAdapter);

        return root;
    }

    public final AventurasRecyclerAdapter getRecyclerAdapter() {
        return aventurasRecyclerAdapter;
    }

    public void setEditMode(boolean b) {
        if (isInEditMode != b) {
            isInEditMode = b;
            aventurasRecyclerAdapter.setEditMode(b);

            if (b) {
                btnModoEdicao.setVisibility(View.VISIBLE);
                btnCriarAventura.setVisibility(View.GONE);
                btnConfirmarAlteracao.setVisibility(View.VISIBLE);
            }
        } else {
            isInEditMode = !b;
            aventurasRecyclerAdapter.setEditMode(!b);
            btnModoEdicao.setVisibility(View.GONE);
            btnCriarAventura.setVisibility(View.VISIBLE);
            btnConfirmarAlteracao.setVisibility(View.GONE);
        }
    }

}
