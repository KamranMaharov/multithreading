import java.util.*;

class DelayQueue<T> {
    private static class Event<T> implements Comparable<Event<T>> {
        T elem;
        long timestamp;

        Event(
            T elem,
            long timestamp
        ) {
            this.elem = elem;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Event<T> second) {
            if (this.timestamp < second.timestamp) {
                return -1;
            } else if (this.timestamp == second.timestamp) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private Object monitor;
    TreeSet<Event<T>> events;

    public DelayQueue() {
        System.out.println("DelayQueue constructor");
        this.monitor = new Object();
        this.events = new TreeSet<>();
    }

    public boolean offer(T elem, long milliSeconds) {
        synchronized (monitor) {
            events.add(new Event<T>(elem, System.currentTimeMillis() + milliSeconds));
            monitor.notifyAll();
        }
        return true;
    }

    public T remove() {
        synchronized (monitor) {
            if (events.isEmpty()) {
                try {
                    monitor.wait();
                } catch (Exception e) {
                }
            }
            Event<T> earliest = events.first();
            long diff = earliest.timestamp - System.currentTimeMillis();
            if (diff > 0) {
                try {
                    Thread.sleep(diff);
                } catch (Exception e) {
                }
            }
            return events.pollFirst().elem;
        }
    }
}

public class Main {
    public static void main(String[] args) {
        DelayQueue<Integer> dqueue = new DelayQueue<Integer>();
        Integer a = new Integer(1234);
        dqueue.offer(a, 5000); // 5 sec
        Integer x = dqueue.remove();
        System.out.println(x);
    }
}
