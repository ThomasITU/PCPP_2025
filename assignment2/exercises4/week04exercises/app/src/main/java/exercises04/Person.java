package exercises04;

public class Person {
    private static long nextId = 0;
    private static boolean initialIdSet = false;
    private static final Object idLock = new Object();  // use object as an intrinsic shared lock
                                                        // static final keyword ensures one instance only is created
    private final long id;
    private String name;
    private int zip;
    private String address;


    private long setId(long initialId) {
        synchronized (idLock) { // use the shared lock to ensure thread safety*
            if (!initialIdSet) {
                nextId = initialId;
                initialIdSet = true;
            } else {
                nextId++;
            }
            return nextId;
        }
    }

    public Person() {
        this.id = setId(0);
    }

    
    public Person(long initialId) {
        this.id = setId(initialId);
    }


    // setters
    public synchronized void setName(String name){
        this.name = name;
    }

    public synchronized void setZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    // getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getZip() {
        return zip;
    }

    public String getAddress() {
        return address;
    }


    public static long getNextId() {
        return nextId;
    }

    public static boolean isInitialIdSet() {
        return initialIdSet;
    }

    public static Object getIdlock() {
        return idLock;
    }

    // driver code 
    public static void main(String[] args) throws InterruptedException {
        int threads = 2;
        int peoplePerThread = 10;

        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < peoplePerThread; j++) {
                    Person p = new Person(1);
                    p.setName("t" + Thread.currentThread().getId() + "-Person-" + p.getId());
                    p.setZipAndAddress(2300 + j, "Address " + j);
                    System.out.println(p.getName()+ " personId: " + p.getId()  +
                            ", zip: " + p.getZip() + ", " + p.getAddress());
                }
                System.out.println("t" + Thread.currentThread().getId() + " finished");
            }).start();;
        }

        System.out.println("main finished.");
    }
}


