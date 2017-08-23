package br.unb.igor.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentCriarAventura;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.fragments.FragmentLogin;

public class ActivityHome extends AppCompatActivity implements FragmentHome.OnFragmentInteractionListener {

    private FragmentHome fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fragmentHome = new FragmentHome();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,fragmentHome).addToBackStack("homeFragment").commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager= getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                finish();
            } else {
                fragmentManager.popBackStack();
            }
        }
    }

    @Override
    public void criarAventura() {
        FragmentCriarAventura fragmentCriarAventura = new FragmentCriarAventura();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragmentCriarAventura).addToBackStack("criarAventuraFragment").commit();
    }
}
