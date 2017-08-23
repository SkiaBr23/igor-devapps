package br.unb.igor.fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import br.unb.igor.R;

public class FragmentCriarAventura extends Fragment {

    private ImageView imgFecharAventura;

    public FragmentCriarAventura() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.criar_aventura, container, false);

        imgFecharAventura = (ImageView)root.findViewById(R.id.btnCloseAventura);
        imgFecharAventura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return root;
    }

}
