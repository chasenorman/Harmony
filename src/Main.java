import processing.core.PApplet;
import processing.sound.*;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    public static Main instance;

    SinOsc[] keyboard = new SinOsc[128];
    public static List<Float> notes = new ArrayList<Float>();

    public static void main(String[] args)  {
        main(Main.class, args);
    }

    public void settings() {
        instance = this;
    }

    public void setup() {
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new SinOsc(this);
            keyboard[i].set(midiToFrequency(i), 0.2f, 0, 0);
        }
        new Midi();
    }

    public void draw() {
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
