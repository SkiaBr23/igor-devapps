package br.unb.igor.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import br.unb.igor.R;
import br.unb.igor.helpers.SessionListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCriarSessao extends Fragment {

    public static String TAG = FragmentHome.class.getName();

    private ImageView btnCloseSessao;
    private FloatingActionButton btnCloseCriarSessao;
    private Button btnConfirmarSessao;
    private String keyAdventure;
    private EditText editTituloSessao;

    private SessionListener mListener;

    public FragmentCriarSessao() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SessionListener) {
            mListener = (SessionListener) context;
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
        final View root = inflater.inflate(R.layout.criar_sessao, container, false);

        btnCloseCriarSessao = (FloatingActionButton)root.findViewById(R.id.btnCloseCriarSessao);
        btnCloseSessao = (ImageView)root.findViewById(R.id.btnCloseSessao);
        btnConfirmarSessao = (Button)root.findViewById(R.id.btnConfirmarSessao);
        editTituloSessao = (EditText)root.findViewById(R.id.editTituloSessao);


        keyAdventure = getArguments().getString("keyAventura");

        btnCloseSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });

        btnCloseCriarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnConfirmarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTituloSessao.getText().toString().length() == 0) {
                    editTituloSessao.setError("Preencha com o título!");
                    editTituloSessao.setTextColor(Color.BLACK);
                } else {
                    View view2 = getActivity().getCurrentFocus();
                    //Fecha o keyboard, ao final da criação da aventura
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    String titleSession = editTituloSessao.getText().toString();
                    editTituloSessao.setText("");
                    mListener.onConfirmarSessao(keyAdventure,titleSession);
                }
            }
        });

        return root;
    }

}
