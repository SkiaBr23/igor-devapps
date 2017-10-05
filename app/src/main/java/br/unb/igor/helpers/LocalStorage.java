package br.unb.igor.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class LocalStorage {

    private static final String FILENAME = "IgorRPG:PrefsFile";

    public static final SharedPreferences get(Context ctx) {
        return ctx.getSharedPreferences(FILENAME, MODE_PRIVATE);
    }

}
