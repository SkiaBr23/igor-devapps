package br.unb.igor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import br.unb.igor.R;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_SCREEN_TIMEOUT = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent maintIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(maintIntent);
                overridePendingTransition(R.anim.fade_in_one,R.anim.fade_out);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);
    }
}
