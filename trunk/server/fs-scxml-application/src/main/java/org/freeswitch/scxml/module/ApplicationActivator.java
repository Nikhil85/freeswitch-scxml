/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.freeswitch.scxml.module;

import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.Var;
import org.freeswitch.adapter.api.SessionFactory;
import org.freeswitch.scxml.ApplicationLauncher;
import org.freeswitch.scxml.ThreadPoolManager;
import org.freeswitch.scxml.actions.AnswerAction;
import org.freeswitch.scxml.actions.ExitAction;
import org.freeswitch.scxml.actions.GenToneAction;
import org.freeswitch.scxml.actions.GetDigitsAction;
import org.freeswitch.scxml.actions.HangupAction;
import org.freeswitch.scxml.actions.InputDigitsAction;
import org.freeswitch.scxml.actions.MenuAction;
import org.freeswitch.scxml.actions.PhraseAction;
import org.freeswitch.scxml.actions.PlayAudioAction;
import org.freeswitch.scxml.actions.RecordAudioAction;
import org.freeswitch.scxml.actions.SayAction;
import org.freeswitch.scxml.actions.SendAction;
import org.freeswitch.scxml.actions.WaitAction;
import org.freeswitch.scxml.engine.ScxmlApplicationLauncher;
import org.freeswitch.scxml.pool.ThreadPoolManagerImpl;
import org.freeswitch.scxml.sender.BasicHttpSender;
import org.freeswitch.scxml.sender.Sender;
import org.freeswitch.scxml.sender.SenderFactory;
import org.freeswitch.scxml.sender.SenderFactoryImpl;
import org.freeswitch.scxml.sender.SipReferSender;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 *
 * @author jocke
 */
public class ApplicationActivator implements BundleActivator {

     private static final String NAME_SPACE = "http://www.freeswitch.org/";
    
    @Override
    public void start(BundleContext context) throws Exception {
        SessionFactory lookup = Lookup.getDefault().lookup(SessionFactory.class);
        System.out.println(lookup);
        
        context.registerService(ApplicationLauncher.class.getName(), new ScxmlApplicationLauncher(), null);
        context.registerService(ThreadPoolManager.class.getName(), new ThreadPoolManagerImpl(), null);
        context.registerService(SenderFactory.class.getName(), new SenderFactoryImpl(), null); 
        context.registerService(Sender.class.getName(), new BasicHttpSender(), null); 
        context.registerService(Sender.class.getName(), new SipReferSender(), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "menu", MenuAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "var", Var.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "answer", AnswerAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "exit", ExitAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "hangup", HangupAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "playaudio", PlayAudioAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "recordaudio", RecordAudioAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "getdigits", GetDigitsAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "wait", WaitAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "inputdigits", InputDigitsAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "phrase", PhraseAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "gentone", GenToneAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "say", SayAction.class), null);
        context.registerService(CustomAction.class.getName(), new CustomAction(NAME_SPACE, "send", SendAction.class), null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        //TODO add deactivation code here
    }

}
