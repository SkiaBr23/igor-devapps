package br.unb.igor.helpers;

import br.unb.igor.R;

public class ImageAssets {

    public static int getBackgroundResource(int imageIndex){
        switch(imageIndex){
            case 1:
                return R.drawable.miniatura_coast;
            case 2:
                return R.drawable.miniatura_corvali;
            case 3:
                return R.drawable.miniatura_heartlands;
            case 4:
                return R.drawable.miniatura_krevast;
            case 5:
                return R.drawable.miniatura_sky;
            default:
                return R.drawable.miniatura_krevast;
        }
    }

}
