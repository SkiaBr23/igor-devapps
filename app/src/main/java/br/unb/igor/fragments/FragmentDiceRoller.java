package br.unb.igor.fragments;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.ChildEventListenerAdapter;
import br.unb.igor.helpers.DiceRoller;
import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.model.Aventura;
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
    private ImageView imgBackground;
    public Button btnRolarDados;
    public RecyclerView recyclerViewListaJogadas;
    private LinearLayoutManager layoutManager;
    private JogadasRecyclerAdapter jogadasRecyclerAdapter;
    private List<Jogada> jogadas;
    private FirebaseAuth mAuth;
    private User user;
    private AdventureListener mListener;
    private String selectedAdventureKey;
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    final int ROLLS_LIMIT = 10;

    private final ChildEventListener rollsListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Jogada ultimaJogada = dataSnapshot.getValue(Jogada.class);
            if (!existeJogada(ultimaJogada)) {
                jogadasRecyclerAdapter.setJogadas(jogadas);
                jogadas.add(ultimaJogada);
                jogadasRecyclerAdapter.notifyItemInserted(jogadas.size() - 1);
                recyclerViewListaJogadas.scrollToPosition(jogadas.size() - 1);
                limitaJogadas(ROLLS_LIMIT);
            }
        }
    };

    MediaPlayer mp = null;

    public FragmentDiceRoller() {
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
    public void onDestroyView() {
        super.onDestroyView();
        ref.child("rolls").child(selectedAdventureKey).removeEventListener(rollsListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_dice_roller, container, false);
        numberPickerqtdDados = root.findViewById(R.id.numberPickerQtdDados);
        imgBackground = root.findViewById(R.id.bkgJogada);
        numberPickertipoDado = root.findViewById(R.id.numberPickerTipoDado);
        numberPickerqtdAdicional = root.findViewById(R.id.numberPickerAdicional);
        btnRolarDados = root.findViewById(R.id.btnRolarDados);

        int backgroundResource =((ActivityHome) getActivity()).getSelectedAdventure().getImagemFundo();
        imgBackground.setImageResource(ImageAssets.getBackgroundResource(backgroundResource));
        recyclerViewListaJogadas = root.findViewById(R.id.recyclerViewListaJogadas);
        jogadas = new ArrayList<>();

        if (mp == null) {
            mp = MediaPlayer.create(getActivity(), R.raw.dice1);
        }

        numberPickerqtdDados.setMinValue(1);
        numberPickerqtdDados.setMaxValue(8);

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
        selectedAdventureKey = ((ActivityHome) getActivity()).getSelectedAdventure().getKey();

        ref.child("rolls").child(selectedAdventureKey).addChildEventListener(rollsListener);

        btnRolarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jogada newJogada = new Jogada();
                int qtdDado = numberPickerqtdDados.getValue();
                int tipoDado = numberPickertipoDado.getValue();
                tipoDado = ajustaTipoDado(tipoDado);
                int modificador = numberPickerqtdAdicional.getValue()  -6;
                int resultado = DiceRoller.roll(tipoDado, qtdDado, modificador);
                Double probabilidade = DiceRoller.probability(resultado - modificador, tipoDado, qtdDado);
                Double probabilidadePeloMenos = DiceRoller.probabilityAtLeast(resultado - modificador, tipoDado, qtdDado);
                String comando = DiceRoller.diceToText(tipoDado,qtdDado,modificador);
                newJogada.setComando(comando);
                newJogada.setNomeAutor(user.getFullName());
                newJogada.setResultado(String.valueOf(resultado));
                newJogada.setProbabilidade(probabilidade);
                newJogada.setProbabilidadePeloMenos(probabilidadePeloMenos);
                newJogada.setIdAutor(user.getUserId());
                newJogada.setUrlFotoAutor(user.getProfilePictureUrl());
                newJogada.setKeyAventura(((ActivityHome) getActivity()).getSelectedAdventure().getKey());
                newJogada.setTipo(Jogada.getTipoRolagem(resultado,qtdDado,tipoDado,modificador));
                jogadas.add(newJogada);
                jogadasRecyclerAdapter.setJogadas(jogadas);
                jogadasRecyclerAdapter.notifyItemInserted(jogadas.size() - 1);
                limitaJogadas(ROLLS_LIMIT);
                recyclerViewListaJogadas.scrollToPosition(jogadas.size() - 1);
                mp.start();
                btnRolarDados.setEnabled(false);
                btnRolarDados.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnRolarDados.setEnabled(true);
                        mp.pause();
                    }
                }, mp.getDuration() + 100);

                mListener.onCreateRoll(newJogada);
            }
        });

        return root;
    }

    public void limitaJogadas(int quantidade){
        int removed = jogadas.size() - quantidade;
        if (removed > 0) {
            while (jogadas.size() > quantidade) {
                jogadas.remove(0);
            }
            jogadasRecyclerAdapter.notifyItemRangeRemoved(0, removed);
        }
    }

    public boolean existeJogada(Jogada jogada){
        for (Jogada j : jogadas){
            if (j.getKey().equals(jogada.getKey())){
                return true;
            }
        }
        return false;
    }

    public int ajustaTipoDado (Integer tipoDado) {
        switch (tipoDado) {
            case 0:
                return 4;
            case 1:
                return 6;
            case 2:
                return 8;
            case 3:
                return 10;
            case 4:
                return 12;
            case 5:
                return 20;
            case 6:
                return 100;
        }
        return 200;
    }

}
