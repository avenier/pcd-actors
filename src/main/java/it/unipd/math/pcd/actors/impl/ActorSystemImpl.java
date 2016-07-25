package it.unipd.math.pcd.actors.impl;

//import it.unipd.math.pcd.actors.*;
import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.Actor;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.LocalActorRef;

import java.util.HashMap;
import java.util.concurrent.Executors;

/**
 * Created by andre on 21/07/2016.
 */
public final class ActorSystemImpl extends AbsActorSystem {

    public ActorSystemImpl(){
        LocalActorRef.aas = this;
        actors = new HashMap<ActorRef<?>, Actor<?>>();
        exec = Executors.newCachedThreadPool();
    }

    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if(mode == ActorMode.LOCAL){
            return new LocalActorRef();
        } else{
            throw new IllegalArgumentException();
        }
    }

    /*
    public <T extends Message> AbsActor<T> getActorPublic(ActorRef<T> ref){
        return getActor(ref);
    }
    */
}
