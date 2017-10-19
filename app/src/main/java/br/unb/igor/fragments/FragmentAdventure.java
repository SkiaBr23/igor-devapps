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
import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;
import br.unb.igor.recycleradapters.JogadoresRecyclerAdapter;
import br.unb.igor.recycleradapters.SessoesRecyclerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAdventure extends Fragment {

    public static final String TAG = FragmentAdventure.class.getName();
    private static final String SAVE_STATE_IS_ON_TAB_PLAYERS = "bTabPlayers";

    private String tituloAventura;
    private String keyAventura;
    private ImageView imgBackground;
    private TextView txtTituloAventuraEdicao;
    private EditText txtTituloAventuraEdicaoEdit;
    private TextView txtDescricaoAventura;
    private EditText txtDescricaoAventuraEdit;
    private ImageView abasJanelas;
    private TextView abaAndamento;
    private TextView abaJogadores;
    private ConstraintLayout boxAndamentoAventura;
    private ConstraintLayout boxJogadoresAventura;
    private AdventureEditListener mListener;
    private FloatingActionButton btnFAB;
    private FirebaseAuth mAuth;
    private CircleImageView profileImageMestre;
    private TextView txtNomeMestre;
    private TextView txtIndicadorNenhumaSessao;
    private TextView txtIndicadorNenhumJogador;

    private RecyclerView recyclerViewListaSessoes;
    private RecyclerView recyclerViewListaJogadores;
    private SessoesRecyclerAdapter sessoesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private JogadoresRecyclerAdapter jogadoresRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerJogadores;
    private List<Sessao> sessoes;
    private List<String> usersID;

    private boolean isInEditMode = false;

    private boolean isOnTabPlayers = false;

    private static String tipoExibicao = "listaJogadoresAventura";


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


    public FragmentAdventure() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_adventure, container, false);
        tituloAventura = getArguments().getString(Aventura.KEY_TITLE);
        keyAventura = getArguments().getString(Aventura.KEY_ID);
        imgBackground = root.findViewById(R.id.bkgEditarAventura);
        txtTituloAventuraEdicao = root.findViewById(R.id.txtTituloAventuraEdicao);
        txtTituloAventuraEdicaoEdit = root.findViewById(R.id.txtTituloAventuraEdicaoEdit);
        txtDescricaoAventura = root.findViewById(R.id.txtDescricaoAventura);
        txtDescricaoAventuraEdit = root.findViewById(R.id.txtDescricaoAventuraEdit);
        abasJanelas = root.findViewById(R.id.abasJanelas);
        abaAndamento = root.findViewById(R.id.abaAndamento);
        abaJogadores = root.findViewById(R.id.abaJogadores);
        btnFAB = root.findViewById(R.id.btnAdventureFAB);
        profileImageMestre = root.findViewById(R.id.profileImageMestre);
        txtNomeMestre = root.findViewById(R.id.txtNomeMestre);
        txtIndicadorNenhumaSessao = root.findViewById(R.id.txtIndicadorNenhumaSessao);
        txtIndicadorNenhumJogador = root.findViewById(R.id.txtIndicadorNenhumJogador);
        recyclerViewListaSessoes = root.findViewById(R.id.recyclerViewListaSessoes);
        recyclerViewListaJogadores = root.findViewById(R.id.recyclerViewListaJogadores);

        int backgroundResource = getArguments().getInt(Aventura.KEY_IMAGE, -1);
        imgBackground.setImageResource(ImageAssets.getBackgroundResource(backgroundResource));

        for (Aventura aventura : ((ActivityHome)getActivity()).getAdventures()) {
            if (aventura.getKey().equals(getArguments().getString("keyAventura"))) {
                tituloAventura = aventura.getTitulo();
                sessoes = aventura.getListaSessoes();
                usersID = aventura.getJogadoresUserIds();
                break;
            }
        }

        //Ajustar aqui quando houver busca de jogadores na base do FireBase
        txtIndicadorNenhumJogador.setVisibility(GONE);

        if (getSessoes().size() > 0) {
            txtIndicadorNenhumaSessao.setVisibility(GONE);
            recyclerViewListaSessoes.setVisibility(View.VISIBLE);
        } else {
            recyclerViewListaSessoes.setVisibility(GONE);
        }

        /*if (getUsersID().size() > 0) {
            txtIndicadorNenhumJogador.setVisibility(View.GONE);
            recyclerViewListaJogadores.setVisibility(View.VISIBLE);
        } else {
            recyclerViewListaJogadores.setVisibility(View.GONE);
        }*/

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
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
            txtNomeMestre.setText(R.string.msg_master_unknown_player);
        }

        boxAndamentoAventura = root.findViewById(R.id.boxAndamentoAventura);
        boxJogadoresAventura = root.findViewById(R.id.boxJogadoresAventura);

        abaJogadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(GONE);
                boxJogadoresAventura.setVisibility(View.VISIBLE);
                abasJanelas.setScaleX(-1.f);
                if (isInEditMode) {
                    btnFAB.setImageResource(R.drawable.botao_confirmar);
                } else {
                    btnFAB.setImageResource(R.drawable.botao_adicionar_jogadores);
                }
                isOnTabPlayers = true;
            }
        });

        abaAndamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boxAndamentoAventura.setVisibility(View.VISIBLE);
                boxJogadoresAventura.setVisibility(GONE);
                abasJanelas.setScaleX(1.f);
                if (isInEditMode) {
                    btnFAB.setImageResource(R.drawable.botao_confirmar);
                } else {
                    btnFAB.setImageResource(R.drawable.botao_adicionar_sessao);
                }
                isOnTabPlayers = false;
            }
        });

        btnFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isInEditMode) {
                    if (isOnTabPlayers) {
                        mListener.onAdicionarJogador(keyAventura);
                    } else {
                        mListener.onAdicionarSessao(keyAventura);
                    }
                }
            }
        });

        txtDescricaoAventura.setClickable(false);
        txtDescricaoAventura.setFocusable(false);

        txtTituloAventuraEdicao.setText(tituloAventura);
        txtTituloAventuraEdicaoEdit.setText(tituloAventura);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaSessoes.setLayoutManager(layoutManager);
        sessoesRecyclerAdapter = new SessoesRecyclerAdapter(getActivity(), mListener, getSessoes());
        recyclerViewListaSessoes.setAdapter(sessoesRecyclerAdapter);
        sessoesRecyclerAdapter.notifyDataSetChanged();

        layoutManagerJogadores = new LinearLayoutManager(getActivity());
        recyclerViewListaJogadores.setLayoutManager(layoutManagerJogadores);
        jogadoresRecyclerAdapter = new JogadoresRecyclerAdapter(getActivity(), mListener, getUsersID(), tipoExibicao);
        recyclerViewListaJogadores.setAdapter(jogadoresRecyclerAdapter);

        setRetainInstance(true);

        if (isOnTabPlayers) {
            abaJogadores.performClick();
        }

        setEditMode(isInEditMode);

        return root;
    }

    public List<Sessao> getSessoes () {
        if (this.sessoes == null) {
            this.sessoes = new ArrayList<>();
        }
        return this.sessoes;
    }

    public List<String> getUsersID () {
        if (this.usersID == null) {
            this.usersID = new ArrayList<>();
        }
        return this.usersID;
    }

    public void setEditMode(boolean b) {
        if (isInEditMode == b) {
            return;
        }

        isInEditMode = b;

        if (b) {
            txtDescricaoAventura.setVisibility(View.INVISIBLE);
            txtDescricaoAventuraEdit.setVisibility(View.VISIBLE);
            txtTituloAventuraEdicao.setVisibility(View.INVISIBLE);
            txtTituloAventuraEdicaoEdit.setVisibility(View.VISIBLE);
            btnFAB.setImageResource(R.drawable.botao_confirmar);
        } else {
            txtDescricaoAventura.setVisibility(View.VISIBLE);
            txtDescricaoAventuraEdit.setVisibility(View.GONE);
            txtTituloAventuraEdicao.setVisibility(View.VISIBLE);
            txtTituloAventuraEdicaoEdit.setVisibility(View.GONE);
            btnFAB.setImageResource(isOnTabPlayers ? R.drawable.botao_adicionar_jogadores : R.drawable.botao_adicionar_sessao);
        }

    }

    public boolean isInEditMode() {
        return isInEditMode;
    }

}
