package ch.bfh.ti.apa102control;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class MainActivity extends AppCompatActivity {


    SPI spiLED;
    int fileHandle;
    int[] spiCommBuffer = new int[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spiLED=new SPI();
        fileHandle= spiLED.open("/dev/spi");
        spiCommBuffer[0]=0x00;
        spiCommBuffer[1]=0x00;
        spiCommBuffer[2]=0x00;
        spiCommBuffer[3]=0x00;
        spiCommBuffer[4]=0xFF;
        spiCommBuffer[5]=0xFF;
        spiCommBuffer[6]=0x00;
        spiCommBuffer[7]=0x00;
        spiCommBuffer[8]=0xFF;
        spiCommBuffer[9]=0x00;
        spiCommBuffer[10]=0x00;
        spiCommBuffer[11]=0xFF;
        spiCommBuffer[12]=0xFF;
        spiCommBuffer[13]=0xFF;
        spiCommBuffer[14]=0xFF;
        spiCommBuffer[15]=0xFF;
        spiLED.write(fileHandle,spiCommBuffer,16);
        spiLED.close(fileHandle);
    }
}
