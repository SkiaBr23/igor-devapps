package br.unb.igor.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import br.unb.igor.R;

public class SplashScreen extends Activity {

    private ImageView logoAppSplashScreen;
    private static int SPLASH_SCREEN_TIMEOUT = 200;
    private static int ANIMATION_LOGO = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);
        logoAppSplashScreen = (ImageView)findViewById(R.id.logoAppSplashScreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            Animation anim = new TranslateAnimation(Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.ABSOLUTE, -600);
            anim.setDuration(SPLASH_SCREEN_TIMEOUT);
            anim.setFillAfter(true);
            logoAppSplashScreen.startAnimation(anim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.fade_in_320ms, R.anim.fade_none);
                finish();
                }
            }, SPLASH_SCREEN_TIMEOUT);
            }
        }, ANIMATION_LOGO);
    }
}
