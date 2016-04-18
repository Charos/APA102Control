package ch.bfh.ti.apa102control;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rtugeek.android.colorseekbar.ColorSeekBar;


public class MainActivity extends AppCompatActivity {


    SPI spiLED;
    int fileHandle;
    int[] spiCommBuffer = new int[16];

    TextView textViewLEDCount;
    RadioGroup radioGroupMode;
    SeekBar speedBar;
    ColorSeekBar colorBar;

    int numLEDs=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Cache UI Components*/
        textViewLEDCount=(TextView)findViewById(R.id.nbrLEDS);
        radioGroupMode=(RadioGroup)findViewById(R.id.radioGroup);
        speedBar=(SeekBar)findViewById(R.id.speedBar);
        colorBar=(ColorSeekBar)findViewById(R.id.colorSlider);

        speedBar.setEnabled(false);
        colorBar.setEnabled(false);
        colorBar.setFocusable(false);
        colorBar.setFocusableInTouchMode(false);
        colorBar.setVisibility(View.INVISIBLE);
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

            }
        });


        radioGroupMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rbStatic){
                    colorBar.setVisibility(View.VISIBLE);
                    speedBar.setEnabled(false);
                }else if(checkedId==R.id.rbChasing){
                    colorBar.setVisibility(View.INVISIBLE);
                    speedBar.setEnabled(true);

                }else if(checkedId==R.id.rbRainbow){
                    colorBar.setVisibility(View.INVISIBLE);
                    speedBar.setEnabled(false);

                }

            }
        });


        /*SPI Test*/
       /* spiLED=new SPI();
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
        spiLED.close(fileHandle);*/
    }
}
