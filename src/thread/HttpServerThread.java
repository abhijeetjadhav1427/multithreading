package thread;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpServerThread {
	private static final String INPUT_FILE = "./resources/war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 4;
    
	public static void main(String[] args) throws IOException {
		String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));	
		startServer(text);
	}
	
	private static void startServer(String text) throws IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);
		httpServer.createContext("/search", new WordCountHandler(text));
		Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		httpServer.setExecutor(executor);
		httpServer.start();
	}
	private static class WordCountHandler implements HttpHandler {
		private String text;
		
		public WordCountHandler(String text) {
			super();
			this.text = text;
		}

		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String query = exchange.getRequestURI().getQuery();
			String []keyValue = query.split("=");
			
			String action = keyValue[0];
			String word = keyValue[1];
			
			if(!action.equalsIgnoreCase("word")) {
				exchange.sendResponseHeaders(400, 0);
				return;
			}
			long count = countWords(word);
			byte[] response = Long.toString(count).getBytes();
			
			exchange.sendResponseHeaders(200, response.length);
			OutputStream stream = exchange.getResponseBody();
			stream.write(response);
			stream.close();
		}
		
		private long countWords(String word) {
			int n = word.length();
			int []lps = new int[n];
			
			for(int i=1; i<n; i++) {
				int j = lps[i-1];
				while(j>0 && word.charAt(j) != word.charAt(i)) j = lps[j-1];
				if(word.charAt(i) == word.charAt(j)) j++;
				lps[i] = j;
			}
			
			long count = 0;
			for(int i=0,j=0; j<n && i<text.length(); i++) {
				while(j>0 && text.charAt(i) != word.charAt(j)) j = lps[j-1];
				if(text.charAt(i) == word.charAt(j)) j++;
				if(j == n) {
					count++;
					j = lps[j-1];
				}
			}
			
			return count;
		}
	}

}
