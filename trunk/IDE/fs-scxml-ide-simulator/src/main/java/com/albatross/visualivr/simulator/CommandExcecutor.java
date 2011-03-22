package com.albatross.visualivr.simulator;

import com.albatross.visualivr.simulator.api.CommandSimulator;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.socket.SocketWriter;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author jocke
 */
public class CommandExcecutor implements SocketWriter {

    private static final Logger LOG = Logger.getLogger(CommandExcecutor.class.getName());
    private static final Pattern APP_PATTERN = Pattern.compile("^(execute-app-name:)(\\s)(\\w*)$", Pattern.MULTILINE);
    private static final Pattern APP_ARGS = Pattern.compile("^(execute-app-arg:)(\\s)(.*)$", Pattern.MULTILINE);
    private Map<String, CommandSimulator> executors;
    
    private Queue<Event> events;
    
    private ThreadPoolManager threadPoolManager;
    
    private InstanceContent content;
    
    private AbstractLookup lookup;

    public CommandExcecutor(Queue<Event> events, ThreadPoolManager threadPoolManager, Map<String, CommandSimulator> executors) {
        this.events = events;
        this.threadPoolManager = threadPoolManager;
        this.content = new InstanceContent();
        this.lookup = new AbstractLookup(content);
        this.executors = executors;

    }

    
    @Override
    public void write(final String data) throws IOException {
        
        LOG.log(Level.INFO, "Data to execute {0}", data);
        
        String appName = null;

        if ((appName = findApplication(data)) != null) {

            if (appName.equals("break")) {
                CommandSimulator simulator = lookup.lookup(CommandSimulator.class);

                if (simulator != null) {
                    LOG.log(Level.INFO, "Will break current action {0} ", simulator.supports());
                    simulator.breakAction();
                    events.offer(Event.named(Event.CHANNEL_EXECUTE_COMPLETE)); // The break
               
                } else {
                    LOG.log(Level.WARNING, "Failed to break action not found");
                }

            } else {

                final CommandSimulator cs = executors.get(appName);

                if (cs != null) {
                    content.add(cs);
                    Runnable runnable = new Runnable() {

                        @Override
                        public void run() {
                            cs.create(events).execute(findApplicationArgs(data));
                        }
                    };

                    threadPoolManager.getApplicationPool().execute(runnable);
                } else {
                    LOG.log(Level.WARNING, "Unsupported application found {0}", appName);
                }
            }
        } else  {
            LOG.log(Level.WARNING, "Could not find application in data {0}", data);
        }

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    private String findApplication(final String data) {

        String result = null;
        Matcher matcher = APP_PATTERN.matcher(data);

        if (matcher.find()) {
            result = matcher.group(3);
        }

        return result;
    }

    String[] findApplicationArgs(final String data) {

        String[] result = null;
        Matcher matcher = APP_ARGS.matcher(data);

        if (matcher.find()) {
            result = matcher.group(3).split("\\s");

        } else {
            result = new String[0];
        }

        return result;
    }
}
