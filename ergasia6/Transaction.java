import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

class Transaction implements Runnable {
	private final List<Operation> operations = new CopyOnWriteArrayList<>();
	private boolean closed = false;
	private final ReentrantLock lock = new ReentrantLock();

	void add(Operation operation) {
		lock.lock();
		try {
			if (closed) return;
			operations.add(operation);
		} finally {
			lock.unlock();
		}
	}

	void close() {
		lock.lock();
		try {
			closed = true;
		} finally {
			lock.unlock();
		}
	}

	public void run() {
		lock.lock();
		try {
			if (!closed) return;
			for (Operation operation : operations) {
				operation.run();
			}
		} finally {
			lock.unlock();
		}
	}
}
