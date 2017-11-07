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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.DiceRoller;
import br.unb.igor.model.Jogada;
import br.unb.igor.model.User;
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
    private LinearLayoutManager layoutManager;
    private JogadasRecyclerAdapter jogadasRecyclerAdapter;
    private List<Jogada> jogadas;
    private FirebaseAuth mAuth;
    private User user;


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
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerViewListaJogadas.setLayoutManager(layoutManager);
        jogadasRecyclerAdapter = new JogadasRecyclerAdapter(getActivity());
        recyclerViewListaJogadas.setAdapter(jogadasRecyclerAdapter);



        mAuth = FirebaseAuth.getInstance();
        user = ((ActivityHome)getActivity()).getCurrentUser();

        btnRolarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jogada newJogada = new Jogada();
                String qtdDado = String.valueOf(numberPickerqtdDados.getValue());
                String tipoDado = String.valueOf(numberPickertipoDado.getValue());
                tipoDado = ajustaTipoDado(tipoDado);
                String adicional = String.valueOf(numberPickerqtdAdicional.getValue()+(-6));
                String comando = qtdDado + tipoDado + "+(" + adicional + ")";
                String resultado = obterResultado(comando);
                newJogada.setComando(comando);
                newJogada.setNomeAutor(user.getFullName());
                newJogada.setResultado(resultado);
                newJogada.setIdAutor(user.getUserId());
                newJogada.setUrlFotoAutor(user.getProfilePictureUrl());
                jogadas.add(newJogada);
                jogadasRecyclerAdapter.setJogadas(jogadas);
                jogadasRecyclerAdapter.notifyDataSetChanged();
                recyclerViewListaJogadas.scrollToPosition(jogadas.size()-1);
            }
        });

        return root;
    }

    public String obterResultado (String comando) {
        int qtdDado = Integer.valueOf(String.valueOf(comando.charAt(0)));
        int faces;
        String[] parts = comando.split("\\+");
        if (parts[0].length() == 3) {
             faces = Integer.valueOf(String.valueOf(parts[0].charAt(2)));
        } else if (parts[0].length() == 4) {
            String facesString = String.valueOf(parts[0].charAt(2))+String.valueOf(parts[0].charAt(3));
            faces = Integer.valueOf(facesString);
        } else {
            String facesString = String.valueOf(parts[0].charAt(2))+String.valueOf(parts[0].charAt(3))+String.valueOf(parts[0].charAt(4));
            faces = Integer.valueOf(facesString);
        }
        int adicional;
        if (parts[1].length() == 3) {
            adicional = Integer.valueOf(String.valueOf(parts[1].charAt(1)));
        } else {
            adicional = Integer.valueOf(String.valueOf(parts[1].charAt(2)))*(-1);
        }

        return String.valueOf(DiceRoller.roll(faces,qtdDado,adicional));
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
