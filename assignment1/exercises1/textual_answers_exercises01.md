##### Exercises 1.1

1. The main method creates a LongCounter object. Then it creates and starts two threads that run concurrently,  and each increments the count field 10 million times by calling method increment.  

   What output values do you get? Do you get the expected output, i.e., 20 million?
   
   No, race condition - and a data race, as there's interleaving operation where two concurrent threads access a shared memory location `LongCount`, and increments it (`write operation`) 
2. Reduce the counts value from 10 million to 100, recompile, and rerun the code. It is now likely that you get the expected result (200) in every run.  
   
   Explain how this could be. Is it guaranteed that the output is always 200? 
   
   Fewer interleaving operation possibilities, and no it's not guaranteed - there's no locking mechanism. The time it takes between `t1.start()` and `t2.start()` might be enough to run 100 increments on `t1`.
3. The increment method in LongCounter uses the assignment: `count = count + 1` to add one to count. 
   
   This could be expressed also as count += 1 or as count++. 
   
   Do you think it would make any difference to use one of these forms instead? Why? Change the code and run it. Do you see any difference in the results for any of these alternatives?
   
   It's syntactic sugar for the same, so no. 
4. Set the value of counts back to 10 million. Use Java ReentrantLock to ensure that the output of the  program equals 20 million. Explain why your solution is correct, and why no other output is possible.  
   
   Note: In your explanation, please use the concepts and vocabulary introduced during the lecture, e.g., critical  sections, interleavings, race conditions, mutual exclusion, etc.  
   
   Note II: The notes above applies to all exercises asking you to explain the correctness of your solution.
   
   
   We get a mutual exclusion on the two thread's critical section (data access). This eliminates the race condition, and the data race. The following is the only interleavings:
   ```java
	public TestLongCounterExperiments() {
		Thread t1 = new Thread(() -> {
			glock.lock();
			for (int i=0; i<counts; i++)
				lc.increment(); // (0)
			glock.unlock();
		});
		
		Thread t2 = new Thread(() -> {
			glock.lock();
			for (int i=0; i<counts; i++)
				lc.increment(); // (1)
			glock.unlock();
		});
		
		t1.start(); t2.start();
		try { t1.join(); t2.join(); }
		catch (InterruptedException exn) {
			System.out.println("Some thread was interrupted");
		}
		System.out.println("Count is " + lc.get() + " and should be " + 2*counts);
	}
	```
5. By using the ReetrantLock in the exercise above, you have defined a critical section. Does your critical  section contain the least number of lines of code? If so, explain why. If not, fix it and explain why your new critical section contains the least number of lines of code.  
   
   Hint: Recall that the critical section should only include the parts of the program that only one thread can execute at the same time.
   ```java
	glock.lock();
	lc.increment();
	glock.unlock();
	``` 

##### Exercises 1.2

1. Write a program that creates a Printer object p, and then creates and starts two threads.
	```java
	   package exercises01;
		
		public class Printer {
		    public Printer() {
		        PrinterThread t1 = new PrinterThread();
		        PrinterThread t2 = new PrinterThread();
		
		        t1.start(); t2.start();
		        try { t1.join(); t2.join(); }
		        catch (InterruptedException exn) {
		            System.out.println("Some thread was interrupted");
		        }
		    }
		
		    public static void main(String[] args) {
		        new Printer();
		    }
		
		    public void print() {
		        System.out.print("-");
		        try {
		            Thread.sleep(50);
		        } catch (InterruptedException exn) {
		        }
		        System.out.print("|");
		    }
		
		    public class PrinterThread extends Thread {
		        public void run() {
		            while (true) {
		                print();     
		            }
		        }
		    }
		}
	```
2. Describe and provide an interleaving where this happens.
	```java
	    public void print() {
	        System.out.print("-"); // (0)
	        try {
	            Thread.sleep(50); // (1)
	        } catch (InterruptedException exn) {
	        }
	        System.out.print("|"); // (2)
	    }
	```
	
	Interleaving: `t1(0), t2(0), t1(1), t2(1), t1(2), t2(2)`
3. Use Java ReentrantLock to ensure there's no race conditions
	```java
	public void print() {
	    glock.lock();
	    System.out.print("-"); // (0)
	    try {
	        Thread.sleep(50); // (1)
	    } catch (InterruptedException exn) {
	    }
		System.out.print("|"); // (2)
		glock.unlock();
	}
	```
	
	The code uses a ReentrantLock to make sure that the printing is mutually exclusive, no threads can reach a print at the same time. Assuming that stdout flushes before the ReentrantLock unlocks, there will be no printing of the same symbol twice. 
	
	Interleaving example: `t1(0), t1(1), t1(2), t2(0), t1(1), t2(2)`

##### Exercises 1.3
1. Modify the behaviour of the Turnstile thread class so that that exactly 15000 enter the park;
	```java
	glock.lock();
	if (counter < MAX_PEOPLE_COVID) {
		counter++;
	}
	glock.unlock();
	```
2. Explain why your solution is correct, and why it always output 15000.
   
	The code uses a ReentrantLock to make sure that the counting is mutually exclusive, no threads can reach a count at the same time - aswell as comparing the count is also mutually exclusive.
##### Exercises 1.4
1. Compare the categories of concurrency
	Hidden concurrency - People sharing the computer, each user thought they had the computer for themselves - virtual/hidden concurrency, the user doesn't experience the concurrency, e.g. splitting the CPU so it appears like multiple things is running. 
	
	Example: Right now I am running about 460 processes: `ps aux | wc -l`, I only have 16 cores `nproc` - Magic!
	
	Exploitation concurrency - Multiple processing units in the same machine (I.e. 4 cores in the machine, we want to use all cores, hence use concurrency to gain anything from having multiple cores).
	
	Example: Using GPUs to crack password hashes
	
	Inherent concurrency - Involve activities that happen at the same time in the real world or consist of independent tasks that don’t depend on each other’s results.
	
	Example: Web Server, which needs to serve multiple users - or a simulation of people around a dining table all speaking with one another at the same time, the problem is inherently concurrent.
##### Exercises 1.5
1. Which operating system are you using. Is is emulated on top of another operating system?
   No, it's not emulated.
	```
	uname = Linux pc 6.8.0-64-generic #67~22.04.1-Ubuntu SMP PREEMPT_DYNAMIC Tue Jun 24 15:19:46 UTC 2 x86_64 x86_64 x86_64 GNU/Linux
	```
2. Collect this information about the hardware of the PC that you will be using for the PCPP exercises: Operating system, no of cores, size of main memory, cache architecture (including sizes of the caches).
	```
	nproc = 16
	```
	
	```
	free -m = 31394 total
	```
	
	```
	lscpu | grep -i "cache" = 
	L1d cache:                            256 KiB (8 instances)
	L1i cache:                            256 KiB (8 instances)
	L2 cache:                             8 MiB (8 instances)
	L3 cache:                             16 MiB (1 instance)
	```
3. Java gives you access to the wall-clock time of your PC: System.nanoTime(). By calling it twice before and after some code, you get information about how long it took to execute the code:
	```  
	private long start, spent;  
	start= System.nanoTime();  
	"code you are measuring"  
	spent += System.nanoTime()-start; 
	```
	
	Measure how long it takes to add the numbers 1, 2, ... 100 on your PC.
	```java
	jshell> private long start, spent;  
	   ...> start= System.nanoTime();  
	   ...> int count = 0; for (int i = 1; i < 101; i++) { count = count + i; }  
	   ...> spent += System.nanoTime()-start; System.out.println("Spent: " + spent);
	
	Spent: 442985340 // Jshell is slow
	```