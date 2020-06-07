

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jsoup.nodes.Node;


/**
 * Przeprowadza przeszukiwanie w głąb obiektu klasy Node biblioteki jsoup.
 *
 * @author downey
 *
 */
public class WikiNodeIterable implements Iterable<Node> {

    private Node root;

    /**
     * Tworzy obiekt typu iterable zaczynający się od podanego obiektu klasy Node.
     *
     * @param root
     */
    public WikiNodeIterable(Node root) {
        this.root = root;
    }

    @Override
    public Iterator<Node> iterator() {
        return new WikiNodeIterator(root);
    }

    /**
     * Wewnętrzna klasa implementująca interfejs Iterator.
     *
     * @author downey
     *
     */
    private class WikiNodeIterator implements Iterator<Node> {

        // ten stos przechowuje obiekty klasy Node, które oczekują na odwiedzenie
        Deque<Node> stack;

        /**
         * Inicjalizuje obiekt klasy Iterator, odkładając korzeń na stos.
         *
         * @param node
         */
        public WikiNodeIterator(Node node) {
            stack = new ArrayDeque<Node>();
            stack.push(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Node next() {
            // jeśli stos jest pusty, skończyliśmy
            if (stack.isEmpty()) {
                throw new NoSuchElementException();
            }

            // w przeciwnym razie zdejmij ze stosu następny obiekt klasy Node
            Node node = stack.pop();
            //System.out.println(node);

            // umieść dzieci na stosie w odwrotnej kolejności
            List<Node> nodes = new ArrayList<Node>(node.childNodes());
            Collections.reverse(nodes);
            for (Node child: nodes) {
                stack.push(child);
            }
            return node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}