/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A map-based implementation of the actor system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActorSystem implements ActorSystem {

    /**
     * Associates every Actor created with an identifier.
     */
    protected Map<ActorRef<?>, Actor<?>> actors = new HashMap<>();

    /**
     * Creates a not fixed ThreadPool to execute the actors
     */
    protected ExecutorService exec = Executors.newCachedThreadPool();

    /**
     * Creates an actor and puts it in the threadPool
     * @param actor The type of actor that has to be created
     * @param mode The mode of the actor requested
     *
     * @return
     */
    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference, actorInstance);
            // Makes the actorIstance be executed by the ThreadPool
            exec.execute((AbsActor)actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }

    protected abstract ActorRef createActorReference(ActorMode mode);

    /**
     * Retrieves the actor from the actors Map, if the actor in not present rises a NoSuchActorException
     * @param aRef
     * @return
     */
    protected AbsActor getActor(ActorRef aRef){
        if(actors.containsKey(aRef)){
            return (AbsActor) actors.get(aRef);
        }else{
            throw new NoSuchActorException();
        }
    }

    /**
     * Imprelentation of the soft-stop on an actor
     * @param actor The actor to be stopped
     */
    @Override
    public void stop(ActorRef<?> actor) {
        //set the stop signal of the actor passed
        getActor(actor).setStopSignal();
        //remove the actor to prevent message to be sent to him
        actors.remove(actor);
    }

    /**
     * Implementation of the soft-stop of every actor
     * Cycle through every entry on the actors map and sets the signal to stop the actor and removes it from the map
     */
    @Override
    public void stop() {
        for (Map.Entry<ActorRef<?>, Actor<?>> entry : actors.entrySet()){
            //Set the stop signal for the current actor in the collection
            ((AbsActor)entry.getValue()).setStopSignal();
            //remove the actor to prevent message to be sent to him
            actors.remove(entry);
        }
    }
}