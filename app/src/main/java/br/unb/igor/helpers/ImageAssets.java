package br.unb.igor.helpers;

import br.unb.igor.R;

public class ImageAssets {

    public static int builtInBackgrounds[] = {
            R.drawable.miniatura_coast,
            R.drawable.miniatura_corvali,
            R.drawable.miniatura_heartlands,
            R.drawable.miniatura_krevast,
            R.drawable.miniatura_sky
    };

    public static int getBackgroundResource(int imageIndex){
        if (imageIndex < 0 || imageIndex >= builtInBackgrounds.length) {
            imageIndex = 0;
        }
        return builtInBackgrounds[imageIndex];
    }

    public static int getBuiltInBackgroundCount() {
        return builtInBackgrounds.length;
    }

}
