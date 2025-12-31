package LinkedList.Basic;

public class DoublyLLMain {
    static void main() {
        DoublyLinkedList dll = new DoublyLinkedList();
        dll.insertFirst(3);
        dll.insertFirst(2);
        dll.insertFirst(8);
        dll.insertFirst(17);
        dll.display();
        dll.displayReverse();
        dll.insertLast(99);
        dll.display();
        dll.insert(99, 65);
        dll.display();
    }
}
