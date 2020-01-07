import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
public class Midi implements Receiver{
    public static void connect() {
        MidiDevice device;
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            try {
                device = MidiSystem.getMidiDevice(info);
                device.getTransmitters().forEach(t->t.setReceiver(new Midi()));
                device.getTransmitter().setReceiver(new Midi());
                device.open();
                System.out.print(device.getDeviceInfo()+" opened. ");
            } catch (MidiUnavailableException e) { }
        }
        System.out.println();
    }

    private Midi() { }

    public void send(MidiMessage msg, long timeStamp) {
        byte[] b = msg.getMessage();
        switch (b[0]) {
            case -112:
                Main.instance.play(b[1]);
                break;
            case -128:
                Main.instance.stop(b[1]);
                break;
            case -80:
                /*if (b[2] == 127) {
                    double[] frequencies = new double[Main.notes.size()];
                    for (int x = 0; x < Main.notes.size(); x++) {
                        frequencies[x] = Main.notes.get(x);
                        System.out.print(frequencies[x] + " ");
                    }
                    System.out.println();
                    System.out.println(Harmony.ratio(frequencies));
                }*/
                break;
            default:
                System.out.println(Arrays.toString(b));
        }
    }

    public void close() {}
}