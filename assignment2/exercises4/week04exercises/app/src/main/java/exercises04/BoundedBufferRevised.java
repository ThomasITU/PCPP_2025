package exercises04;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

interface BoundedBufferRevisedInteface<T> {
    public T take() throws Exception;
    public void insert(T elem) throws Exception;
}


// is this thread safe?
public class BoundedBufferRevised<T> implements BoundedBufferRevisedInteface<T>{
    
    private final Semaphore free_buffer_space; // initialized with buffer size
    private final Semaphore current_list_size;  // # of elements in the list, 
                                                // NB:  Release() increments the size of permits
                                                //      Acquire() decrements the size of permits
    private final List<T> buffer;

    public BoundedBufferRevised(int capacity){
        buffer = new LinkedList<>();
        free_buffer_space = new Semaphore(capacity, true);
        current_list_size = new Semaphore(0, true);
    }

    @Override
    public T take() throws Exception {
        current_list_size.acquire(); // reduce the # of elements free/available for consumers
        T item = buffer.remove(0);
        free_buffer_space.release(); // increase buffer space available for producers  
        return item;
    }

    @Override
    public void insert(T elem) throws Exception {
        free_buffer_space.acquire(); // reduce the buffer space avaialbe for producers
        buffer.add(elem);
        current_list_size.release(); // increase the # of elements free/available for cosumers
    }    
}