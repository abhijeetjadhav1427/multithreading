package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReenterantReadWriteLocks {
	public static final int HIGHEST_PRICE = 1000;
	public static void main(String[] args) throws InterruptedException {
		InventoryDatabase inventoryDatabase = new InventoryDatabase();
		
		Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }
        
        Thread writer = new Thread(() -> {
        	while(true) {
        		inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        		inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));
        		try {
        			Thread.sleep(10);
        		}
        		catch(InterruptedException e) {		
        		}
        	}
        });
        
        writer.setDaemon(true);
        writer.start();
        
        int numberOfReaderThreads = 7;
        List<Thread> readers = new ArrayList<>();
        
        for (int readerIndex = 0; readerIndex<numberOfReaderThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 100000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }
        
        long startReadingTime = System.currentTimeMillis();
        for (Thread reader : readers) {
            reader.start();
        }

        for (Thread reader : readers) {
            reader.join();
        }

        long endReadingTime = System.currentTimeMillis();

        System.out.println(String.format("Reading took %d ms", endReadingTime - startReadingTime));
	}
	public static class InventoryDatabase{
		private TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
		private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		private Lock readLock = lock.readLock();
		private Lock writeLock = lock.writeLock();
		
//		private ReentrantLock reentrantLock = new ReentrantLock();
		
		public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
//			reentrantLock.lock();
			readLock.lock();
			try {
				Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
				Integer toKey = priceToCountMap.floorKey(upperBound);
				
				if(fromKey == null || toKey == null) {
					return 0;
				}
				
				NavigableMap<Integer,Integer> mp = priceToCountMap.subMap(fromKey, true, toKey, true);
				int sum = 0;
				for(int numberOfItemsForPrice: mp.values()) {
					sum += numberOfItemsForPrice;
				}
				
				return sum;
			}
			finally {
//				reentrantLock.unlock();
				readLock.unlock();
			}
		}
		
		public void addItem(int price) {
//			reentrantLock.lock();
			writeLock.lock();
			try {
				Integer numberOfItemsForPrice = priceToCountMap.get(price);
				if(numberOfItemsForPrice == null) {
					priceToCountMap.put(price, 1);
				}
				else {
					priceToCountMap.put(price, numberOfItemsForPrice+1);
				}
			}
			finally {
//				reentrantLock.unlock();
				writeLock.unlock();
			}
		}
		
		public void removeItem(int price) {
//			reentrantLock.lock();
			writeLock.lock();
			try {
				Integer numberOfItemsForPrice = priceToCountMap.get(price);
				if(numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
					priceToCountMap.remove(price);
				}
				else {
					priceToCountMap.put(price, numberOfItemsForPrice-1);
				}
			}
			finally {
//				reentrantLock.unlock();
				writeLock.unlock();
			}
		}
	}
}
