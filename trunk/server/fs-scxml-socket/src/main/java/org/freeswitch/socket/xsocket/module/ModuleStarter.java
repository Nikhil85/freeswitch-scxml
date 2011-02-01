package org.freeswitch.socket.xsocket.module;

import com.google.inject.Injector;
import javax.inject.Inject;
import org.freeswitch.socket.xsocket.XsocketTcpServerImpl;
import org.ops4j.peaberry.activation.Start;
import org.ops4j.peaberry.activation.Stop;

/**
 *
 * @author joe
 */
public class ModuleStarter {
    
    private Injector injector;
   
    @Inject
    public ModuleStarter(Injector injector) {
        this.injector = injector;
    }
    
    @Start
    public void start(){
      injector.getInstance(XsocketTcpServerImpl.class).startServer();    
    }
    
    @Stop
    public void stop(){
      injector.getInstance(XsocketTcpServerImpl.class).stopServer();    
    }
    
}
