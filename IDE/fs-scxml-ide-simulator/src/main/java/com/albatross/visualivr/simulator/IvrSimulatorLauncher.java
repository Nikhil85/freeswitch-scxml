package com.albatross.visualivr.simulator;

import com.albatross.visualivr.simulator.api.CommandSimulator;
import com.albatross.visualivr.simulator.ui.SoftPhoneDialog;
import com.albatross.visualivr.utils.EventLookup;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.ApplicationLauncher;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.scxml.engine.ScxmlApplication;
import org.freeswitch.socket.SocketWriter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import com.albatross.visualivr.utils.event.RunProjectEvent;
import org.freeswitch.adapter.SessionImpl;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jocke
 */
@ServiceProvider(service = ApplicationLauncher.class)
public class IvrSimulatorLauncher implements ApplicationLauncher, LookupListener {

    public static final String APP_URL = "app.url";
    private static final Logger LOG = Logger.getLogger(IvrSimulatorLauncher.class.getName());
    private ScxmlApplication application;
    private Lookup.Result<RunProjectEvent> result;
    private ThreadPoolManager poolManager;
    private Map<String, CommandSimulator> executors = new HashMap<String, CommandSimulator>();

    public IvrSimulatorLauncher() {
    }

    @Override
    public void launch(Session session) {
        try {
            Map<String, Object> vars = session.getVars();

            vars.put(Session.class.getName(), session);

            URL url = (URL) vars.get(APP_URL);

            application.createAndStartMachine(url, vars);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }


    }

    public void init() {

        LOG.info("Simulator is listening for events");

        this.result = EventLookup.getDefault().lookupResult(RunProjectEvent.class);

        this.result.addLookupListener(this);
        this.result.allInstances();

        Collection<? extends CommandSimulator> exes = Lookup.getDefault().lookupAll(CommandSimulator.class);

        for (CommandSimulator sc : exes) {
            executors.put(sc.supports(), sc);
            LOG.log(Level.INFO, "Adding support for {0}", sc.supports());
        }

    }

    public void destroy() {
        result.removeLookupListener(this);
        poolManager.shutdownAll();
    }

    @Override
    public void resultChanged(LookupEvent le) {

        //TODO only run if we are not running

        Collection<? extends RunProjectEvent> allInstances = result.allInstances();

        RunProjectEvent evt = allInstances.iterator().next();
        FileObject fileObject = evt.getFileObject();

        try {
            URL url = fileObject.getURL();

            Map<String, Object> data = new HashMap<String, Object>();
            data.put(APP_URL, url);
            data.put("Channel-Unique-ID", UUID.randomUUID());

            BlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
            SoftPhoneDialog dialog = new SoftPhoneDialog(events);
            dialog.setLocationRelativeTo(null);

            dialog.setVisible(true);
            SocketWriter writer = new CommandExcecutor(events, poolManager, executors);
            final Session session = new SessionImpl(data);

            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    launch(session);
                }
            };

            poolManager.getApplicationPool().execute(runnable);

        } catch (FileStateInvalidException ex) {
            Exceptions.printStackTrace(ex);
        }

    }
}
