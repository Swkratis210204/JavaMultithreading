
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Program2 {
    final static int NUM_WEBPAGES = 40;
    private static List<WebPage> webPages;

    private static void initialize() {
        webPages = IntStream.range(0, NUM_WEBPAGES)
                .mapToObj(i -> new WebPage(i, "http://www.site.se/page" + i + ".html"))
                .collect(Collectors.toList());
    }

    private static void processWebPages() {
        webPages = webPages.parallelStream()
                .peek(WebPage::download)
                .peek(WebPage::analyze)
                .peek(WebPage::categorize)
                .collect(Collectors.toList());
    }

    private static void presentResult() {
        webPages.forEach(System.out::println);
    }

    public static void main(String[] args) {
        initialize();

        long start = System.nanoTime();
        
        processWebPages();

        long stop = System.nanoTime();

        presentResult();

        System.out.println("Execution time (seconds): " + (stop - start) / 1.0E9);
    }
}
