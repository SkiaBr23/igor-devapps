package br.unb.igor.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentAccount;
import br.unb.igor.fragments.FragmentAdicionarJogador;
import br.unb.igor.fragments.FragmentBooks;
import br.unb.igor.fragments.FragmentConvites;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentCriarSessao;
import br.unb.igor.fragments.FragmentAdventure;
import br.unb.igor.fragments.FragmentDiceRoller;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.helpers.AdventureListener;
import br.unb.igor.helpers.ChildEventListenerAdapter;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Jogada;
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
    private FragmentAccount fragmentAccount;
    private FragmentBooks fragmentBooks;
    private FragmentDiceRoller fragmentDiceRoll;

    private ImageView imgHamburguer;
    private ImageView imgOptionsMenu;
    private DrawerLayout mDrawerLayout;
    private DrawerListAdapter mDrawerAdapter;
    private ListView mDrawerOptions;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private Aventura selectedAdventure = null;
    private User currentUser = null;
    private ArrayList<Aventura> adventures;

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    private boolean drawerNeedsUpdate = false;

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
        DiceRoll,
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
                view = getLayoutInflater().inflate(R.layout.list_item_drawer_option, container, false);
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

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            signOut();
            return;
        }

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
                if (drawerNeedsUpdate) {
                    mDrawerAdapter.notifyDataSetChanged();
                    drawerNeedsUpdate = false;
                }
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
            this.adventures = savedInstanceState.getParcelableArrayList(BUNDLE_ADVENTURES);
            getScreenFragment(Screen.Home);
            fragmentHome.setIsLoading(false);
        }

        if (this.currentUser == null) {
            this.currentUser = new User(mAuth.getCurrentUser());
            fetchCurrentUser();
        }

        if (this.adventures == null) {
            this.adventures = new ArrayList<>();
        }

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
        drawerNeedsUpdate = false;

        DB.ref
            .child("users")
            .child(currentUser.getUserId())
            .child("convites")
            .addChildEventListener(InviteChangeFeedListener);

        DB.ref
            .child("adventures")
            .addChildEventListener(AdventureChangeFeedListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        DB.ref
            .child("users")
            .child(currentUser.getUserId())
            .child("convites")
            .removeEventListener(InviteChangeFeedListener);

        DB.ref
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

    private void fetchCurrentUser() {
        if (currentUser.hasBeenFetchedFromDB) {
            return;
        }
        DB.getUserInfoById(currentUser.getUserId(), new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                if (extra != null && extra instanceof User) {
                    currentUser.assignInternal((User)extra);
                    currentUser.hasBeenFetchedFromDB = true;
                    fetchInitialAdventures();
                    Picasso
                        .with(ActivityHome.this)
                        .load(currentUser.getProfilePictureUrl())
                        .fetch();
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment currentFrag = fm.findFragmentById(R.id.content_frame);
                    if (currentFrag instanceof FragmentAccount) {
                        ((FragmentAccount)currentFrag).onUserChanged();
                    }
                }
            }
        }));
    }

    private void fetchInitialAdventures() {
        final List<String> adventureKeys = currentUser.getAventuras();
        if (adventureKeys.isEmpty()) {
            fragmentHome.setIsLoading(false);
            return;
        }
        fragmentHome.setIsLoading(true);
        final List<String> invalidAdventureIDs = new ArrayList<>();
        final OnCompleteHandler handler = new OnCompleteHandler(adventureKeys.size(), new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                fragmentHome.setIsLoading(false);
                currentUser.removeAdventures(invalidAdventureIDs);
                DB.upsertUser(currentUser);
            }
        });
        int index = 0;
        for (String adventureKey : adventureKeys) {
            final int adventureIndex = index++;
            DB.getAdventureById(adventureKey, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
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
                    } else {
                        invalidAdventureIDs.add(adventureKeys.get(adventureIndex));
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
                    getScreenFragment(Screen.Adventure);
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
                DB.upsertUser(currentUser);
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
        DB.upsertUser(currentUser);
        getScreenFragment(Screen.Home);
        fragmentHome.checkInvites();
    }

    public void pushFragment(Fragment f, String tag, Screen newScreen) {
        boolean drawerChanged = mCurrentScreen != newScreen;
        if (f instanceof FragmentHome) {
            showHomeFragment();
        } else {
            // Screens from the Drawer will force the back button
            // to return to home
            for (Screen s : drawerScreens) {
                if (s.equals(newScreen)) {
                    showHomeFragment();
                    break;
                }
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (f instanceof FragmentAdventure) {
                ft.setCustomAnimations(
                    R.anim.slide_in_right_320ms,
                    R.anim.slide_out_left_320ms,
                    R.anim.slide_in_left_320ms,
                    R.anim.slide_out_right_320ms
                );
            } else {
                ft.setCustomAnimations(
                    R.animator.fade_opaque_320ms,
                    R.animator.fade_out_320ms,
                    R.animator.fade_opaque_320ms,
                    R.animator.fade_out_320ms
                );
            }
            ft.replace(R.id.content_frame, f, tag)
                .addToBackStack(tag)
                .commit();
            mCurrentScreen = newScreen;
        }
        if (drawerChanged) {
            drawerNeedsUpdate = true;
        }
        hideKeyboard();
    }

    public void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
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
                if (fragmentAccount != null && fragmentAccount.getRetainInstance()) {
                    fragment = fragmentAccount;
                } else {
                    fragment = getFragmentByClass(FragmentAccount.class);
                    fragmentAccount = (FragmentAccount) fragment;
                }
                break;
            case DiceRoll:
                if (fragmentDiceRoll != null && fragmentDiceRoll.getRetainInstance()) {
                    fragment = fragmentDiceRoll;
                } else {
                    fragment = getFragmentByClass(FragmentDiceRoller.class);
                    fragmentDiceRoll = (FragmentDiceRoller) fragment;
                }
                break;
            case Books:
                if (fragmentBooks != null && fragmentBooks.getRetainInstance()) {
                    fragment = fragmentBooks;
                } else {
                    fragment = getFragmentByClass(FragmentBooks.class);
                    fragmentBooks = (FragmentBooks) fragment;
                }
                break;
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
        } else if (f instanceof FragmentAccount) {
            return Screen.Account;
        } else if (f instanceof FragmentDiceRoller) {
            return Screen.DiceRoll;
        } else if (f instanceof FragmentBooks) {
            return Screen.Books;
        }
        return Screen.Exit;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
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
        if (fragmentAccount != null && fragmentAccount == currentFrag && fragmentAccount.isEditingUsername()) {
            fragmentAccount.toggleUsernameEdit(false);
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
        drawerNeedsUpdate = true;
        hideKeyboard();
    }

    @Override
    public void onCreateAdventure(String title) {
        Aventura aventura = new Aventura(title, currentUser.getUserId());
        DB.createAdventure(aventura);
        currentUser.addAdventure(aventura);
        adventures.add(aventura);
        DB.upsertUser(currentUser);
        int index = currentUser.getAventuras().size() - 1;
        fragmentHome.getRecyclerAdapter().notifyItemInserted(index);
        fragmentHome.scrollToIndex(index, 1000);
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
                DB.removeAdventure(adventure);
                DB.upsertUser(currentUser);
                getScreenFragment(Screen.Home);
                fragmentHome.getRecyclerAdapter().notifyItemRemoved(removeIndex);
                fragmentHome.updateView();
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
            DB.upsertAdventure(selectedAdventure);
            getScreenFragment(Screen.Home);
            fragmentHome.notifyItemChangedVisible();
            onBackPressed();
        }
    }

    @Override
    public void onUserInvitation(User user, boolean hasBeenInvited) {
        if (selectedAdventure != null) {
            DB.setUserInvitation(user, currentUser, selectedAdventure, hasBeenInvited);
        }
    }

    @Override
    public void onUserKickedOut(User user) {
        if (selectedAdventure != null) {
            selectedAdventure.kickUser(user.getUserId());
            DB.upsertAdventure(selectedAdventure);
            user.removeAventura(selectedAdventure.getKey());
            DB.upsertUser(user);
        }
    }

    @Override
    public void onCreateRoll(Jogada jogada) {
        DB.createRoll(jogada);
    }

    @Override
    public void onClickConfirmarConvite(Convite convite, int index) {
        if (currentUser != null){
            DB.getAdventureById(convite.getKeyAventura(), new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null && extra instanceof Aventura) {
                        currentUser.hasBeenFetchedFromDB = true;
                        ((Aventura) extra).acceptUser(currentUser.getUserId());
                        adventures.add((Aventura) extra);
                        DB.upsertAdventure((Aventura) extra);
                        currentUser.addAdventure((Aventura) extra);
                        currentUser.removeConvite(((Aventura) extra).getKey());
                        DB.upsertUser(currentUser);
                    }
                }
            }));
            getScreenFragment(Screen.Invites);
            fragmentConvites.notifyInviteRemoved(index);
            getScreenFragment(Screen.Home);
            fragmentHome.getRecyclerAdapter().notifyDataSetChanged();
            fragmentHome.updateView();
        }
    }

    @Override
    public void onClickCancelarConvite(Convite convite, int index) {
        if (currentUser != null){
            DB.getAdventureById(convite.getKeyAventura(), new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    if (extra != null && extra instanceof Aventura) {
                        currentUser.hasBeenFetchedFromDB = true;
                        ((Aventura) extra).removeInvitedUser(currentUser.getUserId());
                        DB.upsertAdventure((Aventura) extra);
                        currentUser.removeConvite(((Aventura) extra).getKey());
                        DB.upsertUser(currentUser);
                    }
                }
            }));
            getScreenFragment(Screen.Invites);
            fragmentConvites.notifyInviteRemoved(index);
        }
    }

    @Override
    public void rolagemDados() {
        getScreenFragment(Screen.DiceRoll);
        pushFragment(fragmentDiceRoll, FragmentDiceRoller.TAG, Screen.DiceRoll);
    }

    @Override
    public void onInvitesView() {
        getScreenFragment(Screen.Invites);
        pushFragment(fragmentConvites, FragmentConvites.TAG, Screen.Invites);
    }
}
