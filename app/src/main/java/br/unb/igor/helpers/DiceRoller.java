package br.unb.igor.helpers;

import java.util.Random;

/**
 * Created by @skiabr23 on 05/11/17.
 */

public class DiceRoller {

    public static int roll(int faces){
        Random rand = new Random();
        return 1 + rand.nextInt(faces);
    }

    public static int roll(int faces, int dices){
        int result = 0;
        for (int i = 0; i < dices; i++){
            result += roll(faces);
        }
        return result;
    }

    public static int roll(int faces, int dices, int modifier){
       return roll(faces,dices) + modifier;
    }

    public static String diceToText(int faces, int dices, int modifier){
        return dices + "d" + faces + " + " + modifier;
    }

    public static String diceToText(int faces, int dices){
        return dices + "d" + faces;
    }

    public static String diceToText(int faces){
        return "1d"+faces;
    }
}
