package LinkedList.Basic;

public class Main {
    static void main() {
        LinkedList ll = new LinkedList();
        ll.insertFirst(3);
        ll.insertFirst(2);
        ll.insertFirst(8);
        ll.insertFirst(17);
        ll.insertLast(99);
        ll.insert(100,3);
        ll.display();
        System.out.println(ll.deleteFirst());
        ll.display();
        System.out.println(ll.deleteLast());
        ll.display();
        System.out.println(ll.delete(2));
        ll.display();
        System.out.println(ll.find(2));
        ll.insertUsingRecursion(12,2);
        ll.display();
    }
}
