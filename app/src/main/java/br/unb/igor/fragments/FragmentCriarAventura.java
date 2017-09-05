package br.unb.igor.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.unb.igor.R;

public class FragmentCriarAventura extends Fragment {

    private ImageView imgFecharAventura;
    private EditText editTituloAventura;
    private Button btnConfirmarAventura;
    private OnFragmentInteractionListener mListener;

    public FragmentCriarAventura() {
        // Required empty public constructor
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
        // TODO: Update argument type and name
        void onCriacaoAventura(String tituloAventura);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.criar_aventura, container, false);

        imgFecharAventura = (ImageView)root.findViewById(R.id.btnCloseAventura);

        editTituloAventura = (EditText)root.findViewById(R.id.editTituloAventura);

        btnConfirmarAventura = (Button)root.findViewById(R.id.btnConfirmarAventura);

        btnConfirmarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTituloAventura.getText().toString().length() == 0) {
                    editTituloAventura.setError("Preencha com o título!");
                    editTituloAventura.setTextColor(Color.BLACK);
                } else {
                    mListener.onCriacaoAventura(editTituloAventura.getText().toString());
                }
            }
        });

        imgFecharAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return root;
    }

}
