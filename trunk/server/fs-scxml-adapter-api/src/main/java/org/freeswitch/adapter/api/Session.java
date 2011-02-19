package org.freeswitch.adapter.api;

import java.util.Map;
import java.util.Set;

/**
 *
 *
 *
 * @author jocke
 */
public interface Session {

    /**
     * Get all the variables that was sent by
     * FreeSwitch when this call was initiated.
     *
     * @return The map with all variables.
     *
     * @see
     * <a href="http://wiki.freeswitch.org/wiki/Event_socket_outbound">
     *   FreeSwitch
     * </a>
     */
    Map<String, Object> getVars();

    /**
     * Get the unique ID of this call.
     *
     * @return The unique ID.
     */
    String getUuid();

    /**
     * Will make the call ready for media.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the answer action.
     *
     */
    Event answer();

    /**
     * Will shutdown the call and close all media streams.
     * <p>
     *  Calling the Session objects methods after calling this method
     *  will not work.
     * </p>
     *
     * @return An IvrEvent with events that was collected while executing
     *         the hangup action.
     *
     */
    Event hangup();

    /**
     *  Play combined WAV files to the caller in a meaning full way.
     * <p>
     * ex
     * <br/>
     * read number, like "twelve thousand, three hundred forty-five <br />
     *
     * {@code say("en","number","pronounced","12345")}
     *
     * </p>
     *
     *
     * @param moduleName Module name is usually the channel language,
     *                   e.g. "en" or "es"
     *
     * @param type Say type is one of the following.
     *        <ul>
     *        <li>NUMBER</li>
     *        <li>ITEMS</li>
     *        <li>PERSONS
     *        <li>MESSAGES</li>
     *        <li>CURRENCY</li>
     *        <li>TIME_MEASUREMENT</li>
     *        <li>CURRENT_DATE</li>
     *        <li>CURRENT_TIME</li>
     *        <li>CURRENT_DATE_TIME</li>
     *        <li>TELEPHONE_NUMBER</li>
     *        <li>TELEPHONE_EXTENSION</li>
     *        <li>URL</li>
     *        <li>IP_ADDRESS</li>
     *        <li>EMAIL_ADDRESS</li>
     *        <li>POSTAL_ADDRESS</li>
     *        <li>ACCOUNT_NUMBER</li>
     *        <li>NAME_SPELLED</li>
     *        <li>NAME_PHONETIC</li>
     *        <li>SHORT_DATE_TIME</li>
     *        </ul>
     *
     * @param method Say method is one of the following.
     *        <ul>
     *        <li>PRONOUNCED</li>
     *        <li>ITERATED</li>
     *        <li>COUNTED</li>
     *        </ul>
     * @param value The thing to phrase.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the say action.
     */
    Event say(String moduleName, String sayType, String sayMethod, String value);

    /**
     * Record a file.
     *
     * <p>
     *  Note: to get a reference to the path where the recording was placed
     *  use {@code getVars().get("last_rec")}.
     * </p>
     *
     * @param timeLimitInMs The max length of the recording as milliseconds.
     *
     * @param beep          If the the recording should start with a small beep.
     *
     * @param terms         A list of {@link com.telmi.msc.fsadapter.fs.DTMFMessage} that
     *                      should stop the recording if one of them are pressed
     *                      by the caller.
     *
     * @param format        The format wav or mp3 is supported.
     *
     * @return              An IvrEvent with events that was collected
     *                      while executing the record file action.
     */
    Event recordFile(int timeLimitInMs, boolean beep, Set<DTMF> terms,
            String format);

    /**
     * Speak a text using the default TTS Engine.
     *
     * @param text The text to speak.
     *
     * @return     An IvrEvent with events that was collected while executing
     *             the speak action.
     */
    Event speak(String text);

    /**
     *
     * Collect a bunch of {@link com.telmi.msc.fsadapter.fs.DTMFMessage} representing
     * the numbers pressed by the caller.
     *
     * @param maxdigits The number of DTMF tones to collect at most.
     *
     * @param terms     Digits that when pressed should halt the collecting
     *                  of digits.
     *
     * @param timeout   Number of ms to let the caller
     *                  to be able to press digits.
     *
     * @return          An IvrEvent with events that was collected
     *                  while executing the get digits action.
     */
    Event getDigits(int maxdigits, Set<DTMF> terms, long timeout);

    /**
     * Collect a bunch of {@link com.telmi.msc.fsadapter.fs.DTMFMessage} representing
     * the numbers pressed by the caller, after playing a wav file.
     *
     *
     * @param maxDigits The number of DTMF tones to collect at most.
     *
     * @param prompt    The file to play.
     *
     * @param terms     Digits that when pressed should halt the collecting
     *                  of digits.
     *
     * @param timeout   Number of ms to let the caller to be
     *                  able to press digits.
     *
     * @return          An IvrEvent with events that was collected while
     *                  executing the read action.
     */
    Event read(int maxDigits, String prompt, long timeout, Set<DTMF> terms);

    /**
     * Play a sound file to the caller.
     *
     * <p>
     *  There is no check to see if the file is actually there.
     *  If the file is not playing see the log output from the
     *  IVR (FreeSwitch).
     * </p>
     *
     * @param  file The file to play.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the stream file action.
     */
    Event streamFile(String file);

    /**
     * Play a sound file to the caller. Halt if the caller
     * presses a terminator digit.
     *
     * <p>
     *  There is no check to see if the file is actually there.
     *  If the file is not playing see the log output from the
     *  IVR (FreeSwitch).
     * </p>
     *
     * @param  value The file to play.
     *
     * @param  terms  Digits that should halt the execution of this action.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the stream file action.
     */
    Event streamFile(String value, Set<DTMF> terms);

    /**
     * Pause the application Thread.
     * <p>
     *  This has the same affect like calling {@link Thread#sleep(long)}
     * </p>
     * @param milliseconds  Milliseconds to halt execution.
     */
    void sleep(long milliseconds);

    /**
     * Transfer the call.
     *
     * @param target Should be an SIP address.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the deflect action.
     */
    Event deflect(String target);

    /**
     * Hangup the call and pass variables to the calling script.
     *
     * @param  nameList name and value of variables to pass.
     *
     * @return An IvrEvent with events that was collected while executing
     *         the hangup action.
     *
     */
    Event hangup(Map<String, Object> nameList);

    Event beep();

    /**
     * Empty the digits queue before executing an action.
     *
     * @return true if digits was removed false otherwise.
     */
    boolean clearDigits();

    /**
     * Test if the session has disconnected with the server
     *
     * @return
     */
    boolean isAlive();
}
