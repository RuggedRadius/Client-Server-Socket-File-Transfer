import jdk.jfr.StackTrace;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

public class Derp
{
    public static void main(String[] args)
    {
        System.out.println(coinFlip());
        assertTrue("Derp", coinFlip());
    }

    public static boolean coinFlip()
    {
        Random randyMcRandomson = new Random();
        if (randyMcRandomson.nextDouble() > 0.5)
            return true;
        else
            return false;
    }
}
