public class DeadLock {
    private static Integer m1 = new Integer(0);
    private static Integer m2 = new Integer(0);

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(
                () -> {
                    synchronized (m1) {
                        System.out.println("m1 by t1");
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {}
                        synchronized (m2) {
                            System.out.println("m2 by t1");
                        }
                    }
                }
        );

        Thread t2 = new Thread(
                () -> {
                    synchronized (m2) {
                        System.out.println("m2 by t2");
                        try {
                            Thread.sleep(2000);
                        } catch (Exception e) {}
                        synchronized (m1) {
                            System.out.println("m1 by t2");
                        }
                    }
                }
        );


        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
