package br.unb.igor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import br.unb.igor.R;
import br.unb.igor.fragments.FragmentHome;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_container, new FragmentHome())
                .commit();
        }
    }

}
