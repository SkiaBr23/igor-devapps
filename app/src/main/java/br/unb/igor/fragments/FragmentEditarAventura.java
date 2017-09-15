package br.unb.igor.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import br.unb.igor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditarAventura extends Fragment {

    public static String TAG = FragmentHome.class.getName();
    private String tituloAventura;
    private TextView txtTituloAventuraEdicao;
    private EditText txtDescricaoAventura;
    private ImageView abasJanelas;
    private TextView abaAndamento;
    private TextView abaJogadores;
    private ConstraintLayout boxAndamentoAventura;
    private ConstraintLayout boxJogadoresAventura;




    public FragmentEditarAventura() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.editar_aventura, container, false);
        tituloAventura = getArguments().getString("tituloAventura");
        txtTituloAventuraEdicao = (TextView)root.findViewById(R.id.txtTituloAventuraEdicao);
        txtDescricaoAventura = (EditText)root.findViewById(R.id.txtDescricaoAventura);
        abasJanelas = (ImageView)root.findViewById(R.id.abasJanelas);
        abaAndamento = (TextView)root.findViewById(R.id.abaAndamento);
        abaJogadores = (TextView)root.findViewById(R.id.abaJogadores);

        boxAndamentoAventura = (ConstraintLayout)root.findViewById(R.id.boxAndamentoAventura);
        boxJogadoresAventura = (ConstraintLayout)root.findViewById(R.id.boxJogadoresAventura);

        abaJogadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(View.GONE);
                boxJogadoresAventura.setVisibility(View.VISIBLE);
                abasJanelas.setBackground(getResources().getDrawable(R.drawable.aba_dois));
            }
        });

        abaAndamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(View.VISIBLE);
                boxJogadoresAventura.setVisibility(View.GONE);
                abasJanelas.setBackground(getResources().getDrawable(R.drawable.aba_um));
            }
        });




        txtDescricaoAventura.setClickable(false);
        txtDescricaoAventura.setFocusable(false);

        txtTituloAventuraEdicao.setText(tituloAventura);

        return root;
    }

}
