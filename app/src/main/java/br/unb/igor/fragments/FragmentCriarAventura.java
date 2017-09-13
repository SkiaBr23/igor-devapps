package br.unb.igor.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import br.unb.igor.R;
import br.unb.igor.listeners.AdventureListener;

public class FragmentCriarAventura extends Fragment {

    private ImageView imgFecharAventura;
    private EditText editTituloAventura;
    private Button btnConfirmarAventura;
    private FloatingActionButton btnFloatCloseAdventure;
    private AdventureListener mListener;

    public FragmentCriarAventura() {
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
        btnFloatCloseAdventure = (FloatingActionButton)root.findViewById((R.id.btnCriarAventura));
        btnConfirmarAventura = (Button)root.findViewById(R.id.btnConfirmarAventura);
        btnConfirmarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTituloAventura.getText().toString().length() == 0) {
                    editTituloAventura.setError("Preencha com o título!");
                    editTituloAventura.setTextColor(Color.BLACK);
                } else {
                    View view2 = getActivity().getCurrentFocus();
                    //Fecha o keyboard, ao final da criação da aventura
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String title = editTituloAventura.getText().toString();
                    editTituloAventura.setText("");
                    mListener.onCreateAdventure(title);
                }
            }
        });

        btnFloatCloseAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Esse FAB deve fechar a criacao da aventura
                // sem salvar ela
                getActivity().onBackPressed();

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
