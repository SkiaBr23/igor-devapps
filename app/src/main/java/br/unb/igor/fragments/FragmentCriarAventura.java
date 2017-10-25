package br.unb.igor.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;

public class FragmentCriarAventura extends Fragment {

    public static final String TAG = FragmentCriarAventura.class.getName();

    private ImageView imgFecharAventura;
    private EditText editTituloAventura;
    private Button btnConfirmarAventura;
    private FloatingActionButton btnFloatCloseAdventure;
    private AdventureListener mListener;

    private boolean clearEditTextOnCreate = false;

    public FragmentCriarAventura() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_create_adventure, container, false);

        imgFecharAventura = root.findViewById(R.id.btnCloseAventura);
        editTituloAventura = root.findViewById(R.id.editTituloAventura);
        btnFloatCloseAdventure = root.findViewById((R.id.btnCriarAventura));
        btnConfirmarAventura = root.findViewById(R.id.btnConfirmarAventura);
        btnConfirmarAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adventureTitle = editTituloAventura.getText().toString();
                if (adventureTitle.isEmpty()) {
                    editTituloAventura.requestFocus();
                    editTituloAventura.setError("Preencha com o título!");
                    return;
                } else {
                    editTituloAventura.setError(null);
                }

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                clearEditTextOnCreate = true;
                mListener.onCreateAdventure(adventureTitle);
            }
        });

        btnFloatCloseAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });

        imgFecharAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


//        editTituloAventura.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (clearEditTextOnCreate) {
            editTituloAventura.setText("");
            clearEditTextOnCreate = false;
        }
    }

}
