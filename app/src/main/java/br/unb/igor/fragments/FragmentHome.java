package br.unb.igor.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.model.Aventura;
import br.unb.igor.recycleradapters.AventurasRecyclerAdapter;

public class FragmentHome extends Fragment {

    public static String TAG = FragmentHome.class.getName();

    private FloatingActionButton btnCriarAventura;
    private FloatingActionButton btnModoEdicao;
    private FloatingActionButton btnConfirmarAlteracao;

    private FragmentCriarAventura mFragmentCriarAventura;
    private TextView txtFloatingMessage;
    private ProgressBar progressBarLoading;
    private RecyclerView recyclerViewAventurasHome;
    private AventurasRecyclerAdapter aventurasRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Aventura> aventuras;
    private CardView cardInfoConvites;
    private AdventureListener mListener;
    private boolean isInEditMode = false;
    private boolean isLoading = true;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        aventuras = ((ActivityHome)getActivity()).getAdventures();
        setRetainInstance(true);

        txtFloatingMessage = root.findViewById(R.id.floatingText);
        txtFloatingMessage.setText(R.string.msg_loading_adventures);

        progressBarLoading = root.findViewById(R.id.loadingSpinner);

        cardInfoConvites = (CardView)root.findViewById(R.id.cardInformativoConvites);

        //TODO: Esse gone aqui precisa ser removido quando chamar a função de verificar convites e houver convites!
        cardInfoConvites.setVisibility(View.GONE);

        setIsLoading(isLoading);

        btnModoEdicao = root.findViewById(R.id.btnModoEdicao);
        btnCriarAventura = root.findViewById(R.id.btnCriarAventura);
        btnConfirmarAlteracao = root.findViewById(R.id.btnConfirmarAlteracao);

        // Set Fira Sans (Regular) font
        Typeface firaSans = Typeface.createFromAsset(this.getActivity().getAssets(), "FiraSans-Regular.ttf");
        txtFloatingMessage.setTypeface(firaSans);

        btnCriarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                if (mFragmentCriarAventura == null) {
                    Fragment f = fm.findFragmentByTag(FragmentCriarAventura.TAG);
                    if (f != null)
                        mFragmentCriarAventura = (FragmentCriarAventura)f;
                    else
                        mFragmentCriarAventura = new FragmentCriarAventura();
                }

                ((ActivityHome) getActivity()).pushFragment(mFragmentCriarAventura, FragmentCriarAventura.TAG);
            }
        });

        btnConfirmarAlteracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditMode(false);
            }
        });

        recyclerViewAventurasHome = (RecyclerView)root.findViewById(R.id.recyclerViewAventurasHome);
        // setHasFixedSize(true) bugs notifyItemInserted when fetching initial adventures
        // recyclerViewAventurasHome.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAventurasHome.setLayoutManager(layoutManager);
        aventurasRecyclerAdapter = new AventurasRecyclerAdapter(getActivity(), mListener, aventuras);
        recyclerViewAventurasHome.setAdapter(aventurasRecyclerAdapter);

        return root;
    }

    public final AventurasRecyclerAdapter getRecyclerAdapter() {
        return aventurasRecyclerAdapter;
    }

    public void scrollToIndex(int index) {
        recyclerViewAventurasHome.scrollToPosition(index);
    }

    public void setEditMode(boolean b) {
        if (isInEditMode != b) {
            isInEditMode = b;
            aventurasRecyclerAdapter.setEditMode(b);

            if (b) {
                //btnModoEdicao.setVisibility(View.VISIBLE);
                btnCriarAventura.setVisibility(View.GONE);
                btnConfirmarAlteracao.setVisibility(View.VISIBLE);
            } else {
                //btnModoEdicao.setVisibility(View.GONE);
                btnCriarAventura.setVisibility(View.VISIBLE);
                btnConfirmarAlteracao.setVisibility(View.GONE);
            }
        }
    }

    public boolean isInEditMode() {
        return isInEditMode;
    }

    public void toggleEditMode() {
        setEditMode(!isInEditMode);
    }

    public void setIsLoading(boolean b) {

        isLoading = b;

        if (txtFloatingMessage != null)
            txtFloatingMessage.setVisibility(b ? View.VISIBLE : View.GONE);

        if (progressBarLoading != null)
            progressBarLoading.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void notifyItemChangedVisible() {
        LinearLayoutManager llm = (LinearLayoutManager)recyclerViewAventurasHome.getLayoutManager();
        int from = llm.findFirstVisibleItemPosition();
        int to = llm.findLastVisibleItemPosition();
        aventurasRecyclerAdapter.notifyItemRangeChanged(from, to - from + 1);
    }

}
