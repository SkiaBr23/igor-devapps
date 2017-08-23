package br.unb.igor.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.unb.igor.R;

public class FragmentHome extends Fragment {

    private static String TAG = FragmentHome.class.getName();
    private FloatingActionButton btnCriarAventura;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.home, container, false);

        btnCriarAventura = (FloatingActionButton)root.findViewById(R.id.btnCriarAventura);



        return root;
    }
}
