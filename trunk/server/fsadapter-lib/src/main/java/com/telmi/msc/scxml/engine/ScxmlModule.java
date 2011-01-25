package com.telmi.msc.scxml.engine;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.telmi.msc.scxml.actions.AnswerAction;
import com.telmi.msc.scxml.actions.ExitAction;
import com.telmi.msc.scxml.actions.GenToneAction;
import com.telmi.msc.scxml.actions.GetDigitsAction;
import com.telmi.msc.scxml.actions.HangupAction;
import com.telmi.msc.scxml.actions.InputDigitsAction;
import com.telmi.msc.scxml.actions.MenuAction;
import com.telmi.msc.scxml.actions.PhraseAction;
import com.telmi.msc.scxml.actions.PlayAudioAction;
import com.telmi.msc.scxml.actions.RecordAudioAction;
import com.telmi.msc.scxml.actions.SayAction;
import com.telmi.msc.scxml.actions.SendAction;
import com.telmi.msc.scxml.actions.WaitAction;
import com.telmi.msc.scxml.sender.BasicHttpSender;
import com.telmi.msc.scxml.sender.Sender;
import com.telmi.msc.scxml.sender.SenderFactory;
import com.telmi.msc.scxml.sender.SenderFactoryImpl;
import com.telmi.msc.scxml.sender.SipReferSender;

import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.Var;

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
