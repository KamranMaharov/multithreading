import java.util.*;

class DelayQueue<T> {
    private static class Event<E> implements Comparable<Event<E>> {
        E elem;
        long timestamp;

        Event(
            E elem,
            long timestamp
        ) {
            this.elem = elem;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Event<E> second) {
            if (this.timestamp < second.timestamp) {
                return -1;
            } else if (this.timestamp == second.timestamp) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    Thread t;
    Object eventsLock;
    TreeSet<Event<T>> events;
    Object listLock;
    List<T> list;

    public DelayQueue() {
        System.out.println("DelayQueue constructor");
        this.eventsLock = new Object();
        this.events = new TreeSet<>();
        this.listLock = new Object();
        this.list = new ArrayList<>();
        this.t = new Thread(new Runnable(){
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    if (!events.isEmpty()) {
                        synchronized (eventsLock) {
                            Event<T> earliest = events.first();

                            if (earliest.timestamp < System.currentTimeMillis()) {
                                synchronized (listLock) {
                                    list.add(earliest.elem);
                                }
                                events.pollFirst();
                            }
                        }
                    }
                }
            }
        });
        this.t.start();
    }

    public boolean offer(T elem, long milliSeconds) {
        synchronized (eventsLock) {
            return events.add(new Event<T>(elem, System.currentTimeMillis() + milliSeconds));
        }
    }

    public boolean remove(T elem) {
        synchronized (listLock) {
            return list.remove(elem);
        }
    }

    public int size() {
        synchronized (listLock) {
            return list.size();
        }
    }

    public void stop() {
        t.interrupt();
        try {
            t.join();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


public class Main {
    public static void main(String[] args) {
        DelayQueue<Integer> dqueue = new DelayQueue<Integer>();
        Integer a = new Integer(1234);
        dqueue.offer(a, 5000); // 5 sec
        System.out.println(dqueue.size());
        System.out.println(dqueue.remove(a));
        try {
            Thread.sleep(5200); // sleep 5.2 sec, until integer will be available in queue
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(dqueue.size());
        System.out.println(dqueue.remove(a));
        System.out.println(dqueue.size());
        dqueue.stop();
    }
}
