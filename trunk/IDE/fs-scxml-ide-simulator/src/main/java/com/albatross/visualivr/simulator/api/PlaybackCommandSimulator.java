package com.albatross.visualivr.simulator.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.freeswitch.adapter.api.Event;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jocke
 */
@ServiceProvider(service = CommandSimulator.class)
public class PlaybackCommandSimulator implements CommandSimulator, LineListener {
    
    public static final String PLAYBACK = "playback";

    private Queue<Event> events;
    private static final Logger LOG = Logger.getLogger(PlaybackCommandSimulator.class.getName());
    private Clip clip;

    public PlaybackCommandSimulator() {
        events = new LinkedList<Event>();
    }

    public PlaybackCommandSimulator(Queue<Event> events) {
        this.events = events;
    }

    @Override
    public void execute(String[] args) {

        LOG.info("Will play audio file");

        if (args == null || args.length < 1) {
            LOG.severe("Failed to execute playback no args found");
            return;

        } else {
            AudioInputStream ais = null;

            try {

                File file = new File(args[0]);

                if (file.exists()) {
                    clip = AudioSystem.getClip();
                    ais = AudioSystem.getAudioInputStream(new FileInputStream(file));
                    clip.open(ais);
                    clip.start();
                    clip.addLineListener(this);
                    
                } else {
                    throw new FileNotFoundException("The file " + file.getPath() + " does not exist");
                }

            } catch (FileNotFoundException ex) {
              handleError(ex);
            } catch (IOException ioe) {
               handleError(ioe);
            } catch (LineUnavailableException lue) {
                handleError(lue);
            } catch (UnsupportedAudioFileException uafe) {
                handleError(uafe);

            } finally {

                if (ais != null) {
                    try {
                        ais.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        }

    }

    private void handleError(Throwable uafe) {
        Exceptions.printStackTrace(uafe);
        events.offer(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
    }

    @Override
    public void breakAction() {
        if(clip != null) {
            clip.stop();    
            clip.close();
        }
    }

    @Override
    public String supports() {
        return PLAYBACK;
    }

    @Override
    public CommandSimulator create(Queue queue) {
        return new PlaybackCommandSimulator(queue);
    }

    @Override
    public void update(LineEvent event) {

        LOG.log(Level.INFO, "Event from playing {0}", event.getType());
        Type type = event.getType();

        if (type.equals(Type.STOP)) {
            LOG.info("Playing CHANNEL_EXECUTE_COMPLETE");
            clip.close();
            events.offer(Event.named(Event.CHANNEL_EXECUTE_COMPLETE));
        }

    }
}
