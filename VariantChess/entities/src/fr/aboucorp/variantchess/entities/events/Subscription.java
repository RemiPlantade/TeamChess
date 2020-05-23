package fr.aboucorp.variantchess.entities.events;

class Subscription implements Comparable<Subscription>{
    public final fr.aboucorp.variantchess.entities.events.GameEventSubscriber subscriber;
    private final int priority;

    public Subscription(GameEventSubscriber subscriber, int priority) {
        this.subscriber = subscriber;
        this.priority = priority;
    }


    @Override
    public int compareTo(Subscription sub) {
        return Integer.compare(this.priority,sub.priority);
    }
}
