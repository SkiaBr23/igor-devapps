package br.unb.igor.helpers;

import java.util.Random;

/**
 * Created by @skiabr23 on 05/11/17.
 */

public class DiceRoller {

    private static Random rand = new Random();

    public static int roll(int faces){
        return 1 + rand.nextInt(faces);
    }

    public static int roll(int faces, int dice){
        int result = 0;
        for (int i = 0; i < dice; i++){
            result += roll(faces);
        }
        return result;
    }

    public static int binomial(int n, int k) {
        int result = 1;
        k = Math.max(k, n - k);
        for (int i = n; i > k; i--) {
            result *= i;
        }
        for (int i = n - k; i > 1; i--) {
            result /= i;
        }
        return result;
    }

    public static double probability(int value, int faces, int dice) {
        double p = 0.0;
        int iterations = (value - dice) / faces;
        for (int i = 0; i <= iterations; i++) {
            double v = binomial(dice, i) * binomial(value - faces * i - 1, dice - 1);
            if (i % 2 == 0) {
                p += v;
            } else {
                p -= v;
            }
        }
        return 100 * p * Math.pow(faces, -dice);
    }

    public static int roll(int faces, int dices, int modifier){
       return roll(faces, dices) + modifier;
    }

    public static String diceToText(int faces, int dices, int modifier){
        if(modifier > 0)
            return dices + "d" + faces + " + " + modifier;
        if(modifier == 0)
            return diceToText(faces, dices);
        if(modifier < 0)
            return dices + "d" + faces + " - " + modifier*(-1);

        return "";
    }

    public static String diceToText(int faces, int dices){
        return dices + "d" + faces;
    }

    public static String diceToText(int faces){
        return "1d" + faces;
    }
}
