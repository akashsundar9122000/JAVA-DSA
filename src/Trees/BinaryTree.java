package Trees;

import java.util.Scanner;

public class BinaryTree {

    public BinaryTree(){

    }
    private Node root;
    private static class Node{
        int value;
        Node left;
        Node right;

        public Node(int value){
            this.value = value;
        }
    }

    //insert element

    public void populate(Scanner sc){
        System.out.print("Enter the value for root: ");
        int val = sc.nextInt();
        root = new Node(val);
        populate(sc, root);
    }

    private void populate(Scanner sc, Node node){
        System.out.print("Do you want to enter left of " + node.value);
        boolean left = sc.nextBoolean();
        if(left){
            System.out.print("Enter left node value: ");
            int val = sc.nextInt();
            node.left = new Node(val);
            populate(sc, node.left); //recursive call for next left nodes;
        }
        //if dont want to enter left side of node we can ask for right
        System.out.print("Do you want to enter right of " + node.value);
        boolean right = sc.nextBoolean();
        if(right){
            System.out.print("Enter right node value: ");
            int val = sc.nextInt();
            node.right = new Node(val);
            populate(sc, node.right); //recursive call for next right  nodes;
        }
    }

    public void display(){
        display(root, "");
    }

    private void display(Node node, String indent){
        if(node == null){
            return;
        }
        System.out.print(indent + node.value);
        display(node.left,indent);
        display(node.right, indent);
    }

    static void main() {
        Scanner sc = new Scanner(System.in);
        BinaryTree tree = new BinaryTree();
        tree.populate(sc);
        tree.display();
    }
}
