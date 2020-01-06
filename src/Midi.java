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
            System.out.println(Arrays.toString(msg.getMessage()));
        }
        public void close() {}
    }
}