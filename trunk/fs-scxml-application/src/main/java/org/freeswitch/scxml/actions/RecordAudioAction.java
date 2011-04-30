package org.freeswitch.scxml.actions;

import java.util.Set;
import org.freeswitch.adapter.api.HangupException;
import org.freeswitch.adapter.api.Session;
import org.freeswitch.adapter.api.Event;
import org.freeswitch.adapter.api.DTMF;
import org.freeswitch.adapter.api.EventList;
import org.freeswitch.scxml.engine.CallXmlEvent;

/**
 *
 * Handles the recordaudio element in the callxml 3.0 spec.
 *
 * @see
 * <a href="http://docs.voxeo.com/callxml/3.0/frame.jsp?page=recordaudio.htm">
 *   Callxml
 * </a>
 *
 * @author jocke
 */
public final class RecordAudioAction extends AbstractAction {

    private static final long serialVersionUID = 4365016762809054332L;
    public static final String RECORD_MS = "record_ms";
    public static final String RECORD_DATA = "Application-Data";
    private boolean beep = true;
    private boolean cleardigits = true;
    private String maxtime = "30s";
    private String var;
    private String termdigits = "#";
    private String timevar;
    private String format = "wav";

    /**
     *
     * @return the format.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Set the format of the recording.
     *
     * @param recFormat wav or mp3.
     */
    public void setFormat(String recFormat) {
        this.format = recFormat;
    }

    /**
     *
     * @return the length variable.
     */
    public String getTimevar() {

        if (timevar == null || timevar.isEmpty()) {
            return "duration";

        } else {
            return timevar;
        }
    }

    /**
     *
     * @param time Set length variable.
     */
    public void setTimevar(String time) {
        this.timevar = time;
    }

    /**
     *
     * @return var
     */
    public String getVar() {
        return var;
    }

    /**
     *
     * @param variable Set the recording variable.
     */
    public void setVar(String variable) {
        this.var = variable;
    }

    /**
     *
     * @return beep
     */
    public boolean isBeep() {
        return beep;
    }

    /**
     *
     * @param doBeep To play a short beep or not.
     */
    public void setBeep(boolean doBeep) {
        this.beep = doBeep;
    }

    /**
     *
     * @return cleardigits
     */
    public boolean isCleardigits() {
        return cleardigits;
    }

    /**
     *
     * @param clear The empty the digits queue or not.
     */
    public void setCleardigits(boolean clear) {
        this.cleardigits = clear;
    }

    /**
     *
     * @return maxtime
     */
    public String getMaxtime() {
        return maxtime;
    }

    /**
     *
     * @param max How long to record at most.
     */
    public void setMaxtime(String max) {
        this.maxtime = max;
    }

    /**
     *
     * @return termdigits.
     */
    public String getTermdigits() {
        return termdigits;
    }

    /**
     *
     * @param digits That should halt the collecting of digits.
     */
    public void setTermdigits(String digits) {
        this.termdigits = digits;
    }

    /**
     *
     * @return maxtime as un integer in milliseconds.
     */
    public int getMaxtimeAsMillis() {
        return getMillisFromString(maxtime);
    }

    @Override
    public void handleAction(Session fsSession) throws HangupException {

        Set<DTMF> dtmfTerminationDigits = DTMF.createCollectionFromString(termdigits);

        if (cleardigits) {
            fsSession.clearDigits();
        }

        EventList eventList = fsSession.recordFile(getMaxtimeAsMillis(), beep, dtmfTerminationDigits, format);

        String[] data = getData(eventList);

        setContextVar(var, data[0]);
        setContextVar(getTimevar(), data[1]);

        if (proceed(eventList)) {

            if (eventList.containsAny(dtmfTerminationDigits)) {
                fireEvent(CallXmlEvent.TERMDIGIT);

            } else {
                fireEvent(CallXmlEvent.MAXTIME);
            }
        }
    }

    private String[] getData(EventList eventList) {
        Event event = eventList.get(Event.CHANNEL_EXECUTE_COMPLETE);
        String[] data = event.getVar(RECORD_DATA).split("\\s");
        if (data.length < 2) {
            throw new IllegalStateException("failed to get recording data");
        }
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("beep=").append(beep);
        builder.append(",cleardigits=").append(cleardigits);
        builder.append(",maxtime=").append(maxtime);
        builder.append(",termdigits=").append(termdigits);
        builder.append(",timevar=").append(timevar);
        builder.append(",var=").append(var);
        builder.append("}");
        return builder.toString();
    }
}
