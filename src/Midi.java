import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
public class Midi {
    public Midi() {
        MidiDevice device;
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            try {
                device = MidiSystem.getMidiDevice(info);
                device.getTransmitter().setReceiver(new MidiInputReceiver());
                device.open();
                System.out.print(device.getDeviceInfo()+" opened. ");
            } catch (MidiUnavailableException e) { }
        }
        System.out.println();
    }

    public class MidiInputReceiver implements Receiver {
        public void send(MidiMessage msg, long timeStamp) {
            byte[] b = msg.getMessage();
            switch(b[0]) {
                case -112: Main.instance.play(b[1]); break;
                case -128: Main.instance.stop(b[1]); break;
                case -80:
                    double[] frequencies = new double[Main.notes.size()];
                    for (int x = 0; x < Main.notes.size(); x++) {
                        frequencies[x] = Main.notes.get(x);
                    }
                    System.out.println(Harmony.ratio(frequencies));
                    break;
                default:
                    System.out.println(Arrays.toString(b));
            }
        }
        public void close() {}
    }

    public void pedal(boolean state) {
        System.out.println(state);
        /*if (state) {
            double[] frequencies = new double[notes.size()];
            for (int x = 0; x < notes.size(); x++) {
                frequencies[x] = notes.get(x);
            }
            println(Harmony.ratio(frequencies));
        }*/
    }
}