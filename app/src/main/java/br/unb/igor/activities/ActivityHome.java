package br.unb.igor.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
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

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentAdicionarJogador;
import br.unb.igor.fragments.FragmentConvites;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentCriarSessao;
import br.unb.igor.fragments.FragmentAdventure;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.ChildEventListenerAdapter;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;

public class ActivityHome extends AppCompatActivity implements
        AdventureListener, GoogleApiClient.OnConnectionFailedListener,
        PopupMenu.OnMenuItemClickListener {

    private static final String TAG = ActivityHome.class.getName();
    private static final String BUNDLE_ADVENTURES = "KEY_ADVENTURES";
    private static final String BUNDLE_SELECTED_ADVENTURE = "KEY_SELECTED_ADVENTURE";
    private static final String BUNDLE_CURRENT_USER = "KEY_CURRENT_USER";

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
    private GoogleApiClient mGoogleApiClient;
    private DB db;

    private Aventura selectedAdventure = null;
    private User currentUser = null;
    private ArrayList<Aventura> adventures;

    private ChildEventListener AdventureChangeFeedListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            if (selectedAdventure != null) {
                String aid = dataSnapshot.getKey();
                if (!aid.equals(selectedAdventure.getKey())) {
                    return;
                }
                Aventura adventureChanged = dataSnapshot.getValue(Aventura.class);
                if (adventureChanged != null) {
                    getScreenFragment(Screen.Home);
                    fragmentAdventure.onAdventureChange(adventureChanged, mCurrentScreen == Screen.Adventure);
                    getScreenFragment(Screen.AddPlayer);
                    fragmentAdicionarJogador.onAdventureChange(adventureChanged);
                    selectedAdventure.assignInternal(adventureChanged);
                }
            }
        }
    };

    private ChildEventListener InviteChangeFeedListener = new ChildEventListenerAdapter() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {
            Convite convite = dataSnapshot.getValue(Convite.class);
            System.out.println("@@ Convite add");
            if (convite != null) {
                currentUser.setInvitation(convite, true);
                getScreenFragment(Screen.Home);
                fragmentHome.checkInvites();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousKey) {
            Convite convite = dataSnapshot.getValue(Convite.class);
            if (convite != null) {
                System.out.println("@@ Convite change");
                String aid = convite.getKeyAventura();
                List<Convite> convites = currentUser.getConvites();
                for (int index = convites.size() - 1; index >= 0; index--) {
                    if (convites.get(index).getKeyAventura().equals(aid)) {
                        convites.set(index, convite);
                        getScreenFragment(Screen.Home);
                        fragmentHome.checkInvites();
                        break;
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Convite convite = dataSnapshot.getValue(Convite.class);
            if (convite != null) {
                System.out.println("@@ Convite remove");
                currentUser.setInvitation(convite, false);
                getScreenFragment(Screen.Home);
                fragmentHome.checkInvites();
            }
        }
    };

    public enum Screen {
        Home,
        Books,
        Account,
        Invites,
        Settings,
        Exit,
        Adventure,
        CreateSession,
        AddPlayer,
        CreateAdventure
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
        currentUser = getIntent().getParcelableExtra(User.PARCEL_KEY_USER);

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
                mDrawerLayout.openDrawer(Gravity.START);
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

        if (mAuth.getCurrentUser() == null || currentUser == null) {
            signOut();
            return;
        }

        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        db = new DB(mAuth, mDatabase);

        if (this.adventures == null) {
            if (savedInstanceState == null) {
                this.adventures = new ArrayList<>();
                fetchInitialAdventures();
            } else {
                this.adventures = savedInstanceState.getParcelableArrayList(BUNDLE_ADVENTURES);
                fragmentHome.setIsLoading(false);
            }
        }

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

        Parcelable selectedAdventure;
        Parcelable currentUser;
        if (savedInstanceState != null) {
            selectedAdventure = savedInstanceState.getParcelable(BUNDLE_SELECTED_ADVENTURE);
            currentUser = savedInstanceState.getParcelable(BUNDLE_CURRENT_USER);
            if (selectedAdventure != null) {
                setSelectedAdventure((Aventura) selectedAdventure);
            } else {
                setSelectedAdventure(null);
            }
            if (currentUser != null) {
                this.currentUser = (User) currentUser;
            }
        }
    }

    private void updateThreeDotsMenu() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (currentFragment == null ||
            currentFragment instanceof FragmentHome ||
                (currentFragment instanceof FragmentAdventure &&
                ((FragmentAdventure)currentFragment).isCurrentUserMaster())) {
            imgOptionsMenu.setVisibility(View.VISIBLE);
        } else {
            imgOptionsMenu.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCurrentScreen = getFragmentScreen(getSupportFragmentManager().findFragmentById(R.id.content_frame));
        updateThreeDotsMenu();
        mDrawerAdapter.notifyDataSetChanged();

        mDatabase
            .child("users")
            .child(currentUser.getUserId())
            .child("convites")
            .addChildEventListener(InviteChangeFeedListener);

        mDatabase
            .child("adventures")
            .addChildEventListener(AdventureChangeFeedListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        mDatabase
            .child("users")
            .child(currentUser.getUserId())
            .child("convites")
            .removeEventListener(InviteChangeFeedListener);

        mDatabase
            .child("adventures")
            .removeEventListener(AdventureChangeFeedListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_SELECTED_ADVENTURE, selectedAdventure);
        outState.putParcelableArrayList(BUNDLE_ADVENTURES, adventures);
        outState.putParcelable(BUNDLE_CURRENT_USER, currentUser);
    }

    public void setSelectedAdventure(Aventura aventura) {
        if (aventura == null) {
            selectedAdventure = null;
            if (fragmentAdventure != null) {
                fragmentAdventure.onAdventureChange(null, false);
            }
        } else {
            selectedAdventure = aventura;
        }
    }

    private void fetchInitialAdventures() {
        fragmentHome.setIsLoading(true);
        List<String> adventureKeys = currentUser.getAventuras();
        final OnCompleteHandler handler = new OnCompleteHandler(adventureKeys.size(), new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                fragmentHome.setIsLoading(false);
            }
        });
        int index = 0;
        for (String adventureKey : adventureKeys) {
            final int adventureIndex = index++;
            db.getAdventureById(adventureKey, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null && extra instanceof Aventura) {
                        if (adventures.size() <= adventureIndex) {
                            adventures.add((Aventura) extra);
                            fragmentHome.getRecyclerAdapter().notifyItemInserted(adventures.size() - 1);
                        } else {
                            adventures.add(adventureIndex, (Aventura) extra);
                            fragmentHome.getRecyclerAdapter().notifyItemInserted(adventureIndex);
                        }
                    }
                    handler.advance();
                }
            }));
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
                Collections.sort(adventures, new Comparator<Aventura>() {
                    @Override
                    public int compare(Aventura left, Aventura right) {
                        return left.getTitulo().compareToIgnoreCase(right.getTitulo());
                    }
                });
                fragmentHome.notifyItemChangedVisible();
                List<String> reorderedIds = new ArrayList<>();
                for (Aventura a : adventures) {
                    reorderedIds.add(a.getKey());
                }
                currentUser.setAventuras(reorderedIds);
                db.upsertUser(currentUser);
                break;
            default:
                break;
        }
        return false;
    }

    public Aventura getSelectedAdventure() {
        return selectedAdventure;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Aventura> getAdventures() {
        return adventures;
    }

    public void showHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackCount = fragmentManager.getBackStackEntryCount();
        while (backStackCount-- > 0) {
            fragmentManager.popBackStack();
        }
        mCurrentScreen = Screen.Home;
        setSelectedAdventure(null);
    }

    public void markInvitationsAsSeen() {
        for (Convite c : currentUser.getConvites()) {
            c.setUnseen(false);
        }
        db.upsertUser(currentUser);
        getScreenFragment(Screen.Home);
        fragmentHome.checkInvites();
    }

    public void pushFragment(Fragment f, String tag, Screen newScreen) {
        boolean drawerChanged = mCurrentScreen != newScreen;
        if (f instanceof FragmentHome) {
            showHomeFragment();
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
                if (fragmentHome != null && fragmentHome.getRetainInstance()) {
                    fragment = fragmentHome;
                } else {
                    fragment = getFragmentByClass(FragmentHome.class);
                    fragmentHome = (FragmentHome) fragment;
                }
                break;
            case Adventure:
                if (fragmentAdventure != null && fragmentAdventure.getRetainInstance()) {
                    fragment = fragmentAdventure;
                } else {
                    fragment = getFragmentByClass(FragmentAdventure.class);
                    fragmentAdventure = (FragmentAdventure)fragment;
                }
                break;
            case CreateSession:
                if (fragmentCriarSessao != null && fragmentCriarSessao.getRetainInstance()) {
                    fragment = fragmentCriarSessao;
                } else {
                    fragment = getFragmentByClass(FragmentCriarSessao.class);
                    fragmentCriarSessao = (FragmentCriarSessao) fragment;
                }
                break;
            case AddPlayer:
                if (fragmentAdicionarJogador != null && fragmentAdicionarJogador.getRetainInstance()) {
                    fragment = fragmentAdicionarJogador;
                } else {
                    fragment = getFragmentByClass(FragmentAdicionarJogador.class);
                    fragmentAdicionarJogador = (FragmentAdicionarJogador)fragment;
                }
                break;
            case Invites:
                if (fragmentConvites != null && fragmentConvites.getRetainInstance()) {
                    fragment = fragmentConvites;
                } else {
                    fragment = getFragmentByClass(FragmentConvites.class);
                    fragmentConvites = (FragmentConvites) fragment;
                }
                break;
            case CreateAdventure:
                if (fragmentCreateAdventure != null && fragmentCreateAdventure.getRetainInstance()) {
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
        Aventura aventura = new Aventura(title, currentUser.getUserId());
        db.createAdventure(aventura);
        currentUser.addAdventure(aventura);
        adventures.add(aventura);
        db.upsertUser(currentUser);
        int index = currentUser.getAventuras().size() - 1;
        fragmentHome.getRecyclerAdapter().notifyItemInserted(index);
        fragmentHome.scrollToIndex(index);
        showHomeFragment();
    }

    @Override
    public void onSelectAdventure(Aventura aventura, int index) {
        if (fragmentHome.isInEditMode()) {
            return;
        }
        getScreenFragment(Screen.Adventure);
        setSelectedAdventure(aventura);
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
        builder.setMessage(R.string.msg_do_you_wish_to_delete_this_adventure).setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Aventura adventure = adventures.get(removeIndex);
                currentUser.getAventuras().remove(removeIndex);
                adventures.remove(removeIndex);
                db.removeAdventure(adventure);
                db.upsertUser(currentUser);
                fragmentHome.getRecyclerAdapter().notifyItemRemoved(removeIndex);
            }
        }).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
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
        }
        FirebaseAuth.getInstance().signOut();
        Intent logout = new Intent(ActivityHome.this, ActivityLogin.class);
        startActivity(logout);
        finish();
    }
    // [END signOut]


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClickAddSessionFAB() {
        getScreenFragment(Screen.CreateSession);
        pushFragment(fragmentCriarSessao, FragmentCriarSessao.TAG, Screen.CreateSession);
    }

    @Override
    public void onSelectSessao(Sessao sessao, int index) {

    }

    @Override
    public void onClickInviteUsersFAB() {
        getScreenFragment(Screen.AddPlayer);
        pushFragment(fragmentAdicionarJogador, FragmentAdicionarJogador.TAG, Screen.AddPlayer);
    }

    @Override
    public void onViewUserInfo(User user) {
    }

    @Override
    public void onConfirmarSessao(String tituloSessao, String dataSessao) {
        if (selectedAdventure != null) {
            Sessao sessaoSaida = new Sessao(selectedAdventure.getKey(), tituloSessao, dataSessao);
            selectedAdventure.addSessao(sessaoSaida);
            db.upsertAdventure(selectedAdventure);
            getScreenFragment(Screen.Home);
            fragmentHome.notifyItemChangedVisible();
            onBackPressed();
        }
    }

    @Override
    public void onUserInvitation(User user, boolean hasBeenInvited) {
        if (selectedAdventure != null) {
            db.setUserInvitation(user, currentUser.getUserId(), selectedAdventure, hasBeenInvited);
        }
    }

    @Override
    public void onUserKickedOut(User user) {
        if (selectedAdventure != null) {
            selectedAdventure.kickUser(user.getUserId());
            db.upsertAdventure(selectedAdventure);
        }
    }
}
