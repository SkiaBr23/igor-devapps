package br.unb.igor.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuInflater;
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
import br.unb.igor.model.Aventura;

public class ActivityHome extends AppCompatActivity implements FragmentCriarAventura.OnFragmentInteractionListener, FragmentHome.OnFragmentInteractionListener{

    private FragmentHome fragmentHome;

    private ImageView imgHamburguer;
    private ImageView imgOptionsMenu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerOptions;
    private FirebaseAuth mAuth;
    private List<Aventura> aventuras;

    private enum Screen {
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

            int colorFocus = ResourcesCompat.getColor(getResources(), R.color.drawerFocused, null);
            int colorUnfocus = ResourcesCompat.getColor(getResources(), R.color.drawerUnfocused, null);

            if (mCurrentScreen.ordinal() == i) {
                System.out.println(mCurrentScreen.ordinal());
                textView.setTextColor(colorUnfocus);
                imgBar.setVisibility(View.VISIBLE);
                imgBar.setColorFilter(colorUnfocus);
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
                textView.setTextColor(colorFocus);
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
            drawerAdapter.notifyDataSetChanged();
            mDrawerLayout.closeDrawers();
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
                popup.show();
            }
        });

        fragmentHome = new FragmentHome();

        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentHome)
                .commit();
        }

        this.aventuras = new ArrayList<>();
    }

    public void setAventuras (List<Aventura> aventuras) {
        this.aventuras = aventuras;
    }

    public List<Aventura> getAventuras () {
        if (this.aventuras == null) {
            this.aventuras = new ArrayList<>();
        }
        return this.aventuras;
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
        super.onBackPressed();
        View view = this.getCurrentFocus();
        //Fecha o keyboard, durante a criação de aventura, caso o usuario clique sobre o icone de close
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCriacaoAventura(String tituloAventura) {
        Aventura aventura = new Aventura(tituloAventura, "09/05", "");
        getAventuras().add(aventura);
        FragmentHome fragmentH = new FragmentHome();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentH)
                .commit();
    }

    @Override
    public void onAventuraDelete(Aventura aventura) {
        getAventuras().remove(aventura);
        FragmentHome fragmentH = new FragmentHome();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentH)
                .commit();
    }
}
