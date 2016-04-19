package ch.bfh.ti.apa102control;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by micha on 05.04.16.
 */
public class LED {

    LED(int color)
    {
        this.color=color;
    }

    static int[] LED2Buffer(ArrayList<LED> leds)
    {

        int bufferSize=leds.size()*4+8;
        int i=0;
        int[] buffer=new int[bufferSize];

        /*Startframe*/
        for( i=0;i<4;i++)
        {
            buffer[i]=0;
        }

        i=0;
        /* alle LEDs*/
        for (LED led:leds) {
            buffer[4+i*4]=Color.alpha(led.color);
            buffer[5+i*4]=Color.blue(led.color);
            buffer[6+i*4]=Color.green(led.color);
            buffer[7+i*4]=Color.red(led.color);
            i++;

        }
        /* Endframe*/

        for(i=0;i<4;i++)
        {
            buffer[bufferSize-1-i]=0xFF;
        }
        return buffer;
    }


    public int color;
}
