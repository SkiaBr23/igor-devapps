package br.unb.igor.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private FloatingActionButton btnFABDiceRoller;
    private FirebaseAuth mAuth;
    private CircleImageView profileImageMestre;
    private TextView txtNomeMestre;
    private TextView txtIndicadorNenhumaSessao;
    private TextView txtInfoLabel;
    private ProgressBar loadingSpinner;

    private RecyclerView recyclerViewListaSessoes;
    private RecyclerView recyclerViewListaJogadores;
    private SessoesRecyclerAdapter sessoesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private JogadoresRecyclerAdapter jogadoresRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerJogadores;
    private List<Sessao> sessoes = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private User master = null;

    JogadoresRecyclerAdapter.DisplayInfo di;

    private enum FetchState {
        NotFetched,
        Fetching,
        Fetched
    }

    private FetchState usersFetchState = FetchState.NotFetched;
    private FetchState masterUserFetchState = FetchState.NotFetched;

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
        final View root = inflater.inflate(R.layout.fragment_adventure, container, false);

        aventura = ((ActivityHome)getActivity()).getSelectedAdventure();

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
        txtInfoLabel = root.findViewById(R.id.txtInfoLabel);
        loadingSpinner = root.findViewById(R.id.loadingSpinner);
        recyclerViewListaSessoes = root.findViewById(R.id.recyclerViewListaSessoes);
        recyclerViewListaJogadores = root.findViewById(R.id.recyclerViewListaJogadores);
        btnFABDiceRoller = root.findViewById(R.id.btnFABDiceRoller);

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
                    DB.upsertAdventure(aventura);
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

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewListaSessoes.setLayoutManager(layoutManager);
        sessoesRecyclerAdapter = new SessoesRecyclerAdapter(getActivity(), mListener, sessoes);
        recyclerViewListaSessoes.setAdapter(sessoesRecyclerAdapter);

        di = new JogadoresRecyclerAdapter.DisplayInfo();

        di.isMaster = isCurrentUserMaster();
        di.currentUserId = getCurrentUserId();
        di.canPerformActions = false;

        if (aventura != null) {
            di.alreadyJoinedIds = aventura.getJogadoresUserIdsSet();
            di.alreadyInvitedIds = aventura.getJogadoresConvidadosIdsSet();
            di.users = users;
        }

        if (di.isMaster) {
            root.findViewById(R.id.boxYouIndicator).setVisibility(View.VISIBLE);
        } else {
            btnFAB.setVisibility(View.GONE);
            btnFAB.setEnabled(false);
        }

        jogadoresRecyclerAdapter = new JogadoresRecyclerAdapter(getActivity(),mListener, di, new JogadoresRecyclerAdapter.ListAdapterListener() {
            @Override
            public void onClickKickUsuario(User user, int index) {
                jogadoresRecyclerAdapter.getDisplayInfo().users.remove(index);
                jogadoresRecyclerAdapter.notifyDataSetChanged();
                mListener.onUserKickedOut(user);
            }
        });

        layoutManagerJogadores = new LinearLayoutManager(getActivity());
        recyclerViewListaJogadores.setLayoutManager(layoutManagerJogadores);
        recyclerViewListaJogadores.setAdapter(jogadoresRecyclerAdapter);

        if (isOnTabPlayers) {
            abaJogadores.performClick();
        }

        setEditMode(false);
        loadMasterInfo();
        updateAdventureInfo();

        btnFABDiceRoller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.rolagemDados();
            }
        });

        return root;
    }

    private String getCurrentUserId() {
        FirebaseUser user = mAuth != null ? mAuth.getCurrentUser() : null;
        return user != null ? user.getUid() : null;
    }

    public void setEditMode(boolean b) {
        if (isInEditMode == b) {
            return;
        }
        isInEditMode = b;
        jogadoresRecyclerAdapter.getDisplayInfo().canPerformActions = b;
        di.alreadyJoinedIds = aventura.getJogadoresUserIdsSet();
        if (b) {
            jogadoresRecyclerAdapter.notifyDataSetChanged();
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
        return aventura != null && mAuth.getCurrentUser() != null && aventura.getMestreUserId().equals(mAuth.getCurrentUser().getUid());
    }

    public void fetchUsers() {
        if (usersFetchState == FetchState.Fetched) {
            boolean isMaster = isCurrentUserMaster();
            if (users.isEmpty()) {
                if (isMaster) {
                    txtInfoLabel.setText(R.string.msg_you_havent_invited_players_yet);
                } else {
                    txtInfoLabel.setText(R.string.msg_no_players_to_show);
                }
            } else {
                txtInfoLabel.setVisibility(View.GONE);
                recyclerViewListaJogadores.setVisibility(View.VISIBLE);
            }
            loadingSpinner.setVisibility(View.GONE);
            return;
        }
        recyclerViewListaJogadores.setVisibility(View.GONE);
        txtInfoLabel.setVisibility(View.VISIBLE);
        txtInfoLabel.setText(R.string.label_loading);
        loadingSpinner.setVisibility(View.VISIBLE);
        if (usersFetchState == FetchState.Fetching) {
            return;
        }
        usersFetchState = FetchState.Fetching;
        List<String> players = aventura.getJogadoresUserIds();
        List<String> invited = aventura.getJogadoresConvidadosIds();
        List<String> concat = new ArrayList<>(players);
        concat.addAll(invited);
        DB.getUsersById(concat,
            new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null) {
                        List<User> fetchedUsers = (List)extra;
                        users.clear();
                        users.addAll(fetchedUsers);
                        jogadoresRecyclerAdapter.notifyDataSetChanged();
                        usersFetchState = FetchState.Fetched;
                        fetchUsers();
                    }
                }
            }));
    }

    public void loadMasterInfo() {
        if (masterUserFetchState == FetchState.Fetched) {
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
        } else if (masterUserFetchState == FetchState.NotFetched && aventura != null) {
            masterUserFetchState = FetchState.Fetching;
            DB.getUserInfoById(aventura.getMestreUserId(),
                new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                    @Override
                    public void onComplete(boolean cancelled, Object extra, int step) {
                        masterUserFetchState = FetchState.Fetched;
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

    public void updateAdventureInfo() {
        if (aventura != null) {
            txtTituloAventuraEdicao.setText(aventura.getTitulo());
            txtDescricaoAventura.setText(aventura.getSinopse());

            int backgroundResource = aventura.getImagemFundo();
            imgBackground.setImageResource(ImageAssets.getBackgroundResource(backgroundResource));

            sessoes.clear();
            sessoes.addAll(aventura.getListaSessoes());

            if (sessoes.size() > 0) {
                txtIndicadorNenhumaSessao.setVisibility(View.GONE);
                recyclerViewListaSessoes.setVisibility(View.VISIBLE);
            } else {
                txtIndicadorNenhumaSessao.setVisibility(View.VISIBLE);
                recyclerViewListaSessoes.setVisibility(View.GONE);
            }

            sessoesRecyclerAdapter.notifyDataSetChanged();
            fetchUsers();
        }
    }

    public void onAdventureChange(Aventura newAdventure, boolean isThisFragmentVisible) {
        Aventura old = aventura;
        aventura = newAdventure;
        if (old != null && newAdventure != null) {
            if (!old.getMestreUserId().equals(newAdventure.getMestreUserId())) {
                masterUserFetchState = FetchState.NotFetched;
                loadMasterInfo();
            }
            Set<String> joinedPlayers = newAdventure.getJogadoresUserIdsSet();
            Set<String> invitedPlayers = newAdventure.getJogadoresConvidadosIdsSet();
            Set<String> oldJoinedPlayers = old.getJogadoresUserIdsSet();
            Set<String> oldInvitedPlayers = old.getJogadoresConvidadosIdsSet();
            Set<String> oldUIDs = new ArraySet<>(users.size());
            JogadoresRecyclerAdapter.DisplayInfo di = jogadoresRecyclerAdapter.getDisplayInfo();
            di.alreadyInvitedIds = invitedPlayers;
            di.alreadyJoinedIds = joinedPlayers;
            boolean isMaster = isCurrentUserMaster();
            for (int userIndex = users.size() - 1; userIndex >= 0; userIndex--) {
                User u = users.get(userIndex);
                String uid = u.getUserId();
                oldUIDs.add(uid);
                boolean hasJoined = oldJoinedPlayers.contains(uid);
                boolean isInvited = oldInvitedPlayers.contains(uid);
                boolean shouldBeJoined = joinedPlayers.contains(uid);
                boolean shouldBeInvited = invitedPlayers.contains(uid);
                if ((hasJoined != shouldBeJoined) || (isInvited != shouldBeInvited)) {
                    if (!shouldBeInvited && !shouldBeJoined) {
                        if (isThisFragmentVisible && isMaster) {
                            jogadoresRecyclerAdapter.notifyItemChanged(userIndex);
                        } else {
                            users.remove(userIndex);
                            jogadoresRecyclerAdapter.notifyItemRemoved(userIndex);
                        }
                    } else {
                        jogadoresRecyclerAdapter.notifyItemChanged(userIndex);
                    }
                } else if (!shouldBeInvited && !shouldBeJoined && !isThisFragmentVisible) {
                    users.remove(userIndex);
                    jogadoresRecyclerAdapter.notifyItemRemoved(userIndex);
                }
            }
            List<String> newUIDs = new ArrayList<>();
            for (String uid : newAdventure.getJogadoresUserIds()) {
                if (!oldUIDs.contains(uid)) {
                    newUIDs.add(uid);
                }
            }
            for (String uid : newAdventure.getJogadoresConvidadosIds()) {
                if (!oldUIDs.contains(uid)) {
                    newUIDs.add(uid);
                }
            }
            DB.getUsersById(newUIDs, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null && extra instanceof List) {
                        if (usersFetchState != FetchState.Fetched) {
                            return;
                        }
                        List<User> newUsers = (List<User>) extra;
                        int positionStart = users.size() + 1;
                        users.addAll(newUsers);
                        jogadoresRecyclerAdapter.notifyItemRangeInserted(positionStart, newUsers.size());
                    }
                }
            }));
            di.alreadyInvitedIds = oldInvitedPlayers;
            di.alreadyJoinedIds = oldJoinedPlayers;
        }
        updateAdventureInfo();
        aventura = old;
    }
}
