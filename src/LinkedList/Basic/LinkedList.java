package LinkedList.Basic;

public class LinkedList {

    private Node head;
    public Node tail;
    private int size;

    public LinkedList(){
        this.size = 0;
    }

    public void insertFirst(int val){
        Node node = new Node(val);
        node.next = head;
        head = node;

        if (tail == null){ //if tail is null then this is first node that is getting added so head and tail are same
            tail = head;
        }
        size += 1;
    }

    public void insertLast(int val){
        if(tail == null){
            insertFirst(val);
            return;
        }
        Node node = new Node(val);
        tail.next = node;
        tail = node;
        size += 1;
    }

    public void insert(int val, int index){
        if(index == 0){
            insertFirst(val);
            return;
        } else if (index == size) {
            insertLast(val);
            return;
        }
        Node temp = head;
        for (int i = 1; i < index; i++) {
            temp = temp.next;
        }
        Node node = new Node(val, temp.next);
        temp.next = node;
        size ++;
    }

    //using recursion
    public void insertUsingRecursion(int val, int index){
        head = insertRec(val, index, head);
    }

    private Node insertRec(int val, int index, Node node){
        if(index == 0){
            Node temp = new Node(val, node);
            size ++;
            return temp;
        }
        node.next = insertRec(val, index-1, node.next);
        return node;
    }

    public int deleteFirst(){
        int val = head.val;
        head = head.next;
        if(head == null){ //if no had then tail also should be numm
            tail = null;
        }
        size --;
        return val;
    }

    public int deleteLast(){
        if(size <= 1){ //if only one node available
            deleteFirst();
        }
        Node secondLast = get(size - 2);
        int val = tail.val;
        tail = secondLast;
        tail.next = null;
        size --;
        return val;
    }
    
    public int delete(int index){
        if (index == 0){
            return deleteFirst();
        } else if (index == size - 1) {
            deleteLast();
        }
        Node prevNode = get(index - 1);
        int val = prevNode.next.val;
        prevNode.next = prevNode.next.next;
        size --;
        return val;
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

    public Node get(int index){
        Node node = head;
        for (int i = 0 ; i < index ; i++){
            node = node.next;
        }
        return node;
    }

    public void display(){
        Node temp = head;
        while (temp != null){
            System.out.print(temp.val + " -> ");
            temp = temp.next;
        }
        System.out.println("END");
    }

    private class Node{
        int val;
        Node next;

        public Node(int val){
            this.val = val;
        }

        public Node(int val, Node next){
            this.val = val;
            this.next = next;
        }
    }

}
