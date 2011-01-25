/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.telmi.msc.scxml.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jocke
 */
public final class ErrorReporter implements org.apache.commons.scxml.ErrorReporter {


     private static final Logger LOG = LoggerFactory.getLogger(ErrorReporter.class);

    /**
     * This method is called by the executor if an error has occurred.
     *
     * @param errCode
     *        The errorCode @see org.apache.commons.scxml.semantics.ErrorConstants
     * @param errDetail
     *        Message about the error.
     * @param errCtx
     */
    @Override
    public void onError(String errCode, String errDetail, Object errCtx) {
        
        
        LOG.error("Error code = '{}' \n Error detail = '{}'", errCode , errDetail);
    }

}
