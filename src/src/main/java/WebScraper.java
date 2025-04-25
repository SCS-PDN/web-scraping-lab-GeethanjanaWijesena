import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class NewsItem {
    String headline;
    String date;
    String author;

    public NewsItem(String headline, String date, String author) {
        this.headline = headline;
        this.date = date;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Headline: " + headline + " | Date: " + date + " | Author: " + author;
    }
}

public class WebScraper {
    public static void main(String[] args) {
        String url = "https://bbc.com";

        try {
            Document doc = Jsoup.connect(url).get();

            // Title
            System.out.println("Title: " + doc.title());

            // Headings
            System.out.println("\nHeadings:");
            for (int i = 1; i <= 6; i++) {
                Elements headings = doc.select("h" + i);
                for (Element heading : headings) {
                    System.out.println("h" + i + ": " + heading.text());
                }
            }

            // Links
            System.out.println("\nLinks:");
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                System.out.println(link.text() + " -> " + link.absUrl("href"));
            }

            // News Headlines
            System.out.println("\nNews Headlines:");
            List<NewsItem> newsItems = new ArrayList<>();
            Elements articles = doc.select("h3");  // headlines typically in h3

            for (Element article : articles) {
                String headline = article.text();
                String date = "N/A";   // Date and author may not be available on homepage
                String author = "N/A";
                newsItems.add(new NewsItem(headline, date, author));
            }

            for (NewsItem item : newsItems) {
                System.out.println(item);
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

