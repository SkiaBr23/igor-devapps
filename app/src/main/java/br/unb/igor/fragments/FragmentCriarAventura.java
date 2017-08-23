package br.unb.igor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.unb.igor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCriarAventura extends Fragment {
    public FragmentCriarAventura() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_criar_aventura, container, false);
    }

}
