##### Exercises 2.1

1. Use Java Intrinsic Locks (i.e., synchronized) to implement a monitor ensuring that the access to the shared resource by reader and writer threads is according to the specification above.

	```java
	package exercises02;

	import java.util.Scanner;
	
	public final class CustomMonitor {
	    private int activeReaders = 0;
	    private int activeWriters = 0;
	    private int waitingWriters = 0;
	
	    public synchronized void readLock() throws InterruptedException {
	        while (activeWriters > 0 || waitingWriters > 0) { // write priority
	            wait();
	        }
	
	        activeReaders++;
	    } 
	
	    public synchronized void readUnlock() throws InterruptedException {
	        activeReaders--;
	        if (activeReaders == 0) {
	            notifyAll();
	        }
	    } 
	
	    public synchronized void writeLock() throws InterruptedException {
	        waitingWriters++;
	
	        try {
	            while (activeReaders > 0 || activeWriters > 0) {
	                wait();
	            }
	            activeWriters = 1;
	        } finally {
	            waitingWriters--;
	        }
	    } 
	
	    public synchronized void writeUnlock() throws InterruptedException {
	        activeWriters = 0;
	        notifyAll();;
	    } 
	
	    public static void main(String[] args) {
	        CustomMonitor m = new CustomMonitor();
	
	        String toWrite = "deadbeef";
	
	        try {
	            try {
	                m.writeLock();
	                String s = toWrite;
	            } finally {
	                m.writeUnlock();
	            }
	        } catch (InterruptedException ie) {
	            System.out.println("Oops");
	        }
	    }
	}
	```

2. Is your solution fair towards writer threads? In other words, does your solution ensure that if a writer  thread wants to write, then it will eventually do so? If so, explain why. If not, modify part 1. so that  your implementation satisfies this fairness requirement, and then explain why your new solution satisfies  the requirement

**Yes** the solution is fair to writer threads, because of the variable "waitingWriters", meaning that if a writer needs to write the readers will have to wait, as such the writers have priority.


##### Exercise 2.2
1. Does your monitor implementation contain any condition variables? If you answer yes, explain how many  condition variables your monitor uses, how they are used in the monitor, and what method calls are used to  access them. If you answer no, explain why condition variables are not used in the monitor.
	**There's** one implicit condition variable which can be interacted with through wait(), notify(), and notifyAll().
	
**Yes** it can run forever, because there's not necessarily cache coherence. When a lock is used, flushing to main memory occurs. Here we can not guarantee it, and as such it's possible it will run forever. You can use volatile, because then the variable if flushed into main memory, meaning that threads in other cores can access the updated variables.

2. Use Java Intrinsic Locks (synchronized) on the methods of the MutableInteger to ensure that thread t always terminates. Explain why your solution prevents thread t from running forever
	```java
	class MutableInteger {
	    private int value = 0;
	    public synchronized void set(int value) {
	        this.value = value;
	    }
	    public synchronized int get() {
	        return value;
	    }
	}
	```

3. Would thread t always terminate if get() is not defined as synchronized? Explain your answer.
	**The** `get` will use a cached value, probably cpu caches or in a register. These are not updated when the set is called, and as such the code never terminates.
   
4. Remove all the locks in the program, and define value in MutableInteger as a volatile variable.  Does thread t always terminate in this case? Explain your answer.
	**You** can use volatile, because then the variable if flushed into main memory, thus making it visible to the other threads.

##### Exercise 2.3
1. Execute the program several times. Show the results you get. Are there any race conditions?
	```
	Sum is 1481030.000000 and should be 2000000.000000
	; ./do.py exercises02 TestLocking0
	Sum is 1552257.000000 and should be 2000000.000000
	; ./do.py exercises02 TestLocking0
	Sum is 1526859.000000 and should be 2000000.000000
	```

2. Explain why race conditions appear when t1 and t2 use the Mystery object. Hint: Consider (a) what it  means for an instance method to be synchronized, and (b) what it means for a static method to be synchronized.

	**The** locks are on the class monitor, and the instance method. These are two different locks, and as such they don't have an impact on each other. To fix this we can change so they are both either static or non-static.

3. Implement a new version of the class Mystery so that the execution of t1 and t2 does not produce race conditions, without changing the modifiers of the field and methods in the Mystery class. That is, you  should not make any static field into an instance field (or vice versa), and you should not make any static method into an instance method (or vice versa).

	```java
	class Mystery {
	    static ReentrantLock glock = new ReentrantLock();
	
	    private static double sum = 0;
	
	    public static synchronized void addStatic(double x) {
	        glock.lock();
	        sum += x;
	        glock.unlock();
	    }
	
	    public synchronized void addInstance(double x) {
	        glock.lock();
	        sum += x;
	        glock.unlock();
	    }
	
	    public static synchronized double sum() {
	        return sum;
	    }
	}
	```

	**The** two additions are mutually exclusive so interleavings wont happen, and such there is no data race.

4. Note that the method sum() also uses an intrinsic lock. Is the use of this intrinsic lock on sum() necessary for this program? In other words, would there be race conditions if you remove the modifier synchronized from sum() (assuming that you have fixed the race conditions in 3.)

	**There** are no race conditions without the synchronized due to the locking in this program, and the fact that sum is static so that it's in the same place, so caching will not affect the program.