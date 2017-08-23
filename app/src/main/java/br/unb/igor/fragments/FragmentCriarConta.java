package br.unb.igor.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.unb.igor.R;

public class FragmentCriarConta extends Fragment {

    private static String TAG = FragmentCriarConta.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ContextThemeWrapper wrapper = new ContextThemeWrapper(getActivity(), R.style.AppFullScreenCriarConta);
        LayoutInflater localInflater = inflater.cloneInContext(wrapper);
        final View root = localInflater.inflate(R.layout.criar_conta, container, false);
        return root;
    }
}
