import processing.sound.Oscillator;
import processing.sound.SinOsc;
import processing.sound.TriOsc;

public class Voice {
    public static final float radius = 15;
    public boolean enabled = true;
    private Sequence notes = new Sequence();
    private Sequence volumes = new Sequence();
    private int time = 0;
    Oscillator osc;
    Main m;

    public Voice(Main m) {
        this.m = m;
        osc = new TriOsc(m);
    }

    public void drawPrev() {
        if (defined(time - 1)) {
            m.fill(100);
            m.noStroke();
            m.ellipse(getX(time-1), getY(time-1), 2*radius, 2*radius);
        }
    }

    public void draw() {
        if (defined()) {
            m.stroke(0);
            m.strokeWeight(2);
            m.fill(enabled ? 255 : 150);
            m.ellipse(getX(time), getY(time), 2*radius, 2*radius);
        }
    }

    private float getX(int tick) {
        return m.frequencyToX(frequency(tick));
    }

    private float getY(int tick) {
        return m.volumeToY(volume(tick));
    }

    public Float frequency() {
        return frequency(time);
    }

    public Float frequency(int tick) {
        return notes.get(tick);
    }

    public Float volume(int tick) {
        return volumes.get(tick);
    }

    public boolean defined(int tick) {
        return frequency(tick) != null;
    }

    public Float volume() {
        return volume(time);
    }

    public void setPos(float x, float y) {
        float freq = m.xToFrequency(x);
        float vol = m.yToVolume(y);
        set(time, freq, vol);
    }

    public void set(int t, float freq, float vol) {
        notes.set(t, freq);
        volumes.set(t, vol);
        osc.freq(freq);
        osc.amp(vol);
        if (enabled) {
            osc.play();
        }
    }

    public void delete() {
        osc.stop();
        notes.set(time, null);
        volumes.set(time, null);
    }

    public float distanceTo(float x, float y) {
        float dx = (getX(time) - x);
        float dy = (getY(time) - y);
        return Main.sqrt(dx*dx + dy*dy);
    }

    public boolean defined() {
        return frequency()!=null;
    }

    public void update(int time) {
        this.time = time;
        if (defined()) {
            osc.freq(frequency());
            osc.amp(volume());
            if (enabled) {
                osc.play();
            }
        } else {
            osc.stop();
        }
    }

    public void click() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
        if (enabled) {
            osc.play();
        }
        else {
            osc.stop();
        }
    }

    public int size() {
        return notes.size();
    }
}
