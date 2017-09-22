package br.unb.igor.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;
import br.unb.igor.recycleradapters.SessoesRecyclerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditarAventura extends Fragment {

    public static String TAG = FragmentHome.class.getName();
    private String tituloAventura;
    private String keyAventura;
    private TextView txtTituloAventuraEdicao;
    private EditText txtDescricaoAventura;
    private ImageView abasJanelas;
    private TextView abaAndamento;
    private TextView abaJogadores;
    private ConstraintLayout boxAndamentoAventura;
    private ConstraintLayout boxJogadoresAventura;
    private AdventureEditListener mListener;
    private FloatingActionButton btnAdicionarSessao;
    private FloatingActionButton btnAdicionarJogadores;
    private FirebaseAuth mAuth;
    private CircleImageView profileImageMestre;
    private TextView txtNomeMestre;
    private TextView txtIndicadorNenhumaSessao;

    private RecyclerView recyclerViewListaSessoes;
    private SessoesRecyclerAdapter sessoesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Sessao> sessoes;


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


    public FragmentEditarAventura() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.editar_aventura, container, false);
        tituloAventura = getArguments().getString("tituloAventura");
        keyAventura = getArguments().getString("keyAventura");
        txtTituloAventuraEdicao = (TextView)root.findViewById(R.id.txtTituloAventuraEdicao);
        txtDescricaoAventura = (EditText)root.findViewById(R.id.txtDescricaoAventura);
        abasJanelas = (ImageView)root.findViewById(R.id.abasJanelas);
        abaAndamento = (TextView)root.findViewById(R.id.abaAndamento);
        abaJogadores = (TextView)root.findViewById(R.id.abaJogadores);
        btnAdicionarSessao = (FloatingActionButton)root.findViewById(R.id.btnAdicionarSessao);
        btnAdicionarJogadores = (FloatingActionButton)root.findViewById(R.id.btnAdicionarJogador);
        profileImageMestre = (CircleImageView)root.findViewById(R.id.profileImageMestre);
        txtNomeMestre = (TextView)root.findViewById(R.id.txtNomeMestre);
        txtIndicadorNenhumaSessao = (TextView)root.findViewById(R.id.txtIndicadorNenhumaSessao);
        recyclerViewListaSessoes = (RecyclerView)root.findViewById(R.id.recyclerViewListaSessoes);

        System.out.println("Criou view");

        for (Aventura aventura : ((ActivityHome)getActivity()).getAdventures()) {
            if (aventura.getKey().equals(getArguments().getString("keyAventura"))) {
                sessoes = aventura.getSessoes();
            }
        }

        if (sessoes.size() > 0) {
            txtIndicadorNenhumaSessao.setVisibility(View.GONE);
            recyclerViewListaSessoes.setVisibility(View.VISIBLE);
        } else {
            recyclerViewListaSessoes.setVisibility(View.GONE);
        }

             mAuth = FirebaseAuth.getInstance();

             mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user.getDisplayName() != null) {
                txtNomeMestre.setText(user.getDisplayName());
            } else {
                txtNomeMestre.setText(user.getEmail());
            }
            if (user.getPhotoUrl() != null) {
                Picasso.with(profileImageMestre.getContext()).load(user.getPhotoUrl()).transform(new CircleTransform()).into(profileImageMestre);
            }

        } else {
            txtNomeMestre.setText("Unknown User");
        }


        boxAndamentoAventura = (ConstraintLayout)root.findViewById(R.id.boxAndamentoAventura);
        boxJogadoresAventura = (ConstraintLayout)root.findViewById(R.id.boxJogadoresAventura);

        abaJogadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(View.GONE);
                boxJogadoresAventura.setVisibility(View.VISIBLE);
                abasJanelas.setBackground(getResources().getDrawable(R.drawable.aba_dois));
                btnAdicionarSessao.setVisibility(View.GONE);
                btnAdicionarJogadores.setVisibility(View.VISIBLE);
            }
        });

        abaAndamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(View.VISIBLE);
                boxJogadoresAventura.setVisibility(View.GONE);
                abasJanelas.setBackground(getResources().getDrawable(R.drawable.aba_um));
                btnAdicionarSessao.setVisibility(View.VISIBLE);
                btnAdicionarJogadores.setVisibility(View.GONE);
            }
        });

        btnAdicionarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onAdicionarSessao(keyAventura);
            }
        });



        txtDescricaoAventura.setClickable(false);
        txtDescricaoAventura.setFocusable(false);

        txtTituloAventuraEdicao.setText(tituloAventura);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaSessoes.setLayoutManager(layoutManager);
        sessoesRecyclerAdapter = new SessoesRecyclerAdapter(getActivity(), mListener, getSessoes());
        recyclerViewListaSessoes.setAdapter(sessoesRecyclerAdapter);
        sessoesRecyclerAdapter.notifyDataSetChanged();

        return root;
    }

    public List<Sessao> getSessoes () {
        if (this.sessoes == null) {
            this.sessoes = new ArrayList<>();
        }
        return this.sessoes;
    }

}
