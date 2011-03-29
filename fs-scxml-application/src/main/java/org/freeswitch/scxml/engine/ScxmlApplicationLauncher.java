package org.freeswitch.scxml.engine;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.scxml.ApplicationLauncher;
import org.openide.util.Lookup;

/**
 *
 * @author jocke
 */
public final class ScxmlApplicationLauncher implements ApplicationLauncher {

    public static final String SIP_TO_PARAMS = "variable_sip_to_params";
    public static final String SCXML_VAR = "variable_scxml";
    public static final String UTF8 = "UTF-8";
    private static final Logger LOG = LoggerFactory.getLogger(ScxmlApplicationLauncher.class);

    /**
     * Create a new ScxmlApplicationLauncher instance.
     *
     * @param app The scxml application.
     */
    public ScxmlApplicationLauncher() {
    }

    @Override
    public void launch(Session session) {

        LOG.debug("Starting application");

        try {
            // Get all variables passed in from Freeswitch
            Map<String, Object> vars = session.getVars();
            String targetUrl = (String) (vars.containsKey(SIP_TO_PARAMS) ? vars.get(SIP_TO_PARAMS) : vars.get(SCXML_VAR));

            if (targetUrl == null) {
                session.hangup();
                LOG.info("No SCXML param in request! No way to" + " launch application");
                return;
            }

            targetUrl = URLDecoder.decode(targetUrl, UTF8);

            if (targetUrl.contains("=")) {
                targetUrl = targetUrl.split("=")[1];
            }

            URL url = new URL(targetUrl);

            ScxmlApplication app = Lookup.getDefault().lookup(ScxmlApplication.class);

            if (app != null) {
                vars.put(Session.class.getName(), session);
                app.createAndStartMachine(url, vars);
            
            } else {
                LOG.warn("New scxml application was found unable to launch application");
            }

        } catch (IllegalStateException iex) {
            LOG.error(iex.getMessage());
        } catch (MalformedURLException mex) {
            LOG.error(mex.getMessage());
        } catch (UnsupportedEncodingException uex) {
            LOG.error(uex.getMessage());
        }

    }
}
