
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiFetcher {
    private long lastRequestTime = -1;
    private long minInterval = 1000;

    /**
     * Wydobywa i parsuje łańcuch URL, a następnie zwraca listę elementów akapitu.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Elements fetchWikipedia(String url) throws IOException {
        sleepIfNeeded();

        // pobierz i sparsuj dokument
        Connection conn = Jsoup.connect(url);
        Document doc = conn.get();

        // wybierz treść tekstową i wyciągnij akapity
        Element content = doc.getElementById("mw-content-text");

        // TODO: nie wybieraj akapitów z pasków bocznych i ramek
        Elements paras = content.select("p");
        return paras;
    }

    /**
     * Odczytuje treść strony serwisu Wikipedia z katalogu src/resources.
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Elements readWikipedia(String url) throws IOException {
        URL realURL = new URL(url);


        // złóż nazwę pliku
        String slash = File.separator;
        String filename = "resources" + slash + realURL.getHost() + realURL.getPath();

        // odczytaj plik
        InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
        Document doc = Jsoup.parse(stream, "UTF-8", filename);

        // sparsuj zawartość pliku
        // TODO: pozbądź się widocznego poniżej powtórzonego kodu
        Element content = doc.getElementById("mw-content-text");
        Elements paras = content.select("p");
        return paras;
    }

    /**
     * Przestrzega ograniczeń serwisu, opóźniając kolejne żądania co najmniej o minimalny odstęp czasu.
     */
    private void sleepIfNeeded() {
        if (lastRequestTime != -1) {
            long currentTime = System.currentTimeMillis();
            long nextRequestTime = lastRequestTime + minInterval;
            if (currentTime < nextRequestTime) {
                try {
                    //System.out.println("Śpi do " + nextRequestTime);
                    Thread.sleep(nextRequestTime - currentTime);
                } catch (InterruptedException e) {
                    System.err.println("Ostrzeżenie: przerwane uśpienie w metodzie fetchWikipedia.");
                }
            }
        }
        lastRequestTime = System.currentTimeMillis();
    }

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        WikiFetcher wf = new WikiFetcher();
        String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        Elements paragraphs = wf.readWikipedia(url);

        for (Element paragraph: paragraphs) {
            System.out.println(paragraph);
        }
    }
}