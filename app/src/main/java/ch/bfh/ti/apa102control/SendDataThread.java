package ch.bfh.ti.apa102control;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by micha on 25.04.16.
 */
public class SendDataThread extends Thread {

    public Handler dataHandler;
    private SPI spi;


    public SendDataThread(){
    }

    public void run(){
        spi=new SPI();
       final  int fd=spi.open("/dev/spidev1.0");
        if(fd==-1){
        }
        try{
        Looper.prepare();
        dataHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int[]data=msg.getData().getIntArray("data");
                spi.write(fd,data);
            }

        };
        Looper.loop();}
        catch (Exception e) {
            spi.close(fd);
        }


    }


}
