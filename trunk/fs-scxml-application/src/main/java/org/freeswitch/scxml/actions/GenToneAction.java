package org.freeswitch.scxml.actions;


import org.freeswitch.adapter.api.Session;

/**
 *
 * @author jocke
 */
public final class GenToneAction extends AbstractAction {

    private static final long serialVersionUID = -7786026569545937761L;

    /**
     * busy.
     */
    public static final String BUSY = "busy";

    /**
     * ring.
     */
    public static final String RING = "ring";

    private static final String RINGING =
            "tone_stream://L=-repeat-%(2000,4000,440,480);%(2000,4000,440,480)";

    private static final String BUSY_TONE =
            "tone_stream://L=-repeat-%(425,425,390,390);%(425,425,390,390)";

    private String value;
    private int repeat;

    /**
     * Get the number of tones to play.
     *
     * @return repeat in int.
     */
    public int getRepeat() {
        return repeat;
    }

    /**
     * Set the number of tones to play.
     *
     * @param toneRepeat a number more then 0;
     */
    public void setRepeat(int toneRepeat) {
        this.repeat = toneRepeat;
    }


    /**
     * Get the value of value.
     *
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of value.
     *
     * @param toneToPlay new value of value
     */
    public void setValue(String toneToPlay) {
        this.value = toneToPlay;
    }


    @Override
    public void handleAction(Session ivrSession) {

        if (value.equalsIgnoreCase(RING)) {
            ivrSession.streamFile(
                    RINGING.replace("-repeat-", Integer.toString(repeat)));

        } else if (value.equalsIgnoreCase(BUSY)) {

            ivrSession.streamFile(
                    BUSY_TONE.replace("-repeat-", Integer.toString(repeat)));

        } else {
            throw new IllegalStateException(
                "Gentone value should be one off -->" + BUSY + "," + RINGING);

        }

    }

}
