# Exercise 4.1: Bounded Buffer

```java
interface BoundedBufferInterface<T> {
    public T take() throws Exception;
    public void insert(T elem) throws Exception;
}
```

### Mandatory

#### 4.1.1. Implement a class `BoundedBuffer<T>` as described above using only Java Semaphore for synchronization, i.e., Java Lock or intrinsic locks, (`synchronized`) cannot be used.

```java
public class BoundedBuffer<T> implements BoundedBufferInteface<T>{
    

    private final Semaphore free_buffer_space; // initialized with buffer size
    private final Semaphore mutex; // for thread-safety
    private final Semaphore current_list_size;  // # of elements in the list, 
                                                // NB:  Release() increments the size of permits
                                                //      Acquire() decrements the size of permits
    
    private final List<T> buffer;

    public BoundedBuffer(int capacity){
        buffer = new LinkedList<>();
        free_buffer_space = new Semaphore(capacity, true);
        mutex = new Semaphore(1, true);
        current_list_size = new Semaphore(0, true);
    }

    public T take() throws Exception {
        current_list_size.acquire(); // reduce the # of elements free/available for consumers
        mutex.acquire(); 
        T item = buffer.remove(0);
        mutex.release();
        free_buffer_space.release(); // increase buffer space available for producers  
        return item;
    }

    public void insert(T elem) throws Exception {
        free_buffer_space.acquire(); // reduce the buffer space avaialbe for producers
        mutex.acquire();
        buffer.add(elem);
        mutex.release();
        current_list_size.release(); // increase the # of elements free/available for cosumers
    }    
}

```

#### 4.1.2. Explain why your implementation of `BoundedBuffer<T>` is thread-safe. Hint: Recall our definition of thread-safe class, and the elements to identify/consider in analyzing thread-safe classes (see slides).

From slides: 
*A class is said to be thread-safe if and only if
no concurrent execution of
method calls or field accesses (read/write)
result in data races on the fields of the class*

Considering practical elements such as:
* Class state
* Escaping
* (Safe) publication
* Immutability
* Mutual exclusion

Class state: The only manipulate class own class state, no object references as parameters or fields from parent class

Escaping: None of the variables are visible or exposed to callers, achievied by using the private keyword and no variables in return calls.

(Safe) publication: using the final keyword to ensure safe initiliazition, final values does not remain in the cache after constructor finishes 

Immutability: NA

Mutual exclusion: The class ensures mutual exlusiong from around the critical section for the shared state, i.e. sempaphore(1) with a single permit acts as a lock for the critical section 



#### 4.1.3. Is it possible to implement `BoundedBuffer<T>` using Barriers? Explain your answer.

No, i don't think so, or at least highly ineffective. Barriers are used to have a set number of threads wait before computing; the producer-consumer problem does not fit this pattern.


### Challenging

#### 4.1.4. One of the two constructors to Semaphore has an extra parameter named `fair`. Explain what it does, and explain if it matters in this example. If it does not matter in this example, find an example where it does matter.


The `fair` parameter ensures FIFO ordering for the threads waiting for the permit. It doesn't matter in this example, but it might, since it avoids starvation.

---

# Exercise 4.2

Consider a `Person` class with attributes:  
- `id` (long)
- `name` (String)
- `zip` (int)
- `address` (String)

## Functionality

- It must be possible to change zip and address together.
- It is not necessary to be able to change name—but it is not forbidden.
- The id cannot be changed.
- It must be possible to get the values of all fields.
- There must be a constructor for `Person` that takes no parameters. When calling this constructor, each new instance of Person gets an id one higher than the previously created person. In case the constructor is used to create the first instance of Person, then the id for that object is set to 0.
- There must be a constructor for `Person` that takes as parameter the initial id value from which future ids are generated. In case the constructor is used to create the first instance of Person, the initial parameter must be used. For subsequent instances, the parameter must be ignored and the value of the previously created person must be used (as stated in the previous requirement).

### Mandatory

1. **Implement a thread-safe version of `Person` using Java intrinsic locks (`synchronized`).** Hint: The Person class may include more attributes than those stated above; including static attributes.
2. **Explain why your implementation of the Person constructor is thread-safe, and why subsequent accesses to a created object will never refer to partially created objects.**
3. **Implement a program that starts several threads and each of the started threads creates and uses instances of the Person class. Run the program once. Did you observe any error in the behavior of the program?**
4. **Assuming that you did not find any errors when running 3. Is your experiment in 3 sufficient to prove that your implementation is thread-safe?**

---

<!-- 
# Exercise 4.3: Monitors and Synchronization Primitives

Monitors are the most general synchronization primitive (that we have seen). In this exercise, your task is to use monitors to implement other synchronization primitives.

### Challenging

1. **Implement a Semaphore thread-safe class using Java Lock.** Use the description of semaphore provided in the slides.
2. **Implement a (non-cyclic) Barrier thread-safe class using your implementation of Semaphore above.** Use the description of Barrier provided in the slides. -->
