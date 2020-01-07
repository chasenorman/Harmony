import processing.core.PApplet;
import processing.sound.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    public static final double low = 60;
    public static final double high = 2000;

    public static Main instance;

    RangeFunction rf;
    Oscillator[] keyboard = new Oscillator[128];
    public static List<Float> notes = new ArrayList<Float>();

    public static void main(String[] args)  {
        main(Main.class, args);
    }

    public void settings() {
        fullScreen();
        instance = this;
    }

    public void setup() {
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new TriOsc(this);
            keyboard[i].set(midiToFrequency(i), 0.6f, 0, 0);
        }
        Midi.connect();
        rf = Harmony.harmony(1);
        rectMode(CORNERS);
    }

    public void draw() {
        background(0);
        noStroke();
        for (int i = 0; i < notes.size(); i++) {
            randomFill(i);
            plotHarmony(notes.get(i));
        }
    }

    public void randomFill(int i) {
        colorMode(HSB);
        fill(((i+1)*PI*13*11)%255, 255, 255, 100);
        colorMode(RGB);
    }

    public float midiToFrequency(int midi) {
        return 440 * pow(2, (midi - 69) / 12f);
    }

    public void play(int midi) {
        keyboard[midi].play();
        notes.add(midiToFrequency(midi));
    }

    public void stop(int midi) {
        keyboard[midi].stop();
        notes.remove(midiToFrequency(midi));
    }

    public double xToFrequency(double x) {
        return low + (x/width)*(high - low);
    }

    public double frequencyToX(double frequency) {
        return width*(frequency - low)/(high - low);
    }

    public void plotHarmony(float frequency) {
        int w = rf.rangeAt(high/frequency);
        for (int i = rf.rangeAt(low/frequency); i < w; i++) {
            //float h = height *(1- (1/(float)(double)rf.values.get(i)));
            float h = ((float)(double)rf.values.get(i))*height/(float)(Harmony.maxComplexity*Harmony.maxComplexity);
            rect((float)frequencyToX(rf.lows.get(i)*frequency), h, (float)frequencyToX(rf.highs.get(i)*frequency), height);
        }
    }

    public void plot(RangeFunction rf) {
        int w = rf.rangeAt(high);
        for (int i = rf.rangeAt(low); i < w; i++) {
            float h = height *(1- (1/(float)(double)rf.values.get(i)));
            line((float)frequencyToX(rf.lows.get(i)), h, (float)frequencyToX(rf.highs.get(i)), h);
        }
    }

    public void pedal(boolean state) {
        println(state);
        /*if (state) {
            double[] frequencies = new double[notes.size()];
            for (int x = 0; x < notes.size(); x++) {
                frequencies[x] = notes.get(x);
            }
            println(Harmony.ratio(frequencies));
        }*/
    }
}
