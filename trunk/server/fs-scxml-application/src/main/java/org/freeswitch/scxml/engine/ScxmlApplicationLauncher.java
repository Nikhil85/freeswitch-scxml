package org.freeswitch.scxml.engine;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.net.URL;
import org.freeswitch.adapter.Session;
import org.freeswitch.scxml.application.ApplicationLauncher;

/**
 *
 * @author jocke
 */
@Singleton
public final class ScxmlApplicationLauncher implements ApplicationLauncher {

    /**
     * The scxml parameter.
     */
    public static final String SIP_TO_PARAMS = "variable_sip_to_params";
    /**
     * Encoding to use.
     */
    public static final String UTF8 = "UTF-8";
    private final ScxmlApplication application;
    private static final Logger LOG =
            LoggerFactory.getLogger(ScxmlApplication.class);
    private static final Pattern MAP_STRIP =
            Pattern.compile("(\\{|}|\\s)");

    /**
     * Create a new ScxmlApplicationLauncher instance.
     *
     * @param app The scxml application.
     */
    @Inject
    ScxmlApplicationLauncher(ScxmlApplication app) {
        this.application = app;
    }

    @Override
    public void launch(Session session) {

        LOG.debug("Starting application");

        try {
            // Get all variables passed in from Freeswitch
            Map<String, Object> vars = session.getVars();

            vars = extractEventData(vars);

            String param = (String) vars.get(SIP_TO_PARAMS);

            if (param == null) {
                session.hangup();
                LOG.info("No SCXML param in request! No way to"
                        + " launch application");
                return;
            }

            param = URLDecoder.decode(param, UTF8);

            String[] prop = param.split("=");
            URL url = new URL(prop[1]);

            vars.put(Session.class.getName(), session);

            application.createAndStartMachine(url, vars);

        } catch (IllegalStateException iex) {
            LOG.error(iex.getMessage());
        } catch (MalformedURLException mex) {
            LOG.error(mex.getMessage());
        } catch (UnsupportedEncodingException uex) {
            LOG.error(uex.getMessage());
        }

        session.hangup();

    }

    /**
     * Extract variables from the arrived event map.
     *
     * @param  vars To add the extracted data to.
     * @return The same Map but with all new variables.
     * @throws UnsupportedEncodingException when encoding is not supported.
     */
    private Map<String, Object> extractEventData(Map<String, Object> vars)
            throws UnsupportedEncodingException {

        String eventMap = (String) vars.get("variable_sip_h_X-Eventmap");

        if (eventMap == null) {
            LOG.info("No event map in message ");
            return vars;

        } else {

            String evtS = URLDecoder.decode(eventMap, UTF8);

            Matcher matcher = MAP_STRIP.matcher(evtS);
            String params = matcher.replaceAll("");

            String[] param = params.split(",");

            for (String string : param) {
                String[] valuePair = string.split("=");
                vars.put(valuePair[0], valuePair[1]);
            }


            return vars;
        }

    }
}
