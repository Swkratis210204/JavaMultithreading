import java.util.concurrent.locks.ReentrantReadWriteLock;

class Account {
    private final int ID;
    private volatile int balance; 
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    Account(int id, int balance) {
        this.ID = id;
        this.balance = balance;
    }

    int getId() {
        return ID;
    }

    int getBalance() {
        return balance;
    }

    void setBalance(int balance) {
        lock.readLock().lock();
        try {
            this.balance = balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    void adjustBalance(int amount) {
        lock.writeLock().lock();
        try {
            this.balance += amount;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
