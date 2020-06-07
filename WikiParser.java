
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.StringTokenizer;

/**
 *
 */
/**
 * @author downey
 *
 */
public class WikiParser {

    // lista akapitów, które należy przeszukać
    private Elements paragraphs;
    private ArrayList<String> visited2;
    // stos otwartych nawiasów
    // TODO: rozważ uproszczenie tego kodu poprzez zliczanie nawiaasów
    private Deque<String> parenthesisStack;


    /**
     * Inicjalizuje WikiParser za pomocą listy Elements.
     *
     * @param paragraphs
     */
    public WikiParser(Elements paragraphs) {
        this.paragraphs = paragraphs;
        this.parenthesisStack = new ArrayDeque<String>();
    }

    /**
     * Wyszukuje prawidłowe łącze w akapitach.
     *
     * Ostrzega, jeśli akapit zawiera niesparowane nawiasy.
     *
     * @return
     */
    public Element findFirstLink() {
        for (Element paragraph: paragraphs) {
            Element firstLink = findFirstLinkPara(paragraph);
            if (firstLink != null) {
                return firstLink;
            }
            if (!parenthesisStack.isEmpty()) {
                System.err.println("Ostrzeżenie: niesparowane nawiasy.");
            }
        }
        return null;
    }

    /**
     * Zwraca pierwsze prawidłowe łącze w akapicie lub wartość null.
     *
     * @param root
     */
    private Element findFirstLinkPara(Node root) {
        // utwórz obiekt Iterable do przejścia drzewa
        Iterable<Node> nt = new WikiNodeIterable(root);

        // przejdź w pętli przez węzły
        for (Node node: nt) {
            // przetwórz obiekty TextNode w celu uzyskania nawiasów
            if (node instanceof TextNode) {
                processTextNode((TextNode) node);
            }
            // przetwórz elementy w celu uzyskania łączy
            if (node instanceof Element) {
                Element firstLink = processElement((Element) node);
                if (firstLink != null) {
                    return firstLink;
                }
            }
        }
        return null;
    }

    /**
     * Zwraca element, jeśli jest on prawidłowym łączem; w przeciwnym razie zwraca wartość null.
     *
     *
     *
     * @param elt
     */
    private Element processElement(Element elt) {
        //System.out.println(elt.tagName());
        if (validLink(elt)) {
            return elt;
        }

        return null;
    }

    /**
     * Sprawdza, czy łącze jest wartością.
     *
     * @param elt
     * @return
     */
    private boolean validLink(Element elt) {
        // niedobrze, jeśli
        // nie jest łączem

        if (!elt.tagName().equals("a")) {
            return false;
        }

        // jest wyświetlane pochyłą czcionką
        if (isItalic(elt)) {
            return false;
        }
        // jest w nawiasie
        if (isInParens(elt)) {
            return false;
        }
        // jest zakładką
        if (startsWith(elt, "#")) {
            return false;
        }
        // jest stroną pomocy Wikipedii
        if (startsWith(elt, "/wiki/Help:")) {
            return false;
        }

        // TODO: istnieje jeszcze kilka innych "reguł", których tu nie obsłużyliśmy
        return true;
    }

    /**
     * Sprawdza, czy łącze zaczyna się od podanego łańcucha znakowego.
     *
     * @param elt
     * @param s
     * @return
     */
    private boolean startsWith(Element elt, String s) {
        //System.out.println(elt.attr("href"));
        return (elt.attr("href").startsWith(s));
    }

    /**
     * Sprawdza, czy element znajduje się w nawiasach (również zagnieżdżonych).
     *
     * @param elt
     * @return
     */
    private boolean isInParens(Element elt) {
        // sprawdź, czy na stosie znajdują się jakieś nawiasy
        return !parenthesisStack.isEmpty();
    }

    /**
     * Sprawdza, czy element wyświetlany jest pochyłą czcionką.
     *
     * (Either a "i" or "em" tag)
     *
     * @param start
     * @return
     */
    private boolean isItalic(Element start) {
        // podążaj wzdłuż łańcucha przodków aż do osiąnięcia wartości null
        for (Element elt=start; elt != null; elt = elt.parent()) {
            if (elt.tagName().equals("i") || elt.tagName().equals("em")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Przetwarza węzeł tekstowy, dzieląc go i sprawdzając obecność nawiasów.
     *
     * @param node
     */
    private void processTextNode(TextNode node) {
        StringTokenizer st = new StringTokenizer(node.text(), " ()", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            // System.out.print(token);
            if (token.equals("(")) {
                parenthesisStack.push(token);
            }
            if (token.equals(")")) {
                if (parenthesisStack.isEmpty()) {
                    System.err.println("Ostrzeżenie: niesparowane nawiasy.");
                }
                parenthesisStack.pop();
            }
        }
    }
}