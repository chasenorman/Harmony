import processing.core.PApplet;
import processing.event.MouseEvent;
import processing.sound.*;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    public static final float low = 70;
    public static final float high = 590;

    public static Main instance;

    public boolean mute = false;
    public boolean shift = false;

    int maxTick = -1;
    int time = 0;
    RangeFunction rf;
    Oscillator[] keyboard = new Oscillator[128];
    public static List<Voice> voices = new ArrayList<>();
    public static List<Float> notes = new ArrayList<>();

    public static Voice attached;

    public static void main(String[] args)  {
        main(Main.class, args);
    }

    public void settings() {
        fullScreen();
        instance = this;
    }

    public void setup() {
        for (int i = 0; i < keyboard.length; i++) {
            keyboard[i] = new SinOsc(this);
            keyboard[i].set(midiToFrequency(i), 0.2f, 0, 0);
        }
        Midi.connect();
        rf = Harmony.harmony(1);
        rectMode(CORNERS);
        try {
            voices = Midi.load(new File("midifile.mid"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        for (Voice v : voices) {
            v.update();
        }
    }

    public synchronized void draw() {
        background(0);
        for (int i = 0; i < voices.size(); i++) {
            if (voices.get(i).defined() && voices.get(i).enabled) {
                randomFill(i);
                plotHarmony(voices.get(i).frequency());
            }
        }
        for (Voice d : voices) {
            d.draw();
        }
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

    public synchronized void play(int midi) {
        keyboard[midi].play();
        notes.add(midiToFrequency(midi));
    }

    public synchronized void stop(int midi) {
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
        int w = rf.rangeAt(high/frequency);
        for (int i = rf.rangeAt(low/frequency); i <= w; i++) {
            //float h = height *(1- (1/(float)(double)rf.values.get(i)));
            float h = ((float)(double)rf.values.get(i))*height/(float)(Harmony.maxComplexity*Harmony.maxComplexity);
            rect(frequencyToX((float)(double)rf.lows.get(i)*frequency), h, frequencyToX((float)(double)rf.highs.get(i)*frequency), height);
        }
    }

    public void pedal(boolean state) {
        System.out.println(state);
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
            return;
        }

        v = nextVoice();
        if (v == null) {
            v = new Voice(this);
            voices.add(v);
        }

        maxTick = max(maxTick, time);
        v.enabled = true;
        v.setPos(mouseX, mouseY);
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

    public void keyPressed() {
        if (key == 's') {
            try {
                Midi.save(voices, maxTick);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (keyCode == LEFT) {
            if (time > 0) {
                time--;
                for (Voice v : voices) {
                    v.update();
                }
            }
            return;
        }

        if (keyCode == RIGHT) {
            time++;
            for (Voice v : voices) {
                v.update();
            }
            return;
        }

        if (key == 'r') {
            voices.forEach(Voice::delete);
        }

        if (key == 'm') {
            mute ^= true;
            for (Voice v : voices) {
                if (v.defined()) {
                    v.enabled = !mute;
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
