package com.telmi.msc.scxml.sender;

import com.telmi.msc.scxml.actions.SendAction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author jocke
 */
public final class BasicHttpSender implements Sender {

    private static final Logger LOG =
            LoggerFactory.getLogger(BasicHttpSender.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ENCODING = "UTF-8";
    private static final String FORM_URL_ENCODED =
            "application/x-www-form-urlencoded";

    private SCXMLExecutor executor;
    private Context context;

    @Override
    public void setExecutor(SCXMLExecutor aExecutor) {
        this.executor = aExecutor;
    }

    @Override
    public void setContext(Context ctx) {
        this.context = ctx;
    }

    @Override
    public String supports() {
        return "basichttp";
    }

    @Override
    public Sender newInstance() {
        return new BasicHttpSender();
    }

    @Override
    public void send(String sendId, String target, Map<String, Object> params) {

        if (params == null) {
            LOG.error("Send without params is not supported");
            return;
        }

        LOG.debug("sending post to {} ", target);

        try {
            final String query = createQuery(params);
            makeHttpCall(target, query, sendId);
        } catch (UnsupportedEncodingException ex) {
            LOG.error("Oops! failed to encode url {} ", ex.getMessage());
        }
    }

    /**
     * Create a HTTP query string.
     *
     * @param  params Variables to transform.
     * @return A string with value pairs separated with a & sign.
     *
     * @throws UnsupportedEncodingException If encoding is not supported.
     */
    private String createQuery(Map<String, Object> params)
            throws UnsupportedEncodingException {

        StringBuilder builder = new StringBuilder();

        Set<Map.Entry<String, Object>> set = params.entrySet();

        for (Map.Entry<String, Object> entry : set) {

            String key = entry.getKey();
            Object value = entry.getValue();

            if (key != null && value != null) {
                builder.append(URLEncoder.encode(key, ENCODING));
                builder.append("=");
                builder.append(URLEncoder.encode(
                        value.toString(), ENCODING));

                builder.append("&");

            } else {
                LOG.error(" null key or value in params Key='{}' value='{}' ",
                        key, value);

            }

        }

        return builder.toString();
    }

    /**
     * Send a HTTP post request.
     *
     * @param urlToCall URL to send to.
     * @param params    Parameters to send.
     */
    private void makeHttpCall(String urlToCall, String params, String sendid) {

        try {

            URL urlCall = new URL(urlToCall);
            HttpURLConnection con =
                    (HttpURLConnection) urlCall.openConnection();

            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");

            con.setRequestProperty(CONTENT_TYPE, FORM_URL_ENCODED);
            DataOutputStream printout =
                    new DataOutputStream(con.getOutputStream());

            printout.writeBytes(params);
            printout.flush();
            printout.close();

            String contentType = con.getContentType();

            if (contentType.startsWith("text/xml")) {
                Node node = parseXmlResponse(con);
                context.set(sendid, node);

            } else {
                String response = parseResponse(con);
                context.set(sendid, response);
            }

            String event = con.getHeaderField("event");

            if (event != null) {
                fireEvent(event);
            }

        } catch (MalformedURLException ex1) {
            LOG.error("The url is not well formed '{}'  ", ex1.getMessage());
        } catch (IOException ex2) {
            LOG.error("Failed to send post {} ", ex2.getMessage());
        }
    }

    private Node parseXmlResponse(URLConnection connection) {

        DocumentBuilderFactory dfactory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
            return dBuilder.parse(connection.getInputStream());
        } catch (SAXException ex) {
            LOG.error(ex.getMessage());
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        } catch (ParserConfigurationException ex) {
            LOG.error(ex.getMessage());
        }

        return null;

    }

    /**
     * This is a Hack because SCXML does not allow us to
     * fire events upon the machine in this state. So we have to obtain
     * the derived events from the context. This is only possible for
     * send element's using the MS namespace. 
     *
     * @param event The event to fire
     */
    private void fireEvent(String event) {

        @SuppressWarnings("unchecked")
        Collection<TriggerEvent> collection = (Collection<TriggerEvent>)
                context.get(SendAction.DERIVED_EVENTS);

        if (collection == null) {
            LOG.warn("You must use the Send in MS namespace to be "
                    + "able to fire events in send");

        } else {
              collection.add(new TriggerEvent(event, TriggerEvent.SIGNAL_EVENT));
        }

    }

    /**
     * Parse a string response.
     *
     * @param  con
     *         The connection to read from.
     * 
     * @return Response read from connection.
     *
     * @throws IOException
     *         From URLConnection.
     */
    private String parseResponse(HttpURLConnection con) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        String str;
        StringBuffer buffer = new StringBuffer();
        while (null != ((str = bufferedReader.readLine()))) {
            buffer.append(str);

        }

        bufferedReader.close();

        return buffer.toString();

    }
}
