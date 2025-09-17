// For week 4
// raup@itu.dk * 18/09/2021
package exercises04;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;


interface BoundedBufferInteface<T> {
    public T take() throws Exception;
    public void insert(T elem) throws Exception;
}

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
