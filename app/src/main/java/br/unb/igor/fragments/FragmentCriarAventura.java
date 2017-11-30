package br.unb.igor.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.TextView;

import java.util.Random;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.ImageAssets;

public class FragmentCriarAventura extends Fragment {

    public static final String TAG = FragmentCriarAventura.class.getName();

    private ImageView imgBackground;
    private ImageView imgFecharAventura;
    private EditText editTituloAventura;
    private Button btnConfirmarAventura;
    private Button btnBackgroundLeft;
    private Button btnBackgroundRight;
    private ImageView imgBackgroundChoose;
    private TextView txtBackgroundIndex;
    private FloatingActionButton btnFloatCloseAdventure;
    private AdventureListener mListener;

    private boolean clearEditTextOnCreate = false;
    private int backgroundIndex;

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

        imgBackground = root.findViewById(R.id.bkgEditarAventura);
        imgFecharAventura = root.findViewById(R.id.btnCloseAventura);
        editTituloAventura = root.findViewById(R.id.editTituloAventura);
        btnFloatCloseAdventure = root.findViewById((R.id.btnCriarAventura));
        btnConfirmarAventura = root.findViewById(R.id.btnConfirmarAventura);
        btnBackgroundLeft = root.findViewById(R.id.btnBackgroundLeft);
        btnBackgroundRight = root.findViewById(R.id.btnBackgroundRight);
        imgBackgroundChoose = root.findViewById(R.id.imgBackground);
        txtBackgroundIndex = root.findViewById(R.id.txtBackgroundIndex);

        backgroundIndex = new Random().nextInt(ImageAssets.getBuiltInBackgroundCount());
        updateBackground();

        btnBackgroundLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundIndex--;
                updateBackground();
            }
        });

        btnBackgroundRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundIndex++;
                updateBackground();
            }
        });

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
                mListener.onCreateAdventure(adventureTitle, backgroundIndex);
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

    private void updateBackground() {
        int limit = ImageAssets.getBuiltInBackgroundCount();
        if (backgroundIndex < 0) {
            backgroundIndex = limit - 1;
        } else if (backgroundIndex >= limit) {
            backgroundIndex %= limit;
        }
        int backgroundResource = ImageAssets.getBackgroundResource(backgroundIndex);
        imgBackground.setImageResource(backgroundResource);
        imgBackgroundChoose.setImageResource(backgroundResource);
        txtBackgroundIndex.setText(String.valueOf(backgroundIndex + 1));
    }

}
