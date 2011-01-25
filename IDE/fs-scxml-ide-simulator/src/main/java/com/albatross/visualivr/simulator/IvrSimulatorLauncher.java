package com.albatross.visualivr.simulator;

import com.albatross.visualivr.simulator.api.CommandSimulator;
import com.albatross.visualivr.simulator.ui.SoftPhoneDialog;
import com.albatross.visualivr.utils.EventLookup;
import com.albatross.visualivr.utils.event.RunProjectEvent;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.freeswitch.FSSessionImpl;
import com.telmi.msc.fsadapter.ivr.ApplicationLauncher;
import com.telmi.msc.fsadapter.pool.ThreadPoolManager;
import com.telmi.msc.fsadapter.transport.SocketWriter;
import com.telmi.msc.scxml.engine.ScxmlApplication;
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
import javax.inject.Inject;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author jocke
 */
public class IvrSimulatorLauncher implements ApplicationLauncher, LookupListener {

    public static final String APP_URL = "app.url";
    private static final Logger LOG = Logger.getLogger(IvrSimulatorLauncher.class.getName());
    private ScxmlApplication application;
    private Lookup.Result<RunProjectEvent> result;
    private final ThreadPoolManager poolManager;
    private Map<String, CommandSimulator> executors = new HashMap<String, CommandSimulator>();

    @Inject
    public IvrSimulatorLauncher(ScxmlApplication application, ThreadPoolManager poolManager) {
        this.application = application;
        this.poolManager = poolManager;
    }

    @Override
    public void launch(FSSession session) {

        Map<String, Object> vars = session.getVars();

        vars.put(FSSession.class.getName(), session);

        URL url = (URL) vars.get(APP_URL);

        try {
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

            BlockingQueue<FSEvent> events = new LinkedBlockingQueue<FSEvent>();
            SoftPhoneDialog dialog = new SoftPhoneDialog(events);
            dialog.setLocationRelativeTo(null);
            
            dialog.setVisible(true);
            SocketWriter writer = new CommandExcecutor(events, poolManager, executors);
            final FSSession session = new FSSessionImpl(data, writer, poolManager.getScheduler(), "/tmp", events);

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
