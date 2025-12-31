package LinkedList.Basic;

public class DoublyLinkedList {

    private Node head;

    public void insertFirst(int val){
        Node node = new Node(val);
        node.next = head; //placing new node before head
        node.prev = null; //making new node's pre is null because it will be the first node
        if(head != null){ //there may be no element so added null check
            head.prev = node; //head's prev is new node connecting the doubly linked list
        }
        head = node; //Finally new node will the new head
    }

    public void insertLast(int val){
        Node node = new Node(val);
        node.next = null;
        if(head == null){
            node.prev = null;
            head = node;
            return;
        }
        Node temp = head;
        while (temp.next != null){
            temp = temp.next;
        }
        temp.next = node;
        node.prev = temp;
    }

    public void insert(int after, int val){
        Node prevNode = find(after);
        if(prevNode == null){
            System.out.println("Node does not exist");
            return;
        }
        Node node = new Node(val);
        node.next = prevNode.next;
        node.prev = prevNode ;
        prevNode.next = node;
        if(node.next != null){
            node.next.prev = node;
        }
    }

    public Node find(int val){
        Node temp = head;
        while (temp != null){
            if(temp.val == val){
                return temp;
            }
            temp = temp.next;
        }
        return null;
    }

    public void display(){
        Node temp = head;
        while (temp != null){
            System.out.print(temp.val + " -> ");
            temp = temp.next;
        }
        System.out.println("END");
    }

    public void displayReverse(){
        Node temp = head;
        Node last = null;
        while (temp != null){
            last = temp;
            temp = temp.next;
        }
        while (last != null){
            System.out.print(last.val + " -> ");
            last = last.prev;
        }
        System.out.println("BEGIN");
    }

    private class Node{
        int val;
        Node next;
        Node prev;

        public Node(int val){
            this.val = val;
        }
    }
}
