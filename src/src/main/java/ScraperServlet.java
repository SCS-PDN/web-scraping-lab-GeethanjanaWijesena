import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ScraperServlet")
public class ScraperServlet extends HttpServlet {

    // Static inner class to hold scraped data
    public static class ScrapedData implements java.io.Serializable {
        String type;
        String content;

        public ScrapedData(String type, String content) {
            this.type = type;
            this.content = content;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Start HTML response
        out.println("<html><head><title>Scraping Results</title></head><body>");

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("scrapeOption");

        List<ScrapedData> dataList = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();

            if (options != null) {
                for (String opt : options) {
                    switch (opt) {
                        case "title":
                            dataList.add(new ScrapedData("Title", doc.title()));
                            break;

                        case "headings":
                            for (int i = 1; i <= 6; i++) {
                                Elements headings = doc.select("h" + i);
                                for (Element h : headings) {
                                    dataList.add(new ScrapedData("h" + i, h.text()));
                                }
                            }
                            break;

                        case "links":
                            Elements links = doc.select("a[href]");
                            for (Element link : links) {
                                dataList.add(new ScrapedData("Link", link.absUrl("href")));
                            }
                            break;
                    }
                }
            }

            // Session tracking
            HttpSession session = request.getSession();
            Integer visitCount = (Integer) session.getAttribute("visitCount");
            if (visitCount == null) visitCount = 0;
            visitCount++;
            session.setAttribute("visitCount", visitCount);

            out.println("<p>You have visited this page " + visitCount + " times.</p>");

            // Show result table
            out.println("<h3>Scraped Data:</h3>");
            out.println("<table border='1'><tr><th>Type</th><th>Content</th></tr>");
            for (ScrapedData item : dataList) {
                out.println("<tr><td>" + item.type + "</td><td>" + item.content + "</td></tr>");
            }
            out.println("</table>");

            // Show JSON
            Gson gson = new Gson();
            String json = gson.toJson(dataList);

            out.println("<h3>JSON:</h3><pre>" + json + "</pre>");

            // Set in session for download
            session.setAttribute("dataList", dataList);

            // CSV download button
            out.println("<form method='post' action='DownloadCSVServlet'>");
            out.println("<input type='submit' value='Download CSV'>");
            out.println("</form>");

        } catch (Exception e) {
            out.println("<p style='color:red;'>Error scraping URL: " + e.getMessage() + "</p>");
        }

        out.println("</body></html>");
    }
}

