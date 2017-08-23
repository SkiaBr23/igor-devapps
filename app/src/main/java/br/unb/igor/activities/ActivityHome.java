package br.unb.igor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.unb.igor.R;
import br.unb.igor.fragments.FragmentHome;
import br.unb.igor.fragments.FragmentLogin;

public class ActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new FragmentHome())
                .commit();
        }
    }

}
