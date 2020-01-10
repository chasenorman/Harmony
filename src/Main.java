import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.sound.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main extends PApplet {
    public static final float low = 70;
    public static final float high = 590;

    public File save = new File("save.mid");

    public static Main instance;

    public boolean mute = false;
    public boolean shift = false;

    int time = 0;
    Oscillator[] keyboard = new Oscillator[128];
    public static List<Voice> voices = new CopyOnWriteArrayList<>();
    public static List<Float> notes = new CopyOnWriteArrayList<>();

    public static Voice attached;

    public static void main(String[] args)  {
        main(Main.class, args);
    }

    public void settings() {
        //fullScreen();
        size(1000,500);
        instance = this;
    }

    public void setup() {
        surface.setResizable(true);
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new SinOsc(this);
            keyboard[i].set(midiToFrequency(i), 0.2f, 0, 0);
        }
        Midi.connect();
        rectMode(CORNERS);
        try {
            voices = Midi.load(save);
        } catch(Exception e) {
            System.out.println("No save file detected.");
        }
        for (Voice v : voices) {
            v.update(0);
        }
    }

    public void draw() {
        for (Voice v : voices) {
            v.update(time);
        }

        background(0);
        for (int i = 0; i < voices.size(); i++) {
            if (voices.get(i).defined() && voices.get(i).enabled) {
                randomFill(i);
                plotHarmony(voices.get(i).frequency());
            }
        }

        voices.forEach(Voice::drawPrev);
        voices.forEach(Voice::draw);

        for (float f : notes) {
            stroke(255);
            strokeWeight(2);
            line(frequencyToX(f), 0, frequencyToX(f), height);
        }

        fill(255);
        text(time, 20, 20);
    }

    public void randomFill(int i) {
        colorMode(HSB);
        fill((50*(i+1))%255, 255, 255, 100);
        colorMode(RGB);
    }

    public static float midiToFrequency(int midi) {
        return 440 * pow(2, (midi - 69) / 12f);
    }

    public static float velocityToVolume(byte vel) {
        return vel/(5f*127);
    }

    public static byte volumeToVelocity(float vol) {
        return (byte)(vol*127*5);
    }

    public void play(int midi) {
        keyboard[midi].play();
        notes.add(midiToFrequency(midi));
    }

    public void stop(int midi) {
        keyboard[midi].stop();
        notes.remove(midiToFrequency(midi));
    }

    public float xToFrequency(float x) {
        return low + (x/width)*(high - low);
    }

    public float frequencyToX(float frequency) {
        return width*(frequency - low)/(high - low);
    }

    public float yToVolume(float y) {
        float percent = (height-y)/height;
        return percent/5;
    }

    public float volumeToY(float volume) {
        return height*(1-volume*5);
    }

    public void plotHarmony(float frequency) {
        noStroke();
        int w = Harmony.harmony.rangeAt(high/frequency);
        for (int i = Harmony.harmony.rangeAt(low/frequency); i <= w; i++) {
            //float h = height *(1- (1/(float)(double)rf.values.get(i)));
            float h = ((float)(double)Harmony.harmony.values.get(i))*height/(float)Harmony.maxComplexity(2);
            rect(frequencyToX((float)(double)Harmony.harmony.lows.get(i)*frequency), h, frequencyToX((float)(double)Harmony.harmony.highs.get(i)*frequency), height);
        }
    }

    public void pedal(boolean state) {
        if (state) {
            double[] frequencies = new double[notes.size()];
            for (int i = 0; i < notes.size(); i++) {
                frequencies[i] = notes.get(i);
            }
            System.out.println(Harmony.ratio(frequencies));
        }
    }

    public Voice getVoice() {
        Voice best = null;
        float smallest = Float.MAX_VALUE;
        for (Voice v : voices) {
            if (!v.defined()) {
                continue;
            }
            float d = v.distanceTo(mouseX, mouseY);
            if (d < smallest) {
                smallest = d;
                best = v;
            }
        }
        return smallest < Voice.radius + 10 ? best : null;
    }

    public void mouseClicked(MouseEvent evt) {
        if (evt.getCount() == 2) {
            doubleClicked();
            return;
        }

        Voice v = getVoice();
        if (v != null) {
            v.click();
        }
    }

    public Voice nextVoice() {
        for (Voice v : voices) {
            if (!v.defined()) {
                return v;
            }
        }
        return null;
    }

    public void doubleClicked() {
        Voice v = getVoice();
        if (v != null) {
            v.delete();
            if (v.size() == 0) {
                voices.remove(v);
            }
            return;
        }

        v = nextVoice();
        if (v == null) {
            v = new Voice(this);
            voices.add(v);
        }

        v.setPos(mouseX, mouseY);
        v.setEnabled(true);
    }

    public void mousePressed() {
        attached = getVoice();
    }

    public void mouseReleased() {
        attached = null;
    }

    public void mouseDragged() {
        if (attached != null) {
            if (!shift) {
                attached.setPos(mouseX, mouseY);
            } else {
                attached.setPos(frequencyToX(attached.frequency()), mouseY);
            }
        }
    }

    public int maxTick() {
        int maxTick = 0;
        for (Voice v : voices) {
            if (v.size() > maxTick) {
                 maxTick = v.size();
            }
        }
        return maxTick;
    }

    public void keyPressed() {
        if (key == 's') {
            try {
                Midi.save(voices, maxTick(), save);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (keyCode == LEFT) {
            if (time > 0) {
                time--;
                for (Voice v : voices) {
                    v.update(time);
                }
            }
            return;
        }

        if (keyCode == RIGHT) {
            time++;
            for (Voice v : voices) {
                v.update(time);
            }
            return;
        }

        if (key == 'r') {
            List<Voice> toRemove = new ArrayList<>();
            for (Voice v : voices) {
                v.delete();
                if (v.size() == 0) {
                    toRemove.add(v);
                }
            }
            voices.removeAll(toRemove);
        }

        if (key == 'm') {
            mute ^= true;
            for (Voice v : voices) {
                if (v.defined()) {
                    v.setEnabled(!mute);
                }
            }
        }

        if (keyCode == SHIFT) {
            shift = true;
        }
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            shift = false;
        }
    }
}
