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
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;
import br.unb.igor.recycleradapters.JogadoresRecyclerAdapter;
import br.unb.igor.recycleradapters.SessoesRecyclerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class FragmentAdventure extends Fragment {

    public static final String TAG = FragmentAdventure.class.getName();

    private String tituloAventura;
    private ImageView imgBackground;
    private ScrollView txtDescricaoContainer;
    private TextView txtTituloAventuraEdicao;
    private EditText txtTituloAventuraEdicaoEdit;
    private TextView txtDescricaoAventura;
    private EditText txtDescricaoAventuraEdit;
    private ImageView abasJanelas;
    private TextView abaAndamento;
    private TextView abaJogadores;
    private ConstraintLayout boxAndamentoAventura;
    private ConstraintLayout boxJogadoresAventura;
    private AdventureListener mListener;
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
    private List<User> users = new ArrayList<>();
    private User master = null;
    private boolean isMasterFetched = false;

    private Aventura aventura = null;

    private boolean isInEditMode = false;
    private boolean isOnTabPlayers = false;


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

    public FragmentAdventure() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_adventure, container, false);

        imgBackground = root.findViewById(R.id.bkgEditarAventura);
        txtDescricaoContainer = root.findViewById(R.id.adventureDescriptionScrollView);
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

        aventura = ((ActivityHome)getActivity()).getSelectedAdventure();
        int backgroundResource = 0;

        if (aventura != null) {
            tituloAventura = aventura.getTitulo();
            sessoes = aventura.getListaSessoes();
            backgroundResource = aventura.getImagemFundo();
            fetchUsers();
        }

        imgBackground.setImageResource(ImageAssets.getBackgroundResource(backgroundResource));

        if (getSessoes().size() > 0) {
            txtIndicadorNenhumaSessao.setVisibility(GONE);
            recyclerViewListaSessoes.setVisibility(View.VISIBLE);
        } else {
            recyclerViewListaSessoes.setVisibility(GONE);
        }

        int playersCount = aventura.getJogadoresUserIds().size();

        if (playersCount > 1) {
            txtIndicadorNenhumJogador.setVisibility(View.VISIBLE);
        } else {
            txtIndicadorNenhumJogador.setVisibility(View.GONE);
        }

        mAuth = FirebaseAuth.getInstance();
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
                if (isInEditMode) {
                    String title = txtTituloAventuraEdicaoEdit.getText().toString();
                    String descr = txtDescricaoAventuraEdit.getText().toString();
                    txtTituloAventuraEdicao.setText(title);
                    txtDescricaoAventura.setText(descr);
                    aventura.setTitulo(title);
                    aventura.setSinopse(descr);
                    DB.getLastInstance().upsertAdventure(aventura);
                    setEditMode(false);
                } else {
                    if (isOnTabPlayers) {
                        mListener.onClickInviteUsersFAB();
                    } else {
                        mListener.onClickAddSessionFAB();
                    }
                }
            }
        });

        txtDescricaoAventura.setClickable(false);
        txtDescricaoAventura.setFocusable(false);

        txtTituloAventuraEdicao.setText(tituloAventura);
        txtDescricaoAventura.setText(aventura.getSinopse());

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaSessoes.setLayoutManager(layoutManager);
        sessoesRecyclerAdapter = new SessoesRecyclerAdapter(getActivity(), mListener, getSessoes());
        recyclerViewListaSessoes.setAdapter(sessoesRecyclerAdapter);
        sessoesRecyclerAdapter.notifyDataSetChanged();

        if (aventura != null && isCurrentUserMaster()) {
            jogadoresRecyclerAdapter = new JogadoresRecyclerAdapter(mListener, users,
                    aventura.getJogadoresConvidadosIdsSet(),
                    aventura.getJogadoresUserIdsSet());
        } else {
            jogadoresRecyclerAdapter = new JogadoresRecyclerAdapter(mListener, users);
        }
        loadMasterInfo();
        layoutManagerJogadores = new LinearLayoutManager(getActivity());
        recyclerViewListaJogadores.setLayoutManager(layoutManagerJogadores);
        recyclerViewListaJogadores.setAdapter(jogadoresRecyclerAdapter);

        if (isOnTabPlayers) {
            abaJogadores.performClick();
        }

        setEditMode(false);

        return root;
    }

    public List<Sessao> getSessoes () {
        if (this.sessoes == null) {
            this.sessoes = new ArrayList<>();
        }
        return this.sessoes;
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
            txtDescricaoContainer.setBackgroundResource(R.drawable.rectangle_outline);
            txtDescricaoAventuraEdit.setText(txtDescricaoAventura.getText().toString());
            txtTituloAventuraEdicaoEdit.setText(txtTituloAventuraEdicao.getText().toString());
        } else {
            txtDescricaoAventura.setVisibility(View.VISIBLE);
            txtDescricaoAventuraEdit.setVisibility(View.GONE);
            txtTituloAventuraEdicao.setVisibility(View.VISIBLE);
            txtTituloAventuraEdicaoEdit.setVisibility(View.GONE);
            btnFAB.setImageResource(isOnTabPlayers ? R.drawable.botao_adicionar_jogadores : R.drawable.botao_adicionar_sessao);
            txtDescricaoContainer.setBackgroundResource(0);
        }
    }

    public boolean isInEditMode() {
        return isInEditMode;
    }

    public boolean isCurrentUserMaster() {
        return aventura != null && aventura.getMestreUserId().equals(mAuth.getCurrentUser().getUid());
    }

    public void fetchUsers() {
        if (aventura.getJogadoresUserIds().size() == users.size()) {
            return;
        }
        DB.getLastInstance().getUsersById(aventura.getJogadoresUserIds(),
            new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null) {
                        List<User> users = (List)extra;
                        FragmentAdventure.this.users.clear();
                        for (User user : users) {
                            FragmentAdventure.this.users.add(user);
                        }
                        jogadoresRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }));
    }

    public void loadMasterInfo() {
        if (isMasterFetched) {
            if (master == null) {
                txtNomeMestre.setText(R.string.msg_master_unknown_player);
            } else {
                txtNomeMestre.setText(master.getFullName());
                String photoUrl = master.getProfilePictureUrl();
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Picasso
                        .with(profileImageMestre.getContext())
                        .load(photoUrl)
                        .transform(new CircleTransform())
                        .into(profileImageMestre);
                }
            }
        } else {
            DB.getLastInstance().getUserInfoById(aventura.getMestreUserId(),
                new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                    @Override
                    public void onComplete(boolean cancelled, Object extra, int step) {
                        isMasterFetched = true;
                        if (cancelled || extra == null || !(extra instanceof User)) {
                            master = null;
                        } else {
                            master = (User) extra;
                        }
                        loadMasterInfo();
                    }
                }
            ));
        }
    }

}
