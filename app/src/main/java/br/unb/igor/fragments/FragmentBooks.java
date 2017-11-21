package br.unb.igor.fragments;


import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
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
    private File booksPath = Environment.getExternalStoragePublicDirectory(
            "Igor/books");

    public FragmentBooks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_books, container, false);
        livrosRecyclerAdapter = new LivrosRecyclerAdapter(getActivity(), new LivrosRecyclerAdapter.ListAdapterListener() {
            @Override
            public void onClickBaixarLivro(Livro livro, int index) {
                downloadBook(livro);
            }
        });
        this.livros = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerViewListaLivros = root.findViewById(R.id.recyclerViewListaLivros);
        recyclerViewListaLivros.setLayoutManager(gridLayoutManager);
        livrosRecyclerAdapter.setLivros(this.livros);
        recyclerViewListaLivros.setAdapter(livrosRecyclerAdapter);

        FirebaseDatabase ref = FirebaseDatabase.getInstance();
        ref.getReference().child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Livro livro = postSnapshot.getValue(Livro.class);
                    if(!livros.contains(livro)){
                        livro.setDownloaded(isBookDownloaded(livro));
                        livros.add(livro);
                        updateRecycler();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
        return root;
    }
    public void updateRecycler () {
        this.livrosRecyclerAdapter.setLivros(livros);
        this.livrosRecyclerAdapter.notifyDataSetChanged();
    }

    public boolean isBookDownloaded(Livro livro){
        File file = getBookFile(livro);
        return file.exists();
    }
    public boolean downloadBook(final Livro livro){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(livro.getUrlFile());

        File localFile = getBookFile(livro);
        booksPath.mkdirs();

        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Livro baixado com sucesso!", Toast.LENGTH_SHORT).show();
                setDownloaded(livro);
                updateRecycler();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getContext(), "Erro ao baixar livro.", Toast.LENGTH_SHORT).show();

            }
        });
        return false;
    }

    public void setDownloaded(Livro livro){
        for(Livro l : livros){
            if(l.getUrlFile().equals(livro.getUrlFile())){
                livros.get(livros.indexOf(l)).setDownloaded(true);
            }
        }
    }

    public File getBookFile(Livro livro){
        return new File(booksPath, livro.getTitulo().replace(" ","") + ".pdf");
    }

    public Bitmap renderThumbnail(Livro livro) {
        // Render the page and save it to an image file
        try {
            // Load in an already created PDF
            PDDocument document = PDDocument.load(getBookFile(livro));
            // Create a renderer for the document
            PDFRenderer renderer = new PDFRenderer(document);
            // Render the image to an RGB Bitmap
            return renderer.renderImage(0, 1, Bitmap.Config.RGB_565);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
