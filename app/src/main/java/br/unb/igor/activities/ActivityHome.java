package br.unb.igor.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.unb.igor.R;
import br.unb.igor.fragments.FragmentHome;

public class ActivityHome extends AppCompatActivity {

    private FragmentHome fragmentHome;

    private ImageView imgHamburguer;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerOptions;

    private enum Screen {
        Adventures,
        Books,
        Account,
        Notifications,
        Settings
    };

    private Screen mCurrentScreen = Screen.Adventures;

    private class DrawerListAdapter extends BaseAdapter {

        private final String[] options = {
            "Aventuras",
            "Livros",
            "Conta",
            "Notificações",
            "Configurações"
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

            int colorFocus = ResourcesCompat.getColor(getResources(), R.color.drawerFocused, null);
            int colorUnfocus = ResourcesCompat.getColor(getResources(), R.color.drawerUnfocused, null);

            if (mCurrentScreen.ordinal() == i) {
                textView.setTextColor(colorFocus);
                imgBar.setVisibility(View.VISIBLE);
                imgBar.setColorFilter(colorFocus);
                imgIcon.setColorFilter(colorFocus);
            } else {
                textView.setTextColor(colorUnfocus);
                imgBar.setVisibility(View.INVISIBLE);
                imgIcon.setColorFilter(colorUnfocus);
            }

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final DrawerListAdapter drawerAdapter = new DrawerListAdapter();

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

        fragmentHome = new FragmentHome();

        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentHome)
                .commit();
        }
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
            default:
                break;
        }
        return fragment;
    }

}