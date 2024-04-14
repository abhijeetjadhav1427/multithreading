package thread;

import java.util.ArrayList;
import java.util.Random;

public class HackerThreads {
	private static final int MAX_PASSWORD = 1000;
	
	public static void main(String[] args) {
		Random random = new Random();
		Volt volt = new Volt(random.nextInt(MAX_PASSWORD));
		
		ArrayList<Thread> threads = new ArrayList<>();
		threads.add(new AscendingThread(volt));
		threads.add(new DescendingThread(volt));
		threads.add(new PoliceThread());
		
		threads.forEach(thread -> thread.start());
	}
	
	private static class Volt {
		private int password;

		public Volt(int pass) {
			password = pass;
		}

		public boolean isPassword(int password) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return this.password == password;
		}
	}

	private static abstract class HackerThread extends Thread {
		private Volt volt;

		public HackerThread(Volt volt) {
			this.volt = volt;
			this.setName(this.getClass().getSimpleName());
			this.setPriority(Thread.MAX_PRIORITY);
		}

		@Override
		public synchronized void start() {
			// TODO Auto-generated method stub
			super.start();
		}
	}
	private static class AscendingThread extends HackerThread{

		public AscendingThread(Volt volt) {
			super(volt);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			for(int i=0; i<MAX_PASSWORD; i++) {
				if(super.volt.isPassword(i)) {
					System.out.println("Password guessed by " + this.currentThread().getName());
					System.exit(0);
				}
			}
		}
	}
	private static class DescendingThread extends HackerThread{

		public DescendingThread(Volt volt) {
			super(volt);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			for(int i=MAX_PASSWORD-1; i>=0; i--) {
				if(super.volt.isPassword(i)) {
					System.out.println("Password guessed by " + this.currentThread().getName());
					System.exit(0);
				}
			}
		}
	}
	
	private static class PoliceThread extends Thread{

		@Override
		public void run() {
			for(int i=10; i>0; i--) {
				System.out.println(i);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Catched");
			System.exit(0);
		}
		
	}
}
