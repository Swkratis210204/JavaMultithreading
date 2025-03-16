// Author: Sokratis Vissarion Giannoutsos

import java.math.BigInteger;
import java.util.Scanner;

public class Factorizer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Take the product as input
        System.out.println("Give the product of two prime numbers:");
        BigInteger product = scanner.nextBigInteger();

        // Check if the input is prime; if yes, no factorization is possible
        if (isPrime(product)) {
            System.out.println("No factorization possible");
            scanner.close();
            return;
        }

        // Take the number of threads as input
        System.out.println("Give the number of threads:");
        int numOfThreads = scanner.nextInt();
        scanner.close();

        // Start timer
        long startTime = System.currentTimeMillis();

        // Start multi-threaded factorization
        new FactorizationThreads(product, numOfThreads).startThreads();

        // Stop timer and display execution time
        System.out.println("Execution Time: " + (System.currentTimeMillis() - startTime) + " ms");
    }

    //Attempts to find two factors of the given product using a step-wise approach.
    public static BigInteger[] findPrime(BigInteger min, BigInteger product, BigInteger step) {
        BigInteger max = product.sqrt().add(BigInteger.ONE);
        for (BigInteger number = min; number.compareTo(max) <= 0; number = number.add(step)) {
            if (Thread.currentThread().isInterrupted()) return null;
            if (product.mod(number).equals(BigInteger.ZERO)) {
                return new BigInteger[]{number, product.divide(number)};
            }
        }
        return null;
    }
    
    //Determines if a number is prime.
    public static boolean isPrime(BigInteger num) {
        if (num.compareTo(BigInteger.TWO) < 0) return false;
        if (num.equals(BigInteger.TWO)) return true;
        if (num.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        BigInteger sqrt = num.sqrt().add(BigInteger.ONE);
        for (BigInteger i = BigInteger.valueOf(3); i.compareTo(sqrt) <= 0; i = i.add(BigInteger.TWO)) {
            if (num.mod(i).equals(BigInteger.ZERO)) return false;
        }
        return true;
    }
}

class FactorizationThreads {
    private final Thread[] threads;
    private final BigInteger product;
    private volatile boolean found = false;

    public FactorizationThreads(BigInteger product, int numOfThreads) {
        this.threads = new Thread[numOfThreads];
        this.product = product;
    }

    //Starts multiple threads to find prime factors of the given product.
    public void startThreads() {
        for (int i = 0; i < threads.length; i++) {
            if (found) break; 

            BigInteger threadStart = BigInteger.valueOf(i + 2);
            BigInteger step = BigInteger.valueOf(threads.length);
            int threadId = i;

            threads[i] = new Thread(() -> {
                BigInteger[] factors = Factorizer.findPrime(threadStart, product, step);
                if (factors != null) {
                    synchronized (this) {
                        if (!found) {
                            found = true;
                            System.out.println("Thread " + threadId + " found factors: " + factors[0] + " and " + factors[1]);
                            stopAllThreads();
                        }
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try { if (thread != null) thread.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // Interrupts all running threads once factors are found.
    private void stopAllThreads() {
        for (Thread thread : threads) {
            if (thread != null) thread.interrupt();
        }
    }
}
