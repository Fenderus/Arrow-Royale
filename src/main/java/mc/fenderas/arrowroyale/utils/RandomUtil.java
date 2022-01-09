package mc.fenderas.arrowroyale.utils;

import java.util.Random;

public class RandomUtil
{

    public static int randomIntProper(int a, int b){
        int min = Math.min(a, b);
        int max = Math.max(a, b);

        return randomInt(min, max);
    }

    public static int randomInt(int a, int b){
        Random random = new Random();

        if (a < 0 && b > 0){
            int rand = random.nextInt(-a, b + b);
            return rand - b;
        }else if (a < 0 && b < 0){
            int rand = random.nextInt(-b, -a);
            return -rand;
        }else if (a > 0 && b < 0){
            int rand = random.nextInt(-a, a + a);
            return rand - a;
        }else if (a > 0 && b > 0){
            return random.nextInt(a, b);
        }
        return 0;
    }


}
