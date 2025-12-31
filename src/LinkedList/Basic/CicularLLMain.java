package LinkedList.Basic;

public class CicularLLMain {
    static void main() {
        CircularLinkedList cll = new CircularLinkedList();
        cll.insert(23);
        cll.insert(3);
        cll.insert(19);
        cll.insert(75);
        cll.display();
        cll.delete(19);
        cll.display();
    }
}
