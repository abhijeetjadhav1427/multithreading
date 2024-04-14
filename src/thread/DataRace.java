package thread;

import thread.DataRace.Shared;

public class DataRace {
	
	public static void main(String[] args) {
		Shared shared = new Shared();
		
		Thread th1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0; i<100000; i++) {
					shared.increment();
				}
			}
		});
		Thread th2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0; i<100000; i++) {
					shared.checkForDataRace();
				}
			}
		});
		
		th1.start();
		th2.start();
	}
	public static class Shared{
		private volatile int x = 0;
		private volatile int y = 0;
		
		public void increment() {
			x++;
			y++;
		}
		
		public void checkForDataRace() {
			if(y > x) {
				System.out.println("y > x 0 DataRace detected");
			}
		}
	}
}
