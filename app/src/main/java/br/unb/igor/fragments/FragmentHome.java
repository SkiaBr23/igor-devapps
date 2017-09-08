package br.unb.igor.fragments;

import android.app.Fragment;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.model.Aventura;
import br.unb.igor.recycleradapters.AventurasRecyclerAdapter;

public class FragmentHome extends Fragment {

    private static String TAG = FragmentHome.class.getName();
    private FloatingActionButton btnCriarAventura;
    private Fragment mFragmentCriarAventura;
    private RecyclerView recyclerViewAventurasHome;
    private AventurasRecyclerAdapter aventurasRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Aventura> aventuras;
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (((ActivityHome)getActivity()).getAventuras().size() > 0) {
            this.aventuras = ((ActivityHome)getActivity()).getAventuras();
        } else {
            this.aventuras = new ArrayList<>();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        void onAventuraDelete(Aventura aventura);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.home, container, false);

        btnCriarAventura = root.findViewById(R.id.btnCriarAventura);
        btnCriarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (mFragmentCriarAventura == null) {
                mFragmentCriarAventura = new FragmentCriarAventura();
            }

            getActivity()
                .getFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, mFragmentCriarAventura)
                .addToBackStack(TAG)
                .commit();
            }
        });

        recyclerViewAventurasHome = (RecyclerView)root.findViewById(R.id.recyclerViewAventurasHome);
        recyclerViewAventurasHome.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAventurasHome.setLayoutManager(layoutManager);
        aventurasRecyclerAdapter = new AventurasRecyclerAdapter(getActivity(), new AventurasRecyclerAdapter.ListAdapterListener() {
            @Override
            public void onAventuraSelected() {

            }

            @Override
            public void onAventuraDelete(final Aventura aventura) {
                Vibrator v = (Vibrator) getActivity().getSystemService(getActivity().VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(200);
                final AlertDialog alerta;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Deseja remover esta aventura?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onAventuraDelete(aventura);
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                alerta = builder.create();
                alerta.show();

            }
        });

        aventurasRecyclerAdapter.setAventuras(this.aventuras);
        recyclerViewAventurasHome.setAdapter(aventurasRecyclerAdapter);

        return root;
    }

    public List<Aventura> getAventuras () {
        if (this.aventuras == null) {
            this.aventuras = new ArrayList<>();
        }
        return this.aventuras;
    }

    public void setAventuras (List<Aventura> aventuras) {
        this.aventuras = aventuras;
    }
}
