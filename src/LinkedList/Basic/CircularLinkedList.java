package LinkedList.Basic;

public class CircularLinkedList {

    private Node head;
    private Node tail;
    public CircularLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public void insert(int val){
        Node node = new Node(val);
        if(head == null){
            head = node;
            tail = node;
            return;
        }
        tail.next = node;
        node.next = head;
        tail = node;
    }

    public void delete(int val){
        Node temp = head;
        if(temp == null){
            return;
        }
        if(temp.val == val){
            head = head.next;
            tail.next = head;
            return;
        }

        do{
            Node n = temp.next;
            if(n. val == val){
                temp.next = n.next;
                break;
            }
            temp = temp.next;
        }while (temp != head);
    }

    public void display(){
        Node temp = head;
        if(temp != null){
            do{
                System.out.print(temp.val + " -> ");
                temp = temp.next;
            }while (temp != head); //this will print atlease 1 so if only one node available it will work
        }
        System.out.println("HEAD");
    }

    private class Node{
        int val;
        Node next;

        public Node(int val){
            this.val = val;
        }
    }
}
