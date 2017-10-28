package br.unb.igor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.unb.igor.R;

public class FragmentAccount extends Fragment {

    public static String TAG = FragmentAccount.class.getName();

    public FragmentAccount() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_conta, container, false);
        return root;
    }
}
