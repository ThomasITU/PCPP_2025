package exercises01;


public class AddNumbers {
    
    public static void main(String[] args) {
        long start = 0L, spent = 0L;
        int sum = 0;
        start = System.nanoTime();
        for(int i = 1; i <= 100; i++){
            sum += i;
        }

        spent += System.nanoTime() - start;

        System.out.printf("Time spent: %s ns, res: %s", spent, sum);

    }
}
