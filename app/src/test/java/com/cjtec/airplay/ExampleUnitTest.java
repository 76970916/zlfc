package com.cjtec.airplay;

import org.junit.Test;

import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        for (int i = 0; i < 40; i++) {
            Random random = new Random();
            System.out.println(random.nextInt(22) + 18);
        }
    }
}