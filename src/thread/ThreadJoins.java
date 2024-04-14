package thread;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadJoins {
	public static void main(String[] args) {
		List<Long> nums = Arrays.asList(100000L, 25003L, 5368L, 1451L);
		
		List<FactorialCal> threads = new ArrayList<>();
		nums.forEach(num -> {
			threads.add(new FactorialCal(num));
		});
		
		for(Thread th: threads) {
			th.start();
		}
		
		for(Thread th: threads) {
			try {
				th.join(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(int i=0; i<nums.size(); i++) {
			FactorialCal th = threads.get(i);
			if(th.isFinished) {
				System.out.println("Factorial of " + th.inputNum + " is " + th.result);
			}
			else {
				System.out.println(th.inputNum + " is still in progress!!");
			}
		}
	}
	
	private static class FactorialCal extends Thread{
		private long inputNum;
		private BigInteger result;
		private boolean isFinished;
		
		private FactorialCal(Long num) {
			inputNum = num;
			result = BigInteger.ZERO;
			isFinished = false;
		}
		
		@Override
		public void run() {
			factorial(inputNum);
			this.isFinished = true;
		}
		
		private void factorial(long n) {
			result = BigInteger.ONE;
			
			for(long i=n; i>1; i--) {
				result = result.multiply(new BigInteger(Long.toString(i)));
			}
		}
	}
}
