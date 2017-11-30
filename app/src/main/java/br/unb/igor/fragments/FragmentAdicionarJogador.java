package br.unb.igor.fragments;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.DBFeedListener;
import br.unb.igor.helpers.Utils;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.User;
import br.unb.igor.recycleradapters.JogadoresRecyclerAdapter;

public class FragmentAdicionarJogador extends Fragment {

    public static String TAG = FragmentAdicionarJogador.class.getName();

    private ImageButton imgBtnLimparPesquisa;
    private EditText editTxtPesquisaJogadores;
    private TextView txtInfoLabel;
    private ProgressBar loadingSpinner;
    private RecyclerView recyclerViewListaPesquisaJogadores;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private JogadoresRecyclerAdapter jogadoresPesquisadosRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManagerJogadoresPesquisados;
    private List<User> users = new ArrayList<>();

    private AdventureListener mListener;

    private boolean isQueryingUsers;
    private int animationDuration = 320;
    private Handler handler = new Handler();

    private Runnable onBeginSearch = new Runnable() {
        @Override
        public void run() {
            txtInfoLabel.setText(R.string.msg_searching_users);
            recyclerViewListaPesquisaJogadores.setVisibility(View.INVISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
            loadingSpinner.setProgress(0);
            Utils.fadeInScaleUpView(txtInfoLabel, animationDuration);
            Utils.fadeInScaleUpView(loadingSpinner, animationDuration);
        }
    };

    private void onSearchReturn() {
        Utils.fadeOutView(txtInfoLabel, animationDuration);
        Utils.fadeOutView(loadingSpinner, animationDuration);
        txtInfoLabel.postDelayed(onSearchFinish, animationDuration);
    }

    private Runnable onSearchFinish = new Runnable() {
        @Override
        public void run() {
            if (users.isEmpty()) {
                txtInfoLabel.setText(getString(R.string.msg_no_users_to_show));
                Utils.fadeInScaleUpView(txtInfoLabel, animationDuration);
                recyclerViewListaPesquisaJogadores.setVisibility(View.INVISIBLE);
            } else {
                txtInfoLabel.setText("");
                recyclerViewListaPesquisaJogadores.setVisibility(View.VISIBLE);
                Utils.fadeInScaleUpView(recyclerViewListaPesquisaJogadores, animationDuration);
            }
            loadingSpinner.setVisibility(View.INVISIBLE);
            isQueryingUsers = false;
        }
    };

    private DBFeedListener feedListener = new DBFeedListener() {
        @Override
        public void onSelectedAdventureChange(Aventura adventure, Aventura old) {
            onAdventureChange(adventure, old);
        }
    };

    public FragmentAdicionarJogador() {
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
        ((ActivityHome)getActivity()).removeDBFeedListener(feedListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_adicionar_jogador, container, false);

        ((ActivityHome)getActivity()).addDBFeedListener(feedListener);

        recyclerViewListaPesquisaJogadores = root.findViewById(R.id.recyclerViewListaPesquisaJogadores);
        editTxtPesquisaJogadores = root.findViewById(R.id.editTextPesquisaUsuario);
        imgBtnLimparPesquisa= root.findViewById(R.id.imgBtnLimparTxtPesqUsuario);
        txtInfoLabel = root.findViewById(R.id.floatingText);
        loadingSpinner = root.findViewById(R.id.loadingSpinner);
        imgBtnLimparPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTxtPesquisaJogadores.setText("");
            }
        });

        layoutManagerJogadoresPesquisados = new LinearLayoutManager(getActivity());
        recyclerViewListaPesquisaJogadores.setLayoutManager(layoutManagerJogadoresPesquisados);

        JogadoresRecyclerAdapter.DisplayInfo di = new JogadoresRecyclerAdapter.DisplayInfo();

        Aventura currentAdventure = ((ActivityHome)getActivity()).getSelectedAdventure();
        User currentUser = ((ActivityHome)getActivity()).getCurrentUser();

        if (currentAdventure != null) {
            di.alreadyJoinedIds = currentAdventure.getJogadoresUserIdsSet();
            di.alreadyInvitedIds = currentAdventure.getJogadoresConvidadosIdsSet();
            di.isMaster = currentUser != null && currentUser.getUserId().equals(currentAdventure.getMestreUserId());
        }

        di.currentUserId = currentUser.getUserId();
        di.users = users;
        di.textColor = ColorStateList.valueOf(Color.WHITE);
        di.canPerformActions = true;

        jogadoresPesquisadosRecyclerAdapter = new JogadoresRecyclerAdapter(getActivity(),mListener, di, new JogadoresRecyclerAdapter.ListAdapterListener() {
            @Override
            public void onClickKickUsuario(User user, int index) {

            }
        });
        recyclerViewListaPesquisaJogadores.setAdapter(jogadoresPesquisadosRecyclerAdapter);

        editTxtPesquisaJogadores.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    if (isQueryingUsers) {
                        return false;
                    }
                    String query = editTxtPesquisaJogadores.getText().toString().toLowerCase();
                    if (!query.isEmpty()) {
                        clearKeyboard();
                        BuscarUsuarios(query);
                    }
                }
                return false;
            }
        });

        txtInfoLabel.setText(R.string.msg_no_users_to_show);
        editTxtPesquisaJogadores.setText("");
        users.clear();
        jogadoresPesquisadosRecyclerAdapter.notifyDataSetChanged();
        isQueryingUsers = false;

        return root;
    }

    public void BuscarUsuarios(final String query) {
        if (isQueryingUsers) {
            return;
        }
        isQueryingUsers = true;
        Utils.fadeOutView(txtInfoLabel, animationDuration);
        Utils.fadeOutView(loadingSpinner, animationDuration);
        Utils.fadeOutView(recyclerViewListaPesquisaJogadores, animationDuration);
        txtInfoLabel.postDelayed(onBeginSearch, animationDuration);
        editTxtPesquisaJogadores.clearFocus();
        final String userId = mAuth.getCurrentUser().getUid();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users.clear();
                        for (DataSnapshot val : dataSnapshot.getChildren()) {
                            if (val.child("userId").getValue(String.class).equals(userId)) {
                                continue;
                            }
                            if (val.child("email").getValue(String.class).toLowerCase().contains(query) ||
                                    val.child("fullName").getValue(String.class).toLowerCase().contains(query)){
                                users.add(val.getValue(User.class));
                                if (users.size() >= 10) {
                                    break;
                                }
                            }
                        }
                        jogadoresPesquisadosRecyclerAdapter.notifyDataSetChanged();
                        onSearchReturn();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        jogadoresPesquisadosRecyclerAdapter.notifyDataSetChanged();
                        onSearchReturn();
                    }
                });
            }
        }, animationDuration << 1);

    }

    public void clearKeyboard () {
        editTxtPesquisaJogadores.clearFocus();
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editTxtPesquisaJogadores.getWindowToken(),0);
    }

    public void onAdventureChange(Aventura newAdventure, Aventura oldAdventure) {
        if (newAdventure != null) {
            int index = 0;
            for (User u : users) {
                String uid = u.getUserId();
                boolean joined = oldAdventure.getJogadoresUserIdsSet().contains(uid);
                boolean invited = oldAdventure.getJogadoresConvidadosIdsSet().contains(uid);
                boolean shouldBeJoined = newAdventure.getJogadoresUserIdsSet().contains(uid);
                boolean shouldBeInvited = newAdventure.getJogadoresConvidadosIdsSet().contains(uid);
                if (joined != shouldBeJoined || invited != shouldBeInvited) {
                    jogadoresPesquisadosRecyclerAdapter.notifyItemChanged(index);
                }
                index++;
            }
        }
    }
}
