package org.freeswitch.scxml.actions;

import java.util.Map;

import org.freeswitch.adapter.api.Session;


/**
 * Handles the VoiceXML exit element.
 *
 * @author jocke
 *
 * @see <a href="http://www.w3.org/TR/voicexml20/#dml5.3.9" >VoiceXML</a>
 *
 */
public final class ExitAction extends AbstractCallXmlAction {

    private static final long serialVersionUID = -4665515971899256028L;
    private String namelist;
    private String expr;

    /**
     * A string that can be evaluate against a context and transformed
     * to a new string changed if the string contains any variables.
     *
     *  ex.
     * 'The date is ' + new Date().toString();
     *
     *
     * @param exprOfExit The expression to evaluate.
     */
    public void setExpr(String exprOfExit) {
        this.expr = exprOfExit;
    }

    /**
     * A string with variable names separated with a space.
     *
     * @param vars A list of variables names.
     */
    public void setNamelist(String vars) {
        this.namelist = vars;
    }

    /**
     *
     * @return The namelist string.
     */
    public String getNamelist() {
        return namelist;
    }

    /**
     *
     * @return The expr string
     */
    public String getExpr() {
        return expr;
    }


    @Override
    public void handleAction(Session ivrSession) {

        Map<String, Object> vars = getNameListAsMap(namelist);

        if (vars.isEmpty()) {
            ivrSession.hangup();


        } else {
            ivrSession.hangup(vars);

        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("test=").append(getTest());
        builder.append(", namelist=").append(namelist);
        builder.append(", expr=").append(expr);
        builder.append("}");
        return builder.toString();
    }

}
