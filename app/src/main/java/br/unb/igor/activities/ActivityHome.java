package br.unb.igor.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentCriarSessao;
import br.unb.igor.fragments.FragmentEditarAventura;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.helpers.SessionListener;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;

public class ActivityHome extends AppCompatActivity implements
        AdventureListener, GoogleApiClient.OnConnectionFailedListener,
        PopupMenu.OnMenuItemClickListener, AdventureEditListener, SessionListener {

    private static final String TAG = ActivityHome.class.getName();

    private FragmentHome fragmentHome;
    private FragmentEditarAventura fragmentEditarAventura;
    private ImageView imgHamburguer;
    private ImageView imgOptionsMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerOptions;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private List<Aventura> aventuras;
    private GoogleApiClient mGoogleApiClient;

    public enum Screen {
        Adventures,
        Books,
        Account,
        Notifications,
        Settings,
        Exit
    }

    ;

    private Screen mCurrentScreen = Screen.Adventures;

    private class DrawerListAdapter extends BaseAdapter {

        private final String[] options = {
                getString(R.string.menu_adventure),
                getString(R.string.menu_books),
                getString(R.string.menu_account),
                getString(R.string.menu_notifications),
                getString(R.string.menu_settings),
                getString(R.string.menu_exit)
        };

        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int i) {
            return options[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup container) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.drawer_list_item, container, false);
            }

            TextView textView = view.findViewById(R.id.text);
            ImageView imgBar = view.findViewById(R.id.bar);
            ImageView imgIcon = view.findViewById(R.id.icon);

            // Set Fira Sans (Regular) font
            Typeface firaSans = Typeface.createFromAsset(getAssets(), "FiraSans-Regular.ttf");

            textView.setText(options[i]);
            textView.setTypeface(firaSans);

            if (i == 0) {
                imgIcon.setBackgroundResource(R.drawable.aventuras_icone);
            } else if (i == 1) {
                imgIcon.setBackgroundResource(R.drawable.livros_icone);
            } else if (i == 2) {
                imgIcon.setBackgroundResource(R.drawable.conta_icone);
            } else if (i == 3) {
                imgIcon.setBackgroundResource(R.drawable.notificacoes_icone);
            } else if (i == 4) {
                imgIcon.setBackgroundResource(R.drawable.configuracoes_icone);
            } else if (i == 5) {
                imgIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_menu));
            }

            int colorNotFocused = ResourcesCompat.getColor(getResources(), R.color.drawerNotFocused, null);
            int colorFocused = ResourcesCompat.getColor(getResources(), R.color.drawerFocused, null);

            if (mCurrentScreen.ordinal() == i) {
                textView.setTextColor(colorFocused);
                imgBar.setVisibility(View.VISIBLE);
                imgBar.setColorFilter(colorFocused);
                if (i == 0) {
                    imgIcon.setBackgroundResource(R.drawable.aventuras_icone_selecionado);
                } else if (i == 1) {
                    imgIcon.setBackgroundResource(R.drawable.livros_icone_selecionado);
                } else if (i == 2) {
                    imgIcon.setBackgroundResource(R.drawable.conta_icone_selecionado);
                } else if (i == 3) {
                    imgIcon.setBackgroundResource(R.drawable.notificacoes_icone_selecionado);
                } else if (i == 4) {
                    imgIcon.setBackgroundResource(R.drawable.configuracoes_icone_selecionado);
                } else if (i == 5) {
                    signOut();
                    Intent loginIntent = new Intent(ActivityHome.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            } else {
                textView.setTextColor(colorNotFocused);
                imgBar.setVisibility(View.INVISIBLE);
            }

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final DrawerListAdapter drawerAdapter = new DrawerListAdapter();
        mAuth = FirebaseAuth.getInstance();
        this.aventuras = new ArrayList<Aventura>();

        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerOptions = findViewById(R.id.drawer_options);
        mDrawerOptions.setAdapter(drawerAdapter);
        mDrawerOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentScreen = Screen.values()[i];
                mDrawerLayout.closeDrawers();
                drawerAdapter.notifyDataSetChanged();
            }
        });

        imgHamburguer = findViewById(R.id.btnMenuLateral);
        imgHamburguer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        imgOptionsMenu = findViewById(R.id.btnMenu);
        imgOptionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ActivityHome.this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_main, popup.getMenu());
                popup.setOnMenuItemClickListener(ActivityHome.this);
                popup.show();
            }
        });

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            // Esconde os tres pontinhos na tela de criacao de aventura
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
                if (currentFragment != null && currentFragment instanceof FragmentCriarAventura) {
                    imgOptionsMenu.setVisibility(View.INVISIBLE);
                } else {
                    imgOptionsMenu.setVisibility(View.VISIBLE);
                }
            }
        });

        fragmentHome = new FragmentHome();

        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragmentHome)
                    .commit();
        } else{
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_frame, fragmentHome)
                    .commit();
        }

        // Busca de aventuras no FirebaseDatabase

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // TODO: Usuário não credenciado na Home! Tratar.
        }

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        /*
            BUSCANDO INFORMACOES DE AVENTURAS DO USUARIO NO FIREBASE DB
        */

        final String userId = mAuth.getCurrentUser().getUid();

        fetchInitialAdventures();

//        mDatabase.child("users").child(userId).child("adventures").addChildEventListener(
//                new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
//                        String adventureKey = dataSnapshot.getKey();
//                        Set<String> keys = new HashSet<>();
//                        keys.add(adventureKey);
//                        fetchAdventures(keys);
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {
//                        String adventureKey = dataSnapshot.getKey();
//                        Set<String> keys = new HashSet<>();
//                        keys.add(adventureKey);
//                        fetchAdventures(keys);
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//                        String adventureKey = dataSnapshot.getKey();
//                        Set<String> keys = new HashSet<>();
//                        keys.add(adventureKey);
//                        fetchAdventures(keys);
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String previousKey) {
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "db/users/adventures#addChildEventListener:onCancelled", databaseError.toException());
//                    }
//                });

        if (mAuth.getCurrentUser().getProviders().contains("google.com")) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    private void fetchInitialAdventures() {
        final String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("adventures").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                        };
                        HashMap<String, Object> idAventuras = dataSnapshot.getValue(genericTypeIndicator);
                        // Check if adventure id list exists
                        if (idAventuras != null) {
                            // Query adventures on database
                            fetchInitialAdventures(idAventuras.keySet());
                        } else {
                            fragmentHome.setLoadingComplete();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getAdventuresIds:onCancelled", databaseError.toException());
                    }
                });

        // fragmentHome.getRecyclerAdapter().notifyDataSetChanged();
        // showHomeFragment();

    }

    private void fetchInitialAdventures(Set<String> idAventuras) {
        final OnCompleteHandler onCompleteHandler = new OnCompleteHandler(1, new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra) {
                fragmentHome.setLoadingComplete();
            }
        });
        if (idAventuras.isEmpty()) {
            onCompleteHandler.advance();
            return;
        }
        for (String key : idAventuras) {
            mDatabase.child("adventures").child(key).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Aventura aventura = dataSnapshot.getValue(Aventura.class);
                            // Check if adventure is not null and don't exist on current list
                            if (aventura != null) {
                                // Add adventure to list
                                final int index = aventura.getIndexOf(aventuras, aventura.getKey());
                                final boolean exists = dataSnapshot.exists();
                                onCompleteHandler.advance();

                                if (index >= 0) {
                                    if (exists) {
                                        aventuras.set(index, aventura);
                                        fragmentHome.getRecyclerAdapter().notifyItemChanged(index);
                                    } else {
                                        aventuras.remove(index);
                                        fragmentHome.getRecyclerAdapter().notifyItemRemoved(index);
                                    }
                                } else if (exists) {
                                    aventuras.add(aventura);
                                    fragmentHome.getRecyclerAdapter().notifyItemInserted(aventuras.size() - 1);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "fechAdventures:onCancelled", databaseError.toException());
                            onCompleteHandler.cancel();
                        }
                    });
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editar:
                fragmentHome.setEditMode(true);
                break;
            default:
                break;
        }
        return false;
    }

    public List<Aventura> getAdventures() {
        if (this.aventuras == null) {
            this.aventuras = new ArrayList<>();
        }
        return aventuras;
    }

    public void showHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentById(R.id.content_frame) instanceof FragmentHome) {
            return;
        }

        fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragmentHome)
                .addToBackStack(FragmentHome.TAG)
                .commitAllowingStateLoss();
    }

    protected Fragment getScreenFragment(Screen screen) {
        Fragment fragment = null;
        switch (screen) {
            case Adventures:
                if (fragmentHome == null) {
                    fragmentHome = new FragmentHome();
                }
                fragment = fragmentHome;
                break;
            case Account:
            case Books:
            case Notifications:
            case Settings:
            case Exit:
            default:
                break;
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
        View view = this.getCurrentFocus();
        // Fecha o keyboard, durante a criação de aventura, caso o usuario clique sobre o icone de close
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCreateAdventure(String title) {
        String userId = mAuth.getCurrentUser().getUid();
        Aventura aventura = new Aventura(title, "09/05", userId);
        String key = createAdventureFirebase(aventura);
        aventura.setKey(key);
        aventuras.add(aventura);
        fragmentHome.getRecyclerAdapter().notifyItemInserted(aventuras.size() - 1);
        fragmentHome.scrollToIndex(aventuras.size() - 1);
        showHomeFragment();
    }

    @Override
    public void onSelectAdventure(Aventura aventura, int index) {
        fragmentEditarAventura = new FragmentEditarAventura();
        Bundle bundle = new Bundle();
        bundle.putString("tituloAventura", aventura.getTitulo());
        bundle.putString("keyAventura",aventura.getKey());
        fragmentEditarAventura.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentEditarAventura)
                .addToBackStack(FragmentEditarAventura.TAG)
                .commit();
    }

    @Override
    public void onRemoveAdventure(Aventura aventura, int index) {
        Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(100);
        final AlertDialog alerta;
        final int removeIndex = index;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja remover esta aventura?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeAdventureFirebase(aventuras.get(removeIndex));
                aventuras.remove(removeIndex);
                fragmentHome.getRecyclerAdapter().notifyItemRemoved(removeIndex);
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        alerta = builder.create();
        alerta.show();
    }


    private String createAdventureFirebase(final Aventura aventura) {
        final String userId = mAuth.getCurrentUser().getUid();
        String key = mDatabase.child("adventures").push().getKey();
        aventura.setKey(key);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/adventures/" + key, aventura);
        childUpdates.put("/users/" + userId + "/adventures/" + key, true);

        mDatabase.updateChildren(childUpdates);

        return key;
    }

    private void removeAdventureFirebase(final Aventura aventura) {
        final String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("adventures").child(aventura.getKey()).removeValue();
        mDatabase.child("users").child(userId).child("adventures").child(aventura.getKey()).removeValue();
    }

    // [START signOut]
    private void signOut() {
        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().getProviders().contains("google.com")) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }

            if (mAuth.getCurrentUser().getProviders().contains("facebook.com") && AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }

            FirebaseAuth.getInstance().signOut();
        }
    }
    // [END signOut]


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onAdicionarSessao(String keyAventura) {
        FragmentCriarSessao fragmentCriarSessao = new FragmentCriarSessao();
        Bundle bundle = new Bundle();
        bundle.putString("keyAventura",keyAventura);
        fragmentCriarSessao.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentCriarSessao)
                .addToBackStack(FragmentCriarSessao.TAG)
                .commit();
    }

    @Override
    public void onConfirmarSessao(String keyAventura, String tituloSessao, String dataSessao) {
        Aventura aventuraSelecionada = getAventuraViaKey(keyAventura);
        if (aventuraSelecionada != null) {
            Sessao sessaoSaida = new Sessao();
            sessaoSaida.setResumo(tituloSessao);
            sessaoSaida.setData(dataSessao);
            aventuraSelecionada.getSessoes().add(sessaoSaida);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragmentEditarAventura)
                    .addToBackStack(FragmentEditarAventura.TAG)
                    .commit();
        }
    }

    public Aventura getAventuraViaKey (String key) {
        for (Aventura aventuraAux : this.aventuras) {
            if (aventuraAux.getKey().equals(key)) {
                return aventuraAux;
            }
        }
        return null;
    }

}
