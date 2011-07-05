package org.freeswitch.scxml.test;

import org.freeswitch.scxml.test.actions.CountTest;
import org.freeswitch.scxml.test.actions.GetDigitsTest;
import org.freeswitch.scxml.test.actions.InputDigitsTest;
import org.freeswitch.scxml.test.actions.MenuTest;
import org.freeswitch.scxml.test.actions.PhraseTest;
import org.freeswitch.scxml.test.actions.PlayAudioTest;
import org.freeswitch.scxml.test.actions.RecordAudioTest;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import junit.framework.Assert;
import org.junit.runner.Result;
import org.ops4j.pax.exam.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 *
 * @author jocke
 */
@RunWith(JUnit4TestRunner.class)
public class TestSuiteIT {

    @Inject
    private BundleContext context;

    @Test
    public void runSuite() throws Exception {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(System.out));
        Result run = junit.run(
                MenuTest.class,
                GetDigitsTest.class,
                InputDigitsTest.class,
                PhraseTest.class,
                PlayAudioTest.class,
                CountTest.class,
                RecordAudioTest.class
                /** CallTest.class*/);
        Assert.assertTrue(run.wasSuccessful());
    }

    @Configuration
    public static Option[] configuration() throws Exception {
        return options(
                junitBundles(),
                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO"),
                mavenBundle().groupId("org.xsocket").artifactId("xSocket").version("2.8.14"),
                mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.collections").version("3.2.1"),
                mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.beanutils").version("1.8.0"),
                mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.digester").version("1.8.1"),
                mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-service").version("1.6.1"),
                mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-api").version("1.6.1"),
                mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin").version("1.2.8"),
                wrappedBundle(mavenBundle().groupId("commons-jexl").artifactId("commons-jexl").version("1.1")),
                wrappedBundle(mavenBundle().groupId("xalan").artifactId("xalan").version("2.7.0")),
                wrappedBundle(mavenBundle().groupId("xml-apis").artifactId("xml-apis").version("2.0.2")),
                bundle("http://freeswitch-scxml.googlecode.com/files/commons-scxml-0.9.jar"),
                bundle("http://freeswitch-scxml.googlecode.com/files/org-openide-util-lookup.jar"),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.adapter.api").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.socket").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.adapter").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.application").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.application.api").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.lookup").versionAsInProject(),
                mavenBundle("org.freeswitch.scxml", "org.freeswitch.scxml.config").versionAsInProject(),
                felix());
    }

    /**
     * @return the context
     */
    public BundleContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(BundleContext context) {
        this.context = context;
    }
}
