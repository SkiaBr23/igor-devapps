package br.unb.igor.activities;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentAdicionarJogador;
import br.unb.igor.fragments.FragmentConvites;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentCriarSessao;
import br.unb.igor.fragments.FragmentAdventure;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.helpers.AdventureEditListener;
import br.unb.igor.helpers.SessionListener;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;

public class ActivityHome extends AppCompatActivity implements
        AdventureListener, GoogleApiClient.OnConnectionFailedListener,
        PopupMenu.OnMenuItemClickListener, AdventureEditListener, SessionListener {

    private static final String TAG = ActivityHome.class.getName();
    private static final String BUNDLE_ADVENTURES = "Home";

    private FragmentHome fragmentHome;
    private FragmentAdventure fragmentAdventure;
    private FragmentCriarSessao fragmentCriarSessao;
    private FragmentAdicionarJogador fragmentAdicionarJogador;
    private FragmentConvites fragmentConvites;
    private FragmentCriarAventura fragmentCreateAdventure;

    private ImageView imgHamburguer;
    private ImageView imgOptionsMenu;
    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mDrawerAdapter;
    private ListView mDrawerOptions;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private List<Aventura> aventuras;
    private GoogleApiClient mGoogleApiClient;
    private DB db;

    public enum Screen {
        Home,
        Books,
        Account,
        Invites,
        Settings,
        Exit,
        Adventure,
        EditAdventure,
        CreateSession,
        AddPlayer,
        CreateAdventure
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_ADVENTURES, (ArrayList<Aventura>)aventuras);
    }

    private Screen mCurrentScreen = Screen.Home;

    private final Screen[] drawerScreens = {
        Screen.Home,
        Screen.Books,
        Screen.Account,
        Screen.Invites,
        Screen.Settings,
        Screen.Exit
    };

    private class DrawerListAdapter extends BaseAdapter {

        private final String[] drawerOptions = {
            getString(R.string.menu_adventure),
            getString(R.string.menu_books),
            getString(R.string.menu_account),
            getString(R.string.menu_notifications),
            getString(R.string.menu_settings),
            getString(R.string.menu_exit)
        };

        @Override
        public int getCount() {
            return drawerOptions.length;
        }

        @Override
        public Object getItem(int i) {
            return drawerOptions[i];
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

            textView.setText(drawerOptions[i]);
            textView.setTypeface(firaSans);

            if (i == 0) {
                imgIcon.setBackgroundResource(R.drawable.aventuras_icone);
            } else if (i == 1) {
                imgIcon.setBackgroundResource(R.drawable.livros_icone);
            } else if (i == 2) {
                imgIcon.setBackgroundResource(R.drawable.conta_icone);
            } else if (i == 3) {
                imgIcon.setBackgroundResource(R.drawable.ic_convite);
            } else if (i == 4) {
                imgIcon.setBackgroundResource(R.drawable.configuracoes_icone);
            } else if (i == 5) {
                imgIcon.setBackgroundResource(R.drawable.ic_close_menu);
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
                    Intent loginIntent = new Intent(ActivityHome.this, ActivityLogin.class);
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

        mDrawerAdapter = new DrawerListAdapter();
        mAuth = FirebaseAuth.getInstance();

        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerOptions = findViewById(R.id.drawer_options);
        mDrawerOptions.setAdapter(mDrawerAdapter);
        mDrawerOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Screen newScreen;
                mDrawerLayout.closeDrawers();
                if (drawerScreens.length > i) {
                    newScreen = drawerScreens[i];
                    if (newScreen == Screen.Exit) {
                        signOut();
                        return;
                    }
                } else {
                    return;
                }
                setScreen(newScreen);
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
                int menuResId = R.menu.menu_main;
                switch (mCurrentScreen) {
                    case Adventure:
                        menuResId = R.menu.menu_edit;
                        break;
                    default:
                        break;
                }
                inflater.inflate(menuResId, popup.getMenu());
                popup.setOnMenuItemClickListener(ActivityHome.this);
                popup.show();
            }
        });

        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentHome = (FragmentHome)getScreenFragment(Screen.Home);

        // If there is a saved instance, then don't replace the fragment
        // so the currently active fragment remains so
        if (savedInstanceState == null) {
            fragmentManager
                .beginTransaction()
                .replace(R.id.content_frame, fragmentHome, FragmentHome.TAG)
                .commit();
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // TODO: Usuário não credenciado na Home! Tratar.
        }

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();

        if (this.aventuras == null) {
            this.aventuras = new ArrayList<>();
            if (savedInstanceState == null) {
                fetchInitialAdventures();
            } else {
                List<Aventura> saved = savedInstanceState.getParcelableArrayList(BUNDLE_ADVENTURES);
                for (Aventura a : saved) {
                    aventuras.add(a);
                }
                fragmentHome.setIsLoading(false);
            }
        }

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

        updateThreeDotsMenu();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            // Esconde os tres pontinhos na tela de criacao de aventura
            @Override
            public void onBackStackChanged() {
                updateThreeDotsMenu();
            }
        });

        db = new DB(mAuth, mDatabase);
    }

    private void updateThreeDotsMenu() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment == null ||
            currentFragment instanceof FragmentHome ||
            (currentFragment instanceof FragmentAdventure && fragmentAdventure.isCurrentUserMaster())) {
            imgOptionsMenu.setVisibility(View.VISIBLE);
        } else {
            imgOptionsMenu.setVisibility(View.INVISIBLE);
        }
    }

    private void fetchInitialAdventures() {
        final String userId = mAuth.getCurrentUser().getUid();
        fragmentHome.setIsLoading(true);
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
                        fragmentHome.setIsLoading(false);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "getAdventuresIds:onCancelled", databaseError.toException());
                }
            });
    }

    private void fetchInitialAdventures(Set<String> idAventuras) {
        final OnCompleteHandler onCompleteHandler = new OnCompleteHandler(1, new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra) {
                fragmentHome.setIsLoading(false);
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
                if (mCurrentScreen == Screen.Home) {
                    fragmentHome.setEditMode(true);
                } else if (mCurrentScreen == Screen.Adventure) {
                    fragmentAdventure.setEditMode(true);
                }
                break;
            case R.id.action_ordenar:
                Collections.sort(aventuras, new Comparator<Aventura>() {
                    @Override
                    public int compare(Aventura left, Aventura right) {
                        return left.getTitulo().compareToIgnoreCase(right.getTitulo());
                    }
                });
                fragmentHome.notifyItemChangedVisible();
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
        int backStackCount = fragmentManager.getBackStackEntryCount();
        while (backStackCount-- > 0) {
            fragmentManager.popBackStack();
        }
    }

    public void pushFragment(Fragment f, String tag, Screen newScreen) {
        boolean drawerChanged = mCurrentScreen != newScreen;
        if (f instanceof FragmentHome) {
            showHomeFragment();
            mCurrentScreen = Screen.Home;
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.animator.fade_opaque_320ms, R.animator.fade_out_320ms, R.animator.fade_opaque_320ms, R.animator.fade_out_320ms)
                    .replace(R.id.content_frame, f, tag)
                    .addToBackStack(tag)
                    .commit();
            mCurrentScreen = newScreen;
        }
        if (drawerChanged) {
            mDrawerAdapter.notifyDataSetChanged();
        }
    }

    public void setScreen(Screen s) {
        if (s == mCurrentScreen) {
            return;
        }
        Fragment newFrag = getScreenFragment(s);
        if (newFrag != null) {
            pushFragment(newFrag, getClassTag(newFrag.getClass()), s);
        }
    }

    protected String getClassTag(Class c) {
        try {
            Field fieldTag = c.getField("TAG");
            String tag = (String)fieldTag.get(null);
            return tag;
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        }
        return null;
    }

    protected Fragment getFragmentByClass (Class c) {
        String tag = getClassTag(c);
        Fragment f = getSupportFragmentManager().findFragmentByTag(tag);
        if (f == null) {
            try {
                f = (Fragment)c.newInstance();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
        return f;
    }

    protected Fragment getScreenFragment(Screen screen) {
        Fragment fragment = null;
        switch (screen) {
            case Home:
                if (fragmentHome != null) {
                    fragment = fragmentHome;
                } else {
                    fragment = getFragmentByClass(FragmentHome.class);
                    fragmentHome = (FragmentHome) fragment;
                }
                break;
            case Adventure:
                if (fragmentAdventure != null) {
                    fragment = fragmentAdventure;
                } else {
                    fragment = getFragmentByClass(FragmentAdventure.class);
                    fragmentAdventure = (FragmentAdventure)fragment;
                }
                break;
            case CreateSession:
                if (fragmentCriarSessao != null) {
                    fragment = fragmentCriarSessao;
                } else {
                    fragment = getFragmentByClass(FragmentCriarSessao.class);
                    fragmentCriarSessao = (FragmentCriarSessao) fragment;
                }
                break;
            case AddPlayer:
                if (fragmentAdicionarJogador != null) {
                    fragment = fragmentAdicionarJogador;
                } else {
                    fragment = getFragmentByClass(FragmentAdicionarJogador.class);
                    fragmentAdicionarJogador = (FragmentAdicionarJogador)fragment;
                }
                break;
            case Invites:
                if (fragmentConvites != null) {
                    fragment = fragmentConvites;
                } else {
                    fragment = getFragmentByClass(FragmentConvites.class);
                    fragmentConvites = (FragmentConvites) fragment;
                }
                break;
            case CreateAdventure:
                if (fragmentCreateAdventure != null) {
                    fragment = fragmentCreateAdventure;
                } else {
                    fragment = getFragmentByClass(FragmentCriarAventura.class);
                    fragmentCreateAdventure = (FragmentCriarAventura) fragment;
                }
                break;
            case Account:
            case Books:
            case Settings:
            case Exit:
            default:
                break;
        }
        return fragment;
    }

    protected Screen getFragmentScreen(Fragment f) {
        if (f instanceof FragmentHome) {
            return Screen.Home;
        } else if (f instanceof FragmentAdventure) {
            return Screen.Adventure;
        } else if (f instanceof FragmentCriarSessao) {
            return Screen.CreateSession;
        } else if (f instanceof FragmentCriarAventura) {
            return Screen.CreateAdventure;
        } else if (f instanceof FragmentConvites) {
            return Screen.Invites;
        } else if (f instanceof FragmentAdicionarJogador) {
            return Screen.AddPlayer;
        }
        return Screen.Exit;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment currentFrag = fm.findFragmentById(R.id.content_frame);
        if (fragmentHome != null && fragmentHome == currentFrag && fragmentHome.isInEditMode()) {
            fragmentHome.setEditMode(false);
            return;
        }
        if (fragmentAdventure != null && fragmentAdventure == currentFrag && fragmentAdventure.isInEditMode()) {
            fragmentAdventure.setEditMode(false);
            return;
        }
        super.onBackPressed();
        int backStackSize = fm.getBackStackEntryCount();
        if (backStackSize >= 1) {
            Fragment next = fm.findFragmentByTag(fm.getBackStackEntryAt(backStackSize - 1).getName());
            mCurrentScreen = getFragmentScreen(next);
        } else {
            mCurrentScreen = Screen.Home;
        }
        mDrawerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateAdventure(String title) {
        String userId = mAuth.getCurrentUser().getUid();
        Aventura aventura = new Aventura(title, "", userId);
        String key = db.createAdventure(aventura);
        aventura.setKey(key);
        aventuras.add(aventura);
        fragmentHome.getRecyclerAdapter().notifyItemInserted(aventuras.size() - 1);
        fragmentHome.scrollToIndex(aventuras.size() - 1);
        showHomeFragment();
    }

    @Override
    public void onSelectAdventure(Aventura aventura, int index) {
        if (fragmentHome.isInEditMode()) {
            return;
        }
        getScreenFragment(Screen.Adventure);
        Bundle bundle = new Bundle();
        bundle.putString(Aventura.KEY_TITLE, aventura.getTitulo());
        bundle.putString(Aventura.KEY_ID, aventura.getKey());
        bundle.putInt(Aventura.KEY_IMAGE, aventura.getImageResource());
        fragmentAdventure.setArguments(bundle);
        pushFragment(fragmentAdventure, FragmentAdventure.TAG, Screen.Adventure);
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
                db.removeAdventure(aventuras.get(removeIndex));
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

    // [START signOut]
    private void signOut() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getProviders() != null) {
            if (mAuth.getCurrentUser().getProviders().contains("google.com")) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            }

            if (mAuth.getCurrentUser().getProviders().contains("facebook.com") && AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }

            FirebaseAuth.getInstance().signOut();
            Intent logout = new Intent(ActivityHome.this, ActivityLogin.class);
            startActivity(logout);
            finish();
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
        getScreenFragment(Screen.CreateSession);
        Bundle bundle = new Bundle();
        bundle.putString(Aventura.KEY_ID, keyAventura);
        fragmentCriarSessao.setArguments(bundle);
        pushFragment(fragmentCriarSessao, FragmentCriarSessao.TAG, Screen.CreateSession);
    }

    @Override
    public void onSelectSessao(Sessao sessao, int index) {

    }

    @Override
    public void onAdicionarJogador(String keyAventura) {
        getScreenFragment(Screen.AddPlayer);
        Bundle bundle = new Bundle();
        bundle.putString(Aventura.KEY_ID, keyAventura);
        fragmentAdicionarJogador.setArguments(bundle);
        pushFragment(fragmentAdicionarJogador, FragmentAdicionarJogador.TAG, Screen.AddPlayer);
    }

    @Override
    public void onAdicionaJogadorPesquisado(int index) {
        Toast.makeText(getApplicationContext(), "Index: " + index,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmarSessao(String keyAventura, String tituloSessao, String dataSessao) {
        Aventura aventuraSelecionada = getAventuraViaKey(keyAventura);
        if (aventuraSelecionada != null) {
            Sessao sessaoSaida = new Sessao(keyAventura, tituloSessao, dataSessao);
            String sessaoKey = db.createSession(keyAventura, sessaoSaida);
            aventuraSelecionada.getSessoes().put(sessaoKey,sessaoSaida);
            Bundle bundle = new Bundle();
            bundle.putString(Aventura.KEY_TITLE, aventuraSelecionada.getTitulo());
            bundle.putString(Aventura.KEY_ID, aventuraSelecionada.getKey());
            bundle.putInt(Aventura.KEY_IMAGE, aventuraSelecionada.getImageResource());
            getScreenFragment(Screen.Adventure);
            fragmentAdventure.setArguments(bundle);
            getSupportFragmentManager().popBackStack();
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
