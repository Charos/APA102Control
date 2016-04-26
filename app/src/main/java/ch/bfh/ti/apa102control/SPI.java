package ch.bfh.ti.apa102control;

/**
 * Created by student on 05.04.16.
 */
public class SPI {
    public native int open(String deviceName);
    public native int read(int fileHandler, int buffer[], int length);
    public native int write(int fileHandler, int buffer[]);
    public native int close(int fileHandler);
    static
    {
        System.loadLibrary("spi");
    }
}
