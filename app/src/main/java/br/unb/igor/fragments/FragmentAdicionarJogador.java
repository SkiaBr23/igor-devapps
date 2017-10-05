package br.unb.igor.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.model.Sessao;
import br.unb.igor.recycleradapters.JogadoresRecyclerAdapter;

public class FragmentAdicionarJogador extends Fragment {

    public static String TAG = FragmentAdicionarJogador.class.getName();

    private String keyAventura;
    private ImageButton imgBtnLimparPesquisa;
    private EditText editTxtPesquisaJogadores;
    private RecyclerView recyclerViewListaPesquisaJogadores;
    private List<String> usersID;
    private static String tipoExibicao = "pesquisaJogadores";

    private JogadoresRecyclerAdapter jogadoresPesquisadosRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerJogadoresPesquisados;

    private AdventureEditListener mListener;


    public FragmentAdicionarJogador() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdventureEditListener) {
            mListener = (AdventureEditListener) context;
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
        final View root = inflater.inflate(R.layout.adicionar_jogador, container, false);

        recyclerViewListaPesquisaJogadores = (RecyclerView)root.findViewById(R.id.recyclerViewListaPesquisaJogadores);
        editTxtPesquisaJogadores = (EditText)root.findViewById(R.id.editTextPesquisaUsuario);
        imgBtnLimparPesquisa= (ImageButton)root.findViewById(R.id.imgBtnLimparTxtPesqUsuario);

        imgBtnLimparPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTxtPesquisaJogadores.setText("");
            }
        });

        if (getArguments() != null) {
            if (getArguments().get("keyAventura") != null) {
                keyAventura = getArguments().getString("keyAventura");
            }
        }

        layoutManagerJogadoresPesquisados = new LinearLayoutManager(getActivity());
        recyclerViewListaPesquisaJogadores.setLayoutManager(layoutManagerJogadoresPesquisados);
        jogadoresPesquisadosRecyclerAdapter = new JogadoresRecyclerAdapter(getActivity(), mListener, getUsersID(), tipoExibicao);
        recyclerViewListaPesquisaJogadores.setAdapter(jogadoresPesquisadosRecyclerAdapter);

        editTxtPesquisaJogadores.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    //TODO: esse if precisa ser uma funcao que verifica se a string é um email valido
                    if (editTxtPesquisaJogadores.getText().toString().length() != 0) {
                        //TODO: incluir chamada ao firebase aqui
                        Toast.makeText(getActivity(),"Email: " + editTxtPesquisaJogadores.getText().toString(),Toast.LENGTH_SHORT).show();
                        clearKeyboard();
                    }
                }
                return false;
            }
        });

        return root;
    }

    public void clearKeyboard () {
        editTxtPesquisaJogadores.clearFocus();
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editTxtPesquisaJogadores.getWindowToken(),0);
    }

    public List<String> getUsersID () {
        if (this.usersID == null) {
            this.usersID = new ArrayList<>();
        }
        return this.usersID;
    }

}
