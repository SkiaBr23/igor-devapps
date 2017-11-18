package br.unb.igor.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.model.Livro;
import br.unb.igor.recycleradapters.LivrosRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBooks extends Fragment {

    public static String TAG = FragmentBooks.class.getName();

    private RecyclerView recyclerViewListaLivros;
    private LivrosRecyclerAdapter livrosRecyclerAdapter;
    private GridLayoutManager gridLayoutManager;
    private List<Livro> livros;



    public FragmentBooks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_books, container, false);
        livrosRecyclerAdapter = new LivrosRecyclerAdapter(getActivity(), new LivrosRecyclerAdapter.ListAdapterListener() {
        });
        this.livros = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerViewListaLivros = root.findViewById(R.id.recyclerViewListaLivros);
        recyclerViewListaLivros.setLayoutManager(gridLayoutManager);
        //livrosRecyclerAdapter.setLivros(this.livros);
        recyclerViewListaLivros.setAdapter(livrosRecyclerAdapter);
        return root;
    }

}
