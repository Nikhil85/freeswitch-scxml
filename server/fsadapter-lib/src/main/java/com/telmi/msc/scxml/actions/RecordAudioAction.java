package com.telmi.msc.scxml.actions;

import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSSession;
import com.telmi.msc.scxml.engine.CallXmlEvent;
import java.util.Map;

import java.util.Set;

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
public final class RecordAudioAction extends AbstractCallXmlAction {

    private static final long serialVersionUID = 4365016762809054332L;
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
    public void handleAction(FSSession fsSession) {

        Set<DTMFMessage> dtmfTerminationDigits = DTMFMessage.createCollectionFromString(termdigits);

        if (cleardigits) {
            fsSession.clearDigits();
        }

        FSEvent event = fsSession.recordFile(
                getMaxtimeAsMillis(),
                beep,
                dtmfTerminationDigits,
                format);

        Map<String, Object> vars = fsSession.getVars();

        String path = (String) vars.get("last_rec");
        Long duration = (Long) vars.get("duration");

        setContextVar(var, path);
        setContextVar(getTimevar(), duration);

        if (proceed(event)) {

            if (event.containsAny(dtmfTerminationDigits)) {
                fireEvent(CallXmlEvent.TERMDIGIT);

            } else {
                fireEvent(CallXmlEvent.MAXTIME);

            }
        }
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
