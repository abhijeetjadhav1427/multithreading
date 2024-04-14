package thread.thread.per.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IOBoundExecutor {
	private static final int NUMBER_OF_TASKS = 10000;
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		performTasks();
		
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
	}
	
	private static void performTasks() {
//		ExecutorService executorService = Executors.newCachedThreadPool();
		ExecutorService executorService = Executors.newFixedThreadPool(1000);
		
		for(int i=0; i<NUMBER_OF_TASKS; i++) {
			executorService.submit(() -> {
				method();
			});
		}
		
		
		executorService.shutdown();
	}
	
	private static void method() {
		System.out.println(Thread.currentThread());
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
