package exercises02;

public class Driver {

    public static void main(String[] args) {
        Monitor m = new Monitor();
        for (int i = 0; i < 10; i++) {
            // start a reader
            new Thread(() -> {
                m.readLock();
                // read
                m.readUnlock();
            }).start();

            System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
            System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");

            // start a writer
            new Thread(() -> {
                m.writeLock();
                // write
                m.writeUnlock();
            }).start();
            System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
            System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
        }
    }

}
