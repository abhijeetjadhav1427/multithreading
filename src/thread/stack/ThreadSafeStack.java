package thread.stack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class ThreadSafeStack {
	
	public static void main(String[] args) throws InterruptedException {
		LockFreeStack stack = new LockFreeStack();
//		StandardStack stack = new StandardStack();
		
		Random random = new Random();
		
		for(int i=0; i<10000; i++) {
			stack.push(random.nextInt());
		}
		
		List<Thread> threads = new ArrayList<>();
		
		int pushingThreads = 2;
		int poppingThreads = 2;
		
		for(int i=0; i<pushingThreads; i++) {
			Thread thread = new Thread(()->{
				while(true) {
					stack.push(random.nextInt());
				}
			});
			
			thread.setDaemon(true);
			threads.add(thread);
		}
		
		for(int i=0; i<poppingThreads; i++) {
			Thread thread = new Thread(() -> {
				while(true) {
					stack.pop();
				}
			});
			
			thread.setDaemon(true);
			threads.add(thread);
		}
		
		for(Thread thread: threads) {
			thread.start();
		}
		
		Thread.sleep(10000);
		
		System.out.println(stack.getCounter() + " Operations performed");
	}

	public static class LockFreeStack { // 155252383 Operations performed
		private AtomicReference<StackNode<Integer>> head = new AtomicReference<>(null);
		private AtomicInteger counter = new AtomicInteger(0);

		public void push(int value) {
			StackNode<Integer> nextHead = new StackNode<>(value);

			while (true) {
				StackNode<Integer> currentHead = head.get();
				nextHead.next = currentHead;

				if (head.compareAndSet(currentHead, nextHead)) {
					break;
				} else {
					LockSupport.parkNanos(1);
				}
			}

			counter.incrementAndGet();
		}

		public int pop() {
			StackNode<Integer> currentHead = head.get();

			while (currentHead != null) {
				StackNode<Integer> nextHead = currentHead.next;
				if (head.compareAndSet(currentHead, nextHead)) {
					break;
				} else {
					LockSupport.parkNanos(1);
					currentHead = head.get();
				}
			}

			counter.incrementAndGet();
			return currentHead == null ? -1 : currentHead.value;
		}
		
		public int getCounter() {
			return counter.get();
		}
	}
										
	public static class StandardStack { // 140615459 Operations performed
		private StackNode<Integer> head = null;
		private int counter = 0;

		public synchronized void push(int value) {
			StackNode<Integer> nextHead = new StackNode(value);

			nextHead.next = head;
			head = nextHead;
			counter++;
		}

		public synchronized int pop() {
			if (head == null) {
				counter++;
				return -1;
			}

			int value = head.value;
			head = head.next;
			counter++;

			return value;
		}

		public int getCounter() {
			return counter;
		}
	}

	public static class StackNode<T> {
		public T value;
		public StackNode<T> next;

		public StackNode(T value) {
			this.value = value;
			this.next = null;
		}
	}
}
