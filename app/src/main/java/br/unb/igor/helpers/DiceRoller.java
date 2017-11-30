package br.unb.igor.helpers;

import java.util.HashMap;
import java.util.Random;

public class DiceRoller {

    private static Random rand = new Random();
    private static final HashMap<Integer, Double> memoTableProbabilities = new HashMap<>();
    private static final HashMap<Integer, Double> memoTableProbabilitiesAtLeast = new HashMap<>();

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

    public static double binomial(int n, int k) {
        double result = 1;
        k = Math.max(k, n - k);
        int multiply = n;
        int divide = n - k;

        while (multiply > k || divide > 1) {
            if (multiply > k) {
                result *= multiply;
                multiply--;
            }
            if (divide > 1) {
                result /= divide;
                divide--;
            }
        }
        return result;
    }

    public static double probability(int value, int faces, int dice) {
        int key = (value << 20) | ((faces & 0x3FF) << 10) | (dice & 0x3FF);
        Double result = memoTableProbabilities.get(key);
        if (result != null) {
            return result;
        }
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
        p = 100 * p * Math.pow(faces, -dice);
        memoTableProbabilities.put(key, p);
        return p;
    }

    public static double probabilityAtLeast(int value, int faces, int dice) {
        int key = (value << 20) | ((faces & 0x3FF) << 10) | (dice & 0x3FF);
        Double result = memoTableProbabilitiesAtLeast.get(key);
        if (result != null) {
            return result;
        }
        double p = 100.;
        for (int i = dice; i < value; i++) {
            p -= probability(i, faces, dice);
        }
        memoTableProbabilitiesAtLeast.put(key, p);
        return p;
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
