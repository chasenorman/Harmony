import processing.core.PApplet;
import processing.sound.*;

public class Main extends PApplet {
    public static void main(String[] args)  {
        main(Main.class, args);
    }

    public void setup() {

    }

    public void draw() {
        if (frameCount == 1) {
            SinOsc so = new SinOsc(this);
            so.play();
        }
    }
}
