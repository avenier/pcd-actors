package it.unipd.math.pcd.actors.impl;

import it.unipd.math.pcd.actors.AbsActorSystem;
import it.unipd.math.pcd.actors.ActorRef;
import it.unipd.math.pcd.actors.LocalActorRef;

/**
 * Created by andre on 21/07/2016.
 */
public final class ActorSystemImpl extends AbsActorSystem {

    public ActorSystemImpl(){
        LocalActorRef.aas = this;
    }

    /**
     * Creates a LOCAL actorReference
     * @param mode
     * @return
     */
    @Override
    protected ActorRef createActorReference(ActorMode mode) {
        if(mode == ActorMode.LOCAL){
            return new LocalActorRef();
        } else{
            throw new IllegalArgumentException();
        }
    }
}
