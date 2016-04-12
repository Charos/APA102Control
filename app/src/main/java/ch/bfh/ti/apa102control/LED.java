package ch.bfh.ti.apa102control;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by micha on 05.04.16.
 */
public class LED {

    static char[] LED2Buffer(ArrayList<LED> leds)
    {

        char[] buffer=new char[leds.size()*4+8];
        for(int i=0;i<4;i++)
        {
            buffer[i]=0;
        }
        int i=0;
        for (LED led:leds) {
            buffer[4+i*4]=(char)Color.alpha(led.color);
            buffer[5+i*4]=(char)Color.red(led.color);



        }
        return buffer;
    }


    public int color;
}
