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

import javafx.util.Pair;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T>, Runnable {

    /**
     * Used to leat an actor know it has to be stopped
     */
    private AtomicBoolean stopSignal = new AtomicBoolean(false);

    /**
     * Is a linked list representing the mailbox for the messages of the actor
     */
    //private final Queue<Pair<T,ActorRef<T>>> mailBox = new LinkedList<>();
    private final LinkedList<Pair<T,ActorRef<T>>> mailBox = new LinkedList<>();
    //private final LinkedList<Pair<T,ActorRef<? extends Message>>> mailBox = new LinkedList<>();

    /**
     * Self-reference of the actor
     */
    protected ActorRef<T> self;

    /**
     * Sender of the current message
     */
    protected ActorRef<T> sender;
    //protected ActorRef<? extends Message> sender;

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    /**
     * Notify the actor that has to stop
     */
    protected final void setStopSignal(){
        stopSignal.set(true);
        synchronized (mailBox){
            mailBox.notifyAll();
        }
    }

    /**
     * Inserts a new message in the mailBox
     * @param mess The message
     * @param messSender The sender of the message
     * @return true if the message has been inserted in the mailBox
     */
    public final boolean insertNewMessage(final T mess, final ActorRef<T> messSender){
        boolean inserted = false;
        synchronized (mailBox){
            inserted = mailBox.add(new Pair<T, ActorRef<T>>(mess,messSender));
            mailBox.notifyAll();
        }
        return inserted;
    }

    /**
     * Override of the run method to process the messages in the mailBox
     */
    @Override
    public void run(){
        //TODO: check if has to be a AtomicBoolean
        boolean stopActor = false;
        while(!stopActor){
            Pair<T,ActorRef<T>> processedMessage;
            synchronized (mailBox){
                while(mailBox.isEmpty()){

                    //if the mailBox is empty and the stopSingnal has been sent the actor can be stopped
                    if(stopSignal.get()){
                        stopActor = true;
                    }
                    try{
                        mailBox.wait();
                    }catch (InterruptedException e){
                        return;
                    }
                }
                processedMessage = mailBox.remove();
            }
            sender = processedMessage.getValue();
            receive(processedMessage.getKey());
        }
    }
}
