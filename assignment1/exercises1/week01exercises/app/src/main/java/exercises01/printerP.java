package exercises01;
import java.util.concurrent.locks.ReentrantLock;

public class printerP {

    public static void main(String[] args) {
        ReentrantLock glock = new ReentrantLock();
        Printer p = new Printer();
        Thread t1 = new Thread(() -> {
            while(true){
                glock.lock();
                p.print();
                glock.unlock();
            }
        });
        Thread t2 = new Thread(() -> {
            while(true){
                glock.lock();
                p.print();
                glock.unlock();
            }
        });
        t1.start(); t2.start();
    }

}

class Printer{
    public void print() {
        System.out.print("-"); // (0)
        try { 
            Thread.sleep(50); // (1)
        } catch (InterruptedException exn) { }
        System.out.print("|");} // (2)

}
