// Sokratis-Vissarion Giannoutsos, 2020-02-04.


public class Program {
	final static int NUM_WEBPAGES = 40;
	private static WebPage[] webPages = new WebPage[NUM_WEBPAGES];

	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void initialize() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i] = new WebPage(i, "http://www.site.se/page" + i + ".html");
		}
	}
	
	// [Do modify this sequential part of the program.]
	private static void downloadWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i].download();
		}
	}
	
	// [Do modify this sequential part of the program.]
	private static void analyzeWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i].analyze();
		}
	}
	
	// [Do modify this sequential part of the program.]
	private static void categorizeWebPages() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			webPages[i].categorize();
		}
	}
	
	// [You are welcome to modify this method, but it should NOT be parallelized.]
	private static void presentResult() {
		for (int i = 0; i < NUM_WEBPAGES; i++) {
			System.out.println(webPages[i]);
		}
	}
	
	public static void main(String[] args) {
		// Initialize the list of webpages.
		initialize();
		
		// Start timing.
		long start = System.nanoTime();

		// Do the work.
		downloadWebPages();
		analyzeWebPages();
		categorizeWebPages();
		
		// Stop timing.
		long stop = System.nanoTime();

		// Present the result.
		presentResult();
		
		// Present the execution time.
		System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);
	}
}
