import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {
    private final List<Account> accounts = new ArrayList<>();
    ReentrantReadWriteLock lock=new ReentrantReadWriteLock();

    int newAccount(int balance) {
        lock.writeLock().lock();
        try{
            int accountId = accounts.size();
            accounts.add(new Account(accountId, balance));
            return accountId;
        }finally{
            lock.writeLock().unlock();
        }
    }

    Account getAccount(int accountId) {
        lock.readLock().lock();
        try{
            return accounts.get(accountId);
        }finally{
            lock.readLock().unlock();
        }
    }
}
