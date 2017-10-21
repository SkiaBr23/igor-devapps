package br.unb.igor.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
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
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.User;
import br.unb.igor.recycleradapters.JogadoresRecyclerAdapter;

public class FragmentAdicionarJogador extends Fragment {

    public static String TAG = FragmentAdicionarJogador.class.getName();

    private String keyAventura;

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

    private AdventureEditListener mListener;

    private boolean isQueryingUsers;

    private Handler handler = new Handler();
    private AnimationSet animationSetFirst = new AnimationSet(false);
    private AnimationSet animationSetSecond = new AnimationSet(false);
    private Runnable onBeginSearch = new Runnable() {
        @Override
        public void run() {
            txtInfoLabel.setText(R.string.msg_searching_users);
            recyclerViewListaPesquisaJogadores.setVisibility(View.INVISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
            loadingSpinner.setProgress(0);
            txtInfoLabel.clearAnimation();
            txtInfoLabel.startAnimation(animationSetSecond);
            loadingSpinner.clearAnimation();
            loadingSpinner.startAnimation(animationSetSecond);
        }
    };

    private void onSearchReturn() {
        txtInfoLabel.clearAnimation();
        txtInfoLabel.startAnimation(animationSetFirst);
        txtInfoLabel.postDelayed(onSearchFinish, animationSetFirst.getDuration());
        loadingSpinner.clearAnimation();
        loadingSpinner.startAnimation(animationSetFirst);
    };

    private Runnable onSearchFinish = new Runnable() {
        @Override
        public void run() {
            if (users.isEmpty()) {
                txtInfoLabel.setText(getString(R.string.msg_no_users_to_show));
                txtInfoLabel.clearAnimation();
                txtInfoLabel.startAnimation(animationSetSecond);
                recyclerViewListaPesquisaJogadores.setVisibility(View.INVISIBLE);
            } else {
                txtInfoLabel.setText("");
                recyclerViewListaPesquisaJogadores.setVisibility(View.VISIBLE);
                recyclerViewListaPesquisaJogadores.clearAnimation();
                recyclerViewListaPesquisaJogadores.startAnimation(animationSetSecond);
            }
            loadingSpinner.setVisibility(View.INVISIBLE);
            isQueryingUsers = false;
        }
    };

    public FragmentAdicionarJogador() {
        Animation scaleUp = new ScaleAnimation(.72f, 1.f, .72f, 1.f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation fadeOut = new AlphaAnimation(1.f, 0.f);
        Animation fadeIn = new AlphaAnimation(0.f, 1.f);
        animationSetFirst.addAnimation(fadeOut);
        animationSetSecond.addAnimation(scaleUp);
        animationSetSecond.addAnimation(fadeIn);
        animationSetFirst.setDuration(300);
        animationSetSecond.setDuration(300);
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

        if (getArguments() != null) {
            if (getArguments().get(Aventura.KEY_ID) != null) {
                keyAventura = getArguments().getString(Aventura.KEY_ID);
            }
        }

        layoutManagerJogadoresPesquisados = new LinearLayoutManager(getActivity());
        recyclerViewListaPesquisaJogadores.setLayoutManager(layoutManagerJogadoresPesquisados);
        jogadoresPesquisadosRecyclerAdapter = new JogadoresRecyclerAdapter(mListener, users);
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
        txtInfoLabel.startAnimation(animationSetFirst);
        loadingSpinner.startAnimation(animationSetFirst);
        recyclerViewListaPesquisaJogadores.startAnimation(animationSetFirst);
        System.out.println("@@" + jogadoresPesquisadosRecyclerAdapter.getItemCount());
        System.out.println(recyclerViewListaPesquisaJogadores.getMeasuredWidth() + " - " +
                recyclerViewListaPesquisaJogadores.getMeasuredHeight());
        txtInfoLabel.postDelayed(onBeginSearch, animationSetFirst.getDuration());
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
        }, animationSetFirst.getDuration() + animationSetSecond.getDuration());

    }

    public void clearKeyboard () {
        editTxtPesquisaJogadores.clearFocus();
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editTxtPesquisaJogadores.getWindowToken(),0);
    }

}
