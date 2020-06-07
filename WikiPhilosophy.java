
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Sprawdza hipotezę dotyczącą Wikipedii i filozofii.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Kliknij pierwsze łącze, które nie znajduje się w nawiasie i nie jest wyświetlane pochyloną czcionką.
     * 2. Pomiń łącza zewnętrzne, łącza wskazujące bieżącą stronę oraz czerwone łącza.
     * 3. Zakończ, gdy dojdzie do strony hasła "filozofia", strony bez łączy lub strony, która nie istnieje,
     *    albo gdy natrafisz na pętlę.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://pl.wikipedia.org/wiki/Filozfia";
        String source = "https://pl.wikipedia.org/wiki/matematyka";

        testConjecture(destination, source, 100);
    }

    /**
     * Zaczyna od podanego URL-a i przechodzi na stronę wskazywaną przez pierwsze łącze aż do momentu, gdy uda się dotrzeć do strony docelowej lub przekroczony zostanie limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        String url = source;


        for (int i=0; i<limit; i++) {


            if (visited.contains(url)){
                continue;
            }

            Element elt = getFirstValidLink(url);

            if (elt == null) {
                System.err.println("Dotarliśmy do strony bez prawidłowych łączy.");
                return;
            }


            System.out.println("**" + elt.text() + "**");
            url = elt.attr("abs:href");

            if (url.equals(destination)) {
                System.out.println("Eureka!");
                break;
            }
        }
    }

    /**
     * Ładuje i parsuje stronę o podanym URL-u, a następnie wydobywa pierwsze łącze.
     *
     * @param url
     * @return obiekt klasy Element będący pierwszym łączem lub wartość null.
     * @throws IOException
     */
    private static Element getFirstValidLink(String url) throws IOException {
        print("Wydobywam %s...", url);


        Elements paragraphs = wf.fetchWikipedia(url);

        WikiParser wp = new WikiParser(paragraphs);
        Element elt = wp.findFirstLink();

        return elt;
    }

    /**
     * Formatuje i wyświetla argumenty.
     *
     * @param msg
     * @param args
     */
    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }
}