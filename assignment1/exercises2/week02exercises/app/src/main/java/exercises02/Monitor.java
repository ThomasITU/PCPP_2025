package exercises02;

public class Monitor {

    int readers = 0;
    boolean writer = false;
    static Object o = new Object();

    public void readLock() {
        synchronized (o) {
            try {
                while (writer)
                    o.wait();
                readers++;
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                readUnlock();
            }
        }
    }

    public void readUnlock() {
        synchronized (o) {
            try {
                readers--;
                if (readers == 0)
                    o.notifyAll();
            } finally {
            }
        }
    }

    public void writeLock() {
        synchronized (o) {
            try {
                while (writer)
                    o.wait();
                writer = true;
                while (readers > 0) {
                    o.wait();
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                writeUnlock();
            }
        }
    }

    public void writeUnlock() {
        synchronized (o) {
            try {
                writer = false;
                o.notifyAll();
            } finally {
            }
        }
    }

}
