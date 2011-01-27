package org.freeswitch.scxml.engine;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.Var;
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
import org.freeswitch.scxml.sender.BasicHttpSender;
import org.freeswitch.scxml.sender.Sender;
import org.freeswitch.scxml.sender.SenderFactory;
import org.freeswitch.scxml.sender.SenderFactoryImpl;
import org.freeswitch.scxml.sender.SipReferSender;

/**
 *
 * @author jocke
 */
public final class ScxmlModule extends AbstractModule {

    private static final String NAME_SPACE = "http://telmi.se/MS";

    /**
     * Configure the SCXML module.
     */
    @Override
    protected void configure() {
        bind(ScxmlApplication.class).to(ScxmlApplicationImp.class);
        bind(SenderFactory.class).to(SenderFactoryImpl.class);
        bindSenders();
        bindActions();

    }

    /**
     * Bind all the applications senders.
     */
    private void bindSenders() {

        Multibinder<Sender> senderBinder =
                Multibinder.newSetBinder(binder(), Sender.class);

        senderBinder.addBinding().to(BasicHttpSender.class);
        senderBinder.addBinding().to(SipReferSender.class);
    }

    /**
     * Bind all custom actions.
     */
    public void bindActions() {

        Multibinder<CustomAction> actionBinder =
                Multibinder.newSetBinder(binder(), CustomAction.class);

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "menu", MenuAction.class));


       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "var", Var.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "answer", AnswerAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "exit", ExitAction.class));

       actionBinder.addBinding().toInstance(
                new CustomAction(NAME_SPACE, "hangup", HangupAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(
               NAME_SPACE, "playaudio", PlayAudioAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(
               NAME_SPACE, "recordaudio", RecordAudioAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(
               NAME_SPACE, "getdigits", GetDigitsAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "wait", WaitAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE,
               "inputdigits", InputDigitsAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "phrase", PhraseAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "gentone" , GenToneAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "say", SayAction.class));

       actionBinder.addBinding().toInstance(
               new CustomAction(NAME_SPACE, "send", SendAction.class));

    }

}
