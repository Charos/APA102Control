package ch.bfh.ti.apa102control;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    static final String ADC_IN4 = "in_voltage4_raw"; // Channel-4 is for the potentiometer

    SPI spiLED;             /*SPI Interface*/
    int fileHandle;         /*SPI Handle*/

    /*UI-Elements*/
    TextView textViewLEDCount;
    RadioGroup radioGroupMode;
    SeekBar speedBar;
    ColorSeekBar colorBar;
    CheckBox    checkBoxPoti;

    /*
 * Timer task
 */
    Timer timer;
    MyTimerTask myTimerTask;
    final Handler handler = new Handler();

    ADC adc;                /*ADC Interface*/

    ArrayList <LED> leds=new ArrayList<LED>();
    int numLEDs=10;

    enum Mode_t{stat, rainbow,chasing}; /*Steuerungsmodus*/
    Mode_t mode=Mode_t.rainbow;

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

        /*Colorbar settings*/
        colorBar.setMaxValue(4095);     //colorBar max value=Poti max value
        colorBar.setBarHeight(15);
        colorBar.setThumbHeight(30);
        colorBar.setEnabled(false);
        colorBar.setFocusable(false);
        colorBar.setFocusableInTouchMode(false);
        colorBar.setVisibility(View.INVISIBLE);

        /*speedBar settings*/
        speedBar.setEnabled(false);

        /*Checkbox Poti settings*/
        checkBoxPoti.setEnabled(false);

        /*SPI & ADC init*/
        spiLED = new SPI();
        adc=new ADC();

        fileHandle = spiLED.open("/dev/spidev1.0");

              /*
       * Re-schedule timer here otherwise, IllegalStateException of
       * "TimerTask is scheduled already" will be thrown
       */
        if(mode==Mode_t.stat) {
           startPotiTimer();

        }


        textViewLEDCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    numLEDs = Integer.parseInt(textViewLEDCount.getText().toString());
                    Toast.makeText(getBaseContext(), "Anzahl LEDs" + Integer.toString(numLEDs), Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });



        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

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

                if (mode == Mode_t.stat) {
                    final int finalColor=color;
                    AsyncTask x = new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            writeOneColor(finalColor);
                            return 0;
                        }
                    }.execute();

                }

            }
        });

        checkBoxPoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkBoxPoti.isChecked()){
                    if(mode==Mode_t.stat){
                        startPotiTimer();
                    }
                }
                else{
                    stopPotiTimer();
                }
            }
        });



        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbStatic) {
                    mode = Mode_t.stat;
                    colorBar.setVisibility(View.VISIBLE);
                    speedBar.setEnabled(false);
                    checkBoxPoti.setEnabled(true);
                    leds.clear();
                    for (int i = 0; i < numLEDs; i++) {
                        leds.add(new LED(colorBar.getColor()));
                    }
                    int buffer[] = LED.LED2Buffer(leds);
                  //  fileHandle = spiLED.open("/dev/spidev1.0");
                    spiLED.write(fileHandle, buffer, buffer.length);
                   // spiLED.close(fileHandle);
                    if(checkBoxPoti.isChecked()) {
                        startPotiTimer();
                    }
                }
                else if (checkedId == R.id.rbChasing) {
                    mode=Mode_t.chasing;
                    colorBar.setVisibility(View.INVISIBLE);
                    speedBar.setEnabled(true);
                    checkBoxPoti.setEnabled(false);

                    stopPotiTimer();

                }
                else if (checkedId == R.id.rbRainbow) {
                    mode=Mode_t.rainbow;
                    colorBar.setVisibility(View.INVISIBLE);
                    speedBar.setEnabled(false);
                    checkBoxPoti.setEnabled(false);

                    stopPotiTimer();

                }

            }
        });
    }


    @Override
    protected void onStop() {

        spiLED.close(fileHandle);
        stopPotiTimer();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        spiLED.close(fileHandle);
        stopPotiTimer();
        super.onDestroy();
    }

    private void writeOneColor(int Color) {
        /*SPI Test*/

        for(LED led:leds){
            led.color=Color;
        }
        int buffer[]=LED.LED2Buffer(leds);

        spiLED.write(fileHandle, buffer, buffer.length);

    }

    /*
     * Local Timer class
     * Periodically read the ADC value and update the UI
     */
    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AsyncTask x= new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
                            Double adcValue=Double.valueOf(adc.read_adc(ADC_IN4));
                            return adcValue;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            Double adcValue=(Double) o;
                            if(Math.abs(adcValue-colorBar.getColorBarValue())>20){
                                colorBar.setColorBarValue(colorBar.getMaxValue()*adcValue.intValue()/4095);
                            }

                            super.onPostExecute(o);
                        }
                    }.execute();
                }
            });
//            runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//	          /*
//	           * Read analog value form adc4 and display it on the screen
//	           */
//                    Double  adcValue = Double.valueOf(adc.read_adc(ADC_IN4));
//                   colorBar.setColorBarValue(colorBar.getMaxValue()*adcValue.intValue()/4095);
//                }});
        }
    }

    private void startPotiTimer(){
        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 0, 50);

    }

    private void stopPotiTimer(){
        if (timer != null)
        {
            timer.cancel();
            timer.purge();
            timer = null;
        }

    }
}
