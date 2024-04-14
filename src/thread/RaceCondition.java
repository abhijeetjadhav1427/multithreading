package thread;

public class RaceCondition {

	public static void main(String[] args) throws InterruptedException {
		Shared shared = new Shared();
		
		Thread incrementingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				for(int i=0; i<10000; i++) {
					shared.increment();
				}
			}
		});
		Thread decrementingThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i=0; i<10000; i++) {
					shared.decrement();
				}
			}
		});
		
		incrementingThread.start();
		decrementingThread.start();
		
		incrementingThread.join();
		decrementingThread.join();
		
		System.out.println(shared.getData());
	}

	public static class Shared {
		public int data;

		Shared() {
			this.data = 0;
		}

		public synchronized void increment() {
			data++;
		}

		public synchronized void decrement() {
			data--;
		}
		
		public synchronized int getData() {
			return this.data;
		}
	}
}
