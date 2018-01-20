import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

//monitor mechanism in Java
public class ConditionVariable {
    private static final int BOUND = 20;
    private static final Object listLock = new Object();
    private static List<Integer> list = new ArrayList<>();
    private static AtomicLong count = new AtomicLong();

    private static class Producer {
        public Producer(List<Integer> list) {
        }

        public void produce() {
            try {
                for (int i = 1; i <= 10000000; ++i) {
                    synchronized (listLock) {
                        while (list.size() == BOUND) {
                            listLock.wait();
                        }
                        list.add(i);
                        listLock.notify();
                    }
                }
            } catch (Exception ex) {}
        }
    }

    public static class Consumer {
        public Consumer(List<Integer> list) {
        }

        public void consume() {
            try {
                for (int i = 1; i <= 10000000; ++i) {
                    synchronized (listLock) {
                        while (list.isEmpty()) {
                            listLock.wait();
                        }
                        list.remove(0);
                        count.incrementAndGet();
                        listLock.notify();
                    }
                }
            } catch (Exception e) {}
        }
    }

    public static void main(String[] args) throws Exception {
        long t1 = System.currentTimeMillis();
        Producer p = new Producer(list);
        Consumer c = new Consumer(list);

        List<Thread> tps = new ArrayList<>();
        List<Thread> tcs = new ArrayList<>();
        for (int i=1; i<=5; ++i) {
            Thread tp = new Thread(p::produce);
            Thread tc = new Thread(c::consume);
            tps.add(tp);
            tcs.add(tc);

            tp.start();
            tc.start();
        }

        for (int i=0; i<5; ++i) {
            tps.get(i).join();
            tcs.get(i).join();
        }
        long t2 = System.currentTimeMillis();
        System.out.println((t2-t1)/1000 + " seconds elapsed.");
        System.out.println("count = " + count);
    }
}
