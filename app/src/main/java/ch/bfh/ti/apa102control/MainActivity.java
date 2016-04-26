package ch.bfh.ti.apa102control;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    static final String ADC_IN4 = "in_voltage4_raw"; // Channel-4 is for the potentiometer
    static final String LED_L1 = "61";                 //LED1
    static final String BUTTON_T1 = "49";              //Taster1
    static final String BUTTON_T4 = "7";               //Taster4

    static final int CLOSE_APP=0;
    static final int UPDATE_POTI=1;
    public int ADCvalue=0;
    /*
  * Define some useful constants
  */
    final char ON = '0';
    final char OFF = '1';

    final String PRESSED = "0";

    SPI spiLED;             /*SPI Interface*/
    int fileHandle;         /*SPI Handle*/
    Bundle dataBundle;
    /*UI-Elements*/
    TextView textViewLEDCount;
    RadioGroup radioGroupMode;
    SeekBar speedBar;
    ColorSeekBar colorBar;
    CheckBox    checkBoxPoti;
    Button buttonStart;
    RadioButton rbChasing;
    TextView labelColor;
    TextView labelSpeed;


    /*Threads*/
    SendDataThread dataThread;
    ReadDataThread readThread;
    RainBowTread   rainBowTread;


    private Handler mHandler;

    int speed=1;

    ADC adc;                /*ADC Interface*/
    SysfsFileGPIO gpio ;

    List<LED> leds=new ArrayList<LED>();
    int numLEDs=10;

    enum Mode_t{stat, rainbow,chasing}; /*Steuerungsmodus*/
    Mode_t mode=Mode_t.stat;

    boolean started=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Cache UI Components*/
        textViewLEDCount=(TextView)findViewById(R.id.nbrLEDS);
        radioGroupMode=(RadioGroup)findViewById(R.id.radioGroup);
        speedBar=(SeekBar)findViewById(R.id.speedBar);
        colorBar=(ColorSeekBar)findViewById(R.id.colorSlider);
        checkBoxPoti=(CheckBox)findViewById(R.id.cbPoti);
        buttonStart=(Button) findViewById(R.id.buttonStart);
        rbChasing=(RadioButton)findViewById(R.id.rbChasing);
        labelColor=(TextView)findViewById(R.id.colorLabel);
        labelSpeed=(TextView) findViewById(R.id.speedLabel);

        /*disable radio button chasing, mode not implemented*/
        rbChasing.setVisibility(View.INVISIBLE);
        /*disable seekbar chasing, mode not implemented*/
        speedBar.setVisibility(View.INVISIBLE);
         /*disable label chasing, mode not implemented*/
        labelSpeed.setVisibility(View.INVISIBLE);

        /*Colorbar settings*/
        colorBar.setMaxValue(4095);     //colorBar max value=Poti max value
        colorBar.setBarHeight(15);
        colorBar.setThumbHeight(30);
        colorBar.setEnabled(false);
        colorBar.setFocusable(false);
        colorBar.setFocusableInTouchMode(false);
        colorBar.setVisibility(View.VISIBLE);

        /*speedBar settings*/
        speedBar.setVisibility(View.INVISIBLE);
        labelSpeed.setVisibility(View.INVISIBLE);
        speedBar.setMax(50);


        /*Checkbox Poti settings*/
        checkBoxPoti.setEnabled(true);

        /*SPI & ADC init*/
        spiLED = new SPI();
        adc=new ADC();
        gpio= new SysfsFileGPIO();

        mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case CLOSE_APP:
                        finish();
                        break;
                    case UPDATE_POTI:
                        if(checkBoxPoti.isChecked()&&(mode==Mode_t.stat)&&(started==true))
                        {
                            if(Math.abs(colorBar.getColorBarValue()-ADCvalue)>20){
                                colorBar.setColorBarValue(ADCvalue);
                            }
                        }
                        break;
                    default:
                }
                super.handleMessage(msg);
            }
        };

        dataBundle=new Bundle();

        dataThread=new SendDataThread();
        dataThread.start();

        readThread=new ReadDataThread(mHandler);
        readThread.start();

        rainBowTread =new RainBowTread();




        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                speed=speedBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarValue, int alphaValue, int color) {
                colorBar.invalidate();
                if ((mode == Mode_t.stat) && (started == true)) {
                    final int finalColor = colorBar.getColor();
                    writeOneColor(finalColor);
                }
            }
        });

        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbStatic) {
                    mode = Mode_t.stat;
                    colorBar.setVisibility(View.VISIBLE);
                    labelColor.setVisibility(View.VISIBLE);
                    speedBar.setVisibility(View.INVISIBLE);
                    labelSpeed.setVisibility(View.INVISIBLE);
                    checkBoxPoti.setEnabled(true);
                } else if (checkedId == R.id.rbChasing) {
                    mode = Mode_t.chasing;
                    colorBar.setVisibility(View.INVISIBLE);
                    labelColor.setVisibility(View.INVISIBLE);
                    speedBar.setVisibility(View.VISIBLE);
                    labelSpeed.setVisibility(View.VISIBLE);
                    checkBoxPoti.setEnabled(false);

                } else if (checkedId == R.id.rbRainbow) {
                    mode = Mode_t.rainbow;
                    colorBar.setVisibility(View.INVISIBLE);
                    labelColor.setVisibility(View.INVISIBLE);
                    speedBar.setVisibility(View.VISIBLE);
                    labelSpeed.setVisibility(View.VISIBLE);
                    checkBoxPoti.setEnabled(false);
                }

            }
        });

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!started) {
                    started = true;
                    buttonStart.setText("Stop");
                    textViewLEDCount.setEnabled(false);
                    for (int i = 0; i < radioGroupMode.getChildCount(); i++) {
                        radioGroupMode.getChildAt(i).setEnabled(false);
                    }
                    switch (radioGroupMode.getCheckedRadioButtonId()) {
                        case R.id.rbChasing:
                            break;
                        case R.id.rbRainbow:

                            leds.clear();
                            float[] hsv=new float[3];
                            hsv[1]=1.f;
                            hsv[2]=1.f;
                            hsv[0]=0.f;
                            for(int i =0;i<numLEDs;i++){
                                hsv[0]=90.f*i/(numLEDs);
                                leds.add(new LED(Color.HSVToColor(hsv)));
                            }
                            int buffer[]=LED.LED2Buffer(leds);
                            dataBundle.putIntArray("data", buffer);
                            Message msg=dataThread.dataHandler.obtainMessage();
                            msg.setData(dataBundle);
                            dataThread.dataHandler.sendMessage(msg);
                            if(rainBowTread==null)
                            {
                                rainBowTread=new RainBowTread();
                            }
                            rainBowTread.start();
                            break;
                        case R.id.rbStatic:
                            colorBar.setColorBarValue(2000);
                            leds.clear();
                            for (int i = 0; i < numLEDs; i++) {
                                leds.add(new LED(colorBar.getColor()));
                            }
                            writeOneColor(colorBar.getColor());
                            break;
                    }


                } else {
                    if(rainBowTread!=null){
                        rainBowTread.interrupt();
                        rainBowTread=null;
                    }
                    started = false;
                    buttonStart.setText("Start");
                    textViewLEDCount.setEnabled(true);
                    for (int i = 0; i < radioGroupMode.getChildCount(); i++) {
                        radioGroupMode.getChildAt(i).setEnabled(true);
                    }
                    writeOneColor(Color.BLACK);
                }

            }
        });

        textViewLEDCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    numLEDs = Integer.parseInt(textViewLEDCount.getText().toString());
                    //Toast.makeText(getBaseContext(), "Anzahl LEDs" + Integer.toString(numLEDs), Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        writeOneColor(Color.BLACK);
        readThread.interrupt();
        if(rainBowTread!=null){
            rainBowTread.interrupt();
            rainBowTread=null;
        }
        dataThread.dataHandler.getLooper().quit();
        super.onDestroy();
    }

    private void writeOneColor(int Color) {
        /*SPI Test*/

        for(LED led:leds){
            led.color=Color;
        }
        int buffer[]=LED.LED2Buffer(leds);
        dataBundle.putIntArray("data", buffer);
        Message msg=dataThread.dataHandler.obtainMessage();
        msg.setData(dataBundle);
        dataThread.dataHandler.sendMessage(msg);

    }

    class RainBowTread extends Thread{

        List<LED> rainbow=new ArrayList<LED>();
        float[]hsv={0.f,1.f,1.f};
        float Hue=0f;
        @Override
        public void run() {

            try {
                while (!isInterrupted()) {
                        hsv[0]=Hue;
                        for(int i=0;i<numLEDs;i++){
                            hsv[0]+=(360/numLEDs);
                            hsv[0]%=360;
                           leds.set(i,new LED(Color.HSVToColor(hsv)));
                        }
                        Hue++;
                    if(Hue==360)
                        Hue=0;
                       // Collections.rotate(leds, 1);
                        int buffer[]=LED.LED2Buffer(leds);
                        dataBundle.putIntArray("data", buffer);
                        Message msg=dataThread.dataHandler.obtainMessage();
                        msg.setData(dataBundle);
                        dataThread.dataHandler.sendMessage(msg);
                    sleep(50-speed);
                }
            }
            catch(InterruptedException e){

            }
            super.run();
        }
    }

    class ReadDataThread extends Thread{

        private Handler mHandler;
        private boolean ledOn=false;
        ReadDataThread(Handler mHandler){
            this.mHandler=mHandler;
        }
        @Override
        public void run() {
                try{
                    while(!isInterrupted()) {
                        if (ledOn) {
                            gpio.write_value(LED_L1, OFF);
                            ledOn = false;
                        } else {
                            gpio.write_value(LED_L1, ON);
                            ledOn = true;
                        }



                        if (gpio.read_value(BUTTON_T4).equals(PRESSED)) {
                            mHandler.sendEmptyMessage(CLOSE_APP);

                        }
                        Double adcValue = Double.valueOf(adc.read_adc(ADC_IN4));
                        ADCvalue = adcValue.intValue();
                        mHandler.sendEmptyMessage(UPDATE_POTI);

                        sleep(50);
                    }
                }
                catch (InterruptedException e){

                }
            gpio.write_value(LED_L1,OFF);

        }
    }


}
