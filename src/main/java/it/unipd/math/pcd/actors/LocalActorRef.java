package it.unipd.math.pcd.actors;

/**
 * Created by andre on 25/07/2016.
 */
public class LocalActorRef<T extends Message> implements ActorRef<T> {
    public static AbsActorSystem aas = null;

    /**
     * Gets an actor and sends a message to him
     * @param message The message to send
     * @param to The actor to which sending the message
     */
    @Override
    public void send(T message, ActorRef to) {
        AbsActor act = aas.getActor(to);
        act.insertNewMessage(message,this);
    }


    @Override
    public int compareTo(ActorRef o) {
        return toString().compareTo(o.toString());
    }
}
