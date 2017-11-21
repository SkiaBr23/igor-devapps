package br.unb.igor.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shockwave.pdfium.PdfiumCore;
import com.squareup.picasso.Picasso;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.helpers.ConvertUriToFilePath;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.helpers.Utils;
import br.unb.igor.model.Livro;
import br.unb.igor.recycleradapters.LivrosRecyclerAdapter;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBooks extends Fragment {

    public static String TAG = FragmentBooks.class.getName();

    private RecyclerView recyclerViewListaLivros;
    private LivrosRecyclerAdapter livrosRecyclerAdapter;
    private GridLayoutManager gridLayoutManager;
    private FloatingActionButton btnFABaddBooks;
    private List<Livro> livros;
    private Livro livroAdd;
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

            @Override
            public void onClickAbrirLivro(Livro livro, int adapterPosition) {
                readBook(livro);
            }
        });
        this.livros = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerViewListaLivros = root.findViewById(R.id.recyclerViewListaLivros);
        btnFABaddBooks = root.findViewById(R.id.btnFABaddBooks);
        recyclerViewListaLivros.setLayoutManager(gridLayoutManager);
        livrosRecyclerAdapter.setLivros(this.livros);
        recyclerViewListaLivros.setAdapter(livrosRecyclerAdapter);

        btnFABaddBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBookFromDevice();
            }
        });
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

    public void readBook(Livro livro){
        Uri path = Uri.fromFile(getBookFile(livro));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(path, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void getBookFromDevice(){
        Intent intent = new Intent()
                .setType("application/pdf")
                .setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            Uri selectedfile = data.getData();//The uri with the location of the file
            File file = new File(ConvertUriToFilePath.getPathFromURI(getContext(),selectedfile));
            String titulo = file.getName().replace(".pdf","");
            if (containsBook(livros,titulo)) {
                Toast.makeText(getContext(), "Livro já está na estante!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            livroAdd = new Livro();
            livroAdd.setTitulo(titulo);
            livroAdd.setIdAddedBy(mAuth.getCurrentUser().getUid());
            final Bitmap thumbnail = renderThumbnail(file);
            System.out.println("teste");
            DB.uploadBookThumbnail(file, thumbnail, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    thumbnail.recycle();
                    if (cancelled || extra == null || !(extra instanceof Uri)) {
                        Toast toast = Toast.makeText(getActivity(),
                                R.string.msg_failed_to_upload_picture, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                        System.out.println("nao passou thumb");
                    } else {
                        System.out.println("PASSOU thumb");
                        Uri thumbnailUrl = (Uri) extra;
                        livroAdd.setUrlThumbnail(thumbnailUrl.toString());
                    }
                }
            }));


            DB.uploadBookFile(file, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (cancelled || extra == null || !(extra instanceof Uri)) {
                        Toast toast = Toast.makeText(getActivity(),
                                "Erro ao enviar livro!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    } else {
                        Uri fileUrl = (Uri) extra;
                        livroAdd.setUrlFile(fileUrl.toString());
                        if(livroAdd.getUrlThumbnail() != null){
                            DB.pushBook(livroAdd);
                        }
                    }
                }
            }));
        }
    }

    private boolean containsBook(List<Livro> livros, String titulo) {
        for(Livro l : livros){
            if(l.getTitulo().equals(titulo)){
                return true;
            }
        }
        return false;
    }
    private String getRealPathFromURI(Uri contentURI) {
        String filePath;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            filePath = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }


    public void setDownloaded(Livro livro){
        for(Livro l : livros){
            if(l.getUrlFile().equals(livro.getUrlFile())){
                livros.get(livros.indexOf(l)).setDownloaded(true);
            }
        }
    }

    public File getBookFile(Livro livro){
        return new File(booksPath, livro.getTitulo() + ".pdf");
    }


    public Bitmap renderThumbnail(File file) {
        int pageNum = 0;
        ParcelFileDescriptor pfd = new ParcelFileDescriptor(openFile(file));
        PdfiumCore pdfiumCore = new PdfiumCore(getContext());
        try {
            com.shockwave.pdfium.PdfDocument pdfDocument = pdfiumCore.newDocument(pfd);

            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);
            return bitmap;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public ParcelFileDescriptor openFile(File file){
        ParcelFileDescriptor pfd;
        try {
            pfd=ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY);
        }
        catch (  FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return pfd;
    }
}
