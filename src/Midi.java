import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.*;
public class Midi implements Receiver{
    public static final double LOW_SEMITONES = 12*Math.log(55)/Math.log(2) - 33;
    public static final int maxBendSemitones = 2;
    public static final int resolution = 96;

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
                Main.instance.pedal(b[2]==127);
                break;
            default:
                System.out.println(Arrays.toString(b));
        }
    }

    public static void save(List<Voice> voices, int maxTick, File f) throws InvalidMidiDataException, IOException {
        javax.sound.midi.Sequence s = new javax.sound.midi.Sequence(javax.sound.midi.Sequence.PPQ, resolution/4);
        for (int x = 0; x < voices.size(); x++) {
            Track t = s.createTrack();
            //****  General MIDI sysex -- turn on General MIDI sound set  ****
            byte[] b = {(byte) 0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte) 0xF7};
            SysexMessage sm = new SysexMessage();
            sm.setMessage(b, 6);
            MidiEvent me = new MidiEvent(sm, 0);
            t.add(me);

            //****  set tempo (meta event)  ****
            MetaMessage mt = new MetaMessage();
            byte[] bt = {0x02, (byte) 0x00, 0x00};
            mt.setMessage(0x51, bt, 3);
            me = new MidiEvent(mt, 0);
            t.add(me);

            //****  set track name (meta event)  ****
            mt = new MetaMessage();
            String TrackName = (x+1) + "/" + voices.size();
            mt.setMessage(0x03, TrackName.getBytes(), TrackName.length());
            me = new MidiEvent(mt, 0);
            t.add(me);

            //****  set omni on  ****
            ShortMessage mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7D, 0x00);
            me = new MidiEvent(mm, 0);
            t.add(me);

            //****  set poly on  ****
            mm = new ShortMessage();
            mm.setMessage(0xB0, 0x7F, 0x00);
            me = new MidiEvent(mm, 0);
            t.add(me);

            //****  set instrument to Piano  ****
            mm = new ShortMessage();
            mm.setMessage(0xC0, 0x00, 0x00);
            me = new MidiEvent(mm, 0);
            t.add(me);

            populateTrack(t, voices.get(x), maxTick);

            //****  set end of track ****
            mt = new MetaMessage();
            byte[] bet = {}; // empty array
            mt.setMessage(0x2F, bet, 0);
            me = new MidiEvent(mt, resolution*(maxTick+2));
            t.add(me);
        }

        MidiSystem.write(s,1,f);
    }

    public static void populateTrack(Track t, Voice v, int maxTick) throws InvalidMidiDataException {
        MidiEvent me;
        ShortMessage mm;
        for (int i = 0; i <= maxTick; i++) {
            if (v.defined(i)) {
                double semitones = (12 * Math.log(v.frequency(i)) * 1.44269504089) - LOW_SEMITONES;
                byte note = (byte) (semitones + 0.5);
                byte bend = (byte) (64 * (1 + ((semitones - note) / (double) maxBendSemitones)));

                //****  pitch bend  ****
                mm = new ShortMessage();
                mm.setMessage(0xE0, 0x00, bend);
                me = new MidiEvent(mm, i*resolution);
                t.add(me);

                //****  note on  ****
                mm = new ShortMessage();
                mm.setMessage(0x90, note, Main.volumeToVelocity(v.volume(i)));
                me = new MidiEvent(mm, i*resolution + 1);
                t.add(me);

                //****  note off  ****
                mm = new ShortMessage();
                mm.setMessage(0x80, note, 0x40);
                me = new MidiEvent(mm, (i+1)*resolution - 1);
                t.add(me);
            }
        }
    }

    public static List<Voice> load(File f) throws InvalidMidiDataException, IOException {
        javax.sound.midi.Sequence sequence = MidiSystem.getSequence(f);
        List<Voice> voices = new ArrayList<>();
        for (Track t : sequence.getTracks()) {
            voices.add(loadTrack(t));
        }
        return voices;
    }

    public static Voice loadTrack(Track track) {
        Voice v = new Voice(Main.instance);
        for (int i=0; i < track.size(); i++) {
            MidiEvent event = track.get(i);
            MidiMessage message = event.getMessage();
            if (message instanceof ShortMessage) {
                ShortMessage sm = (ShortMessage) message;
                if (sm.getCommand() == 0xE0) {
                    byte bend = (byte)sm.getData2();
                    i++;
                    sm = (ShortMessage) track.get(i).getMessage();
                    byte key = (byte)sm.getData1();

                    float frequency = midiToFrequency(key + ((bend/64f)-1)*maxBendSemitones);

                    //int velocity = sm.getData2();
                    v.set(((int)event.getTick())/resolution, frequency, Main.velocityToVolume((byte)sm.getData2()));
                }
            }
        }
        return v;
    }

    public static float midiToFrequency(float midi) {
        return 440 * Main.pow(2, (midi - 69) / 12f);
    }

    public void close() {}
}