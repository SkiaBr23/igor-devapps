package br.unb.igor.activities;

import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.listeners.AdventureListener;
import br.unb.igor.model.Aventura;

public class ActivityHome extends AppCompatActivity implements
        AdventureListener,
        PopupMenu.OnMenuItemClickListener {

    private FragmentHome fragmentHome;

    private ImageView imgHamburguer;
    private ImageView imgOptionsMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerOptions;
    private FirebaseAuth mAuth;
    private List<Aventura> aventuras;

    public enum Screen {
        Adventures,
        Books,
        Account,
        Notifications,
        Settings,
        Exit
    };

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

            textView.setText(options[i]);

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
                System.out.println(mCurrentScreen.ordinal());
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
                    mAuth.signOut();
                    Intent loginIntent = new Intent(ActivityHome.this,LoginActivity.class);
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
                Fragment createAdventureFragment = fragmentManager.findFragmentById(R.id.content_frame);
                if (createAdventureFragment != null && createAdventureFragment instanceof FragmentCriarAventura) {
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
        }

        aventuras = new ArrayList<>();

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
        return aventuras;
    }

    public void showHomeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentHome)
                .commit();
    }

    protected Fragment getScreenFragment (Screen screen) {
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
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCreateAdventure(String title) {
        Aventura aventura = new Aventura(title, "09/05", "");
        aventuras.add(aventura);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragmentHome)
            .addToBackStack(FragmentHome.TAG)
            .commit();
    }

    @Override
    public void onSelectAdventure(Aventura aventura, int index) {

    }

    @Override
    public void onRemoveAdventure(Aventura aventura, int index) {
        Vibrator v = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(100);
        final AlertDialog alerta;
        final int removeIndex = index;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deseja remover esta aventura?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                aventuras.remove(removeIndex);
                fragmentHome.getRecyclerAdapter().notifyItemRemoved(removeIndex);
                showHomeFragment();
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showHomeFragment();
            }
        });
        alerta = builder.create();
        alerta.show();
    }
}
