package thread;

public class DeadLock {
	public static void main(String[] args) {
		Shared shared = new Shared();
		
		Thread th1 = new Thread(() -> shared.method1());
		Thread th2 = new Thread(() -> shared.method2());
		
		th1.start();
		th2.start();
	}
	public static class Shared{
		private String a = "hello";
		private String b = "world";
		
		public void method1() {
			synchronized (a) {
				System.out.println("thread1 acquired lock on a");
				synchronized (b) {
					System.out.println("thread1 acquired lock on b");
				}
			}
		}
		
		public void method2() {
			synchronized (b) {
				System.out.println("thread2 acquired lock on b");
				synchronized (a) {
					System.out.println("thread2 acquired lock on a");
				}
			}
		}
	}
}
