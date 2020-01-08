import processing.sound.Oscillator;
import processing.sound.SinOsc;

public class Voice {
    public static final float radius = 15;
    public boolean enabled = true;
    private Sequence notes = new Sequence();
    private Sequence volumes = new Sequence();
    Oscillator osc;
    Main m;

    public Voice(Main m) {
        this.m = m;
        osc = new SinOsc(m);
    }

    public void draw() {
        Float prev = notes.get(m.time - 1);
        if (prev != null) {
            float x = m.frequencyToX(prev);
            float y = m.volumeToY(volumes.get(m.time-1));
            m.fill(100);
            m.noStroke();
            m.ellipse(x, y, 2*radius, 2*radius);
        }

        if (notes.get(m.time) != null) {
            m.stroke(0);
            m.strokeWeight(2);
            m.fill(enabled ? 255 : 150);
            m.ellipse(getX(), getY(), 2*radius, 2*radius);
        }
    }

    private float getX() {
        return m.frequencyToX(frequency());
    }

    private float getY() {
        return m.volumeToY(volume());
    }

    public Float frequency() {
        return notes.get(m.time);
    }

    public Float frequency(int tick) {
        return notes.get(tick);
    }

    public boolean defined(int tick) {
        return frequency(tick) != null;
    }

    public Float volume() {
        return volumes.get(m.time);
    }

    public void setPos(float x, float y) {
        float freq = m.xToFrequency(x);
        float vol = m.yToVolume(y);
        notes.set(m.time, freq);
        volumes.set(m.time, vol);
        osc.freq(freq);
        osc.amp(vol);
        if (enabled) {
            osc.play();
        }
    }

    public void set(int t, float freq, float vol) {
        notes.set(t, freq);
        volumes.set(t, vol);
    }

    public void delete() {
        osc.stop();
        notes.remove(m.time);
        volumes.remove(m.time);
    }

    public float distanceTo(float x, float y) {
        float dx = (getX() - x);
        float dy = (getY() - y);
        return Main.sqrt(dx*dx + dy*dy);
    }

    public boolean defined() {
        return frequency()!=null;
    }

    public void update() {
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
        enabled ^= true;
        if (enabled) {
            osc.play();
        }
        else {
            osc.stop();
        }
    }
}
