package br.unb.igor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.model.Jogada;
import br.unb.igor.recycleradapters.JogadasRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDiceRoller extends Fragment {

    public static final String TAG = FragmentDiceRoller.class.getName();

    public NumberPicker numberPickerqtdDados;
    public NumberPicker numberPickertipoDado;
    public NumberPicker numberPickerqtdAdicional;
    public Button btnRolarDados;
    public RecyclerView recyclerViewListaJogadas;
    private RecyclerView.LayoutManager layoutManager;
    private JogadasRecyclerAdapter jogadasRecyclerAdapter;
    private List<Jogada> jogadas;


    public FragmentDiceRoller() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_dice_roller, container, false);
        numberPickerqtdDados = (NumberPicker)root.findViewById(R.id.numberPickerQtdDados);
        numberPickertipoDado = (NumberPicker)root.findViewById(R.id.numberPickerTipoDado);
        numberPickerqtdAdicional = (NumberPicker)root.findViewById(R.id.numberPickerAdicional);
        btnRolarDados = (Button)root.findViewById(R.id.btnRolarDados);
        recyclerViewListaJogadas = (RecyclerView)root.findViewById(R.id.recyclerViewListaJogadas);


        jogadas = new ArrayList<>();

        numberPickerqtdDados.setMinValue(1);
        numberPickerqtdDados.setMaxValue(2);

        numberPickertipoDado.setMinValue(0);
        numberPickertipoDado.setMaxValue(6);
        numberPickertipoDado.setDisplayedValues( new String[] { "D4", "D6", "D8","D10","D12","D20","D100" } );

        numberPickerqtdAdicional.setMinValue(0);
        numberPickerqtdAdicional.setMaxValue(12);
        numberPickerqtdAdicional.setValue(6);
        String[] nums = {"-6","-5","-4","-3","-2","-1","0","1","2","3","4","5","6"};
        numberPickerqtdAdicional.setDisplayedValues(nums);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaJogadas.setLayoutManager(layoutManager);
        jogadasRecyclerAdapter = new JogadasRecyclerAdapter(getActivity());
        recyclerViewListaJogadas.setAdapter(jogadasRecyclerAdapter);

        btnRolarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jogada newJogada = new Jogada();
                String qtdDado = String.valueOf(numberPickerqtdDados.getValue());
                String tipoDado = String.valueOf(numberPickertipoDado.getValue());
                tipoDado = ajustaTipoDado(tipoDado);
                String adicional = String.valueOf(numberPickerqtdAdicional.getValue());
                adicional = ajustaAdicional(adicional);
                String comando = qtdDado + tipoDado + "+" + adicional;
                String resultado = "20";
                newJogada.setComando(comando);
                newJogada.setNomeAutor("Maximillian");
                newJogada.setResultado(resultado);
                jogadas.add(newJogada);
                jogadasRecyclerAdapter.setJogadas(jogadas);
                jogadasRecyclerAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }

    public String ajustaAdicional (String adicional) {
        switch(adicional){
            case "0":
                return "-6";
            case "1":
                return "-5";
            case "2":
                return "-4";
            case "3":
                return "-3";
            case "4":
                return "-2";
            case "5":
                return "-1";
            case "6":
                return "0";
            case "7":
                return "1";
            case "8":
                return "2";
            case "9":
                return "3";
            case "10":
                return "4";
            case "11":
                return "5";
            case "12":
                return "6";
        }
        return "10";

    }

    public String ajustaTipoDado (String tipoDado) {
        switch(tipoDado){
            case "0":
                return "D4";
            case "1":
                return "D6";
            case "2":
                return "D8";
            case "3":
                return "D10";
            case "4":
                return "D12";
            case "5":
                return "D20";
            case "6":
                return "D100";
        }
        return "D200";
    }

}
