package Trees;

public class BinarySearchTree {
    private Node root;
    public BinarySearchTree(){

    }
    public class Node{
        private int value;
        private int height;
        private Node left;
        private Node right;

        public Node(int value){
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    }

    public int getHeight(Node node){
        if(node == null){
            return -1;
        }
        return node.height;
    }

    public boolean isEmpty(){
        return root == null;
    }

    public void display(){
        display(root, "Root Node is: ");
    }

    private void display(Node node, String details){
        if(node == null){
            return;
        }
        System.out.println(details + node.getValue());
        display(node.left, "This is left child of " + node.getValue() + " : ");
        display(node.right, "This is right child of " + node.getValue() + " : ");
    }

    public void insert(int value){
        root = insert(root, value); //after recursion in the below mentod finally root will be returned.
    }

    private Node insert(Node node, int value){
        if(node == null){
            node = new Node(value);
            return node;
        }
        //in BST if value is less than node value it should be inserted to its left
        if(value < node.value){
            node.left = insert(node.left, value);
        }
        //if greater then right
        if(value > node.value){
            node.right = insert(node.right, value);
        }
        //update the node height now
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1; //height will be max of left or right of the node + 1
        //if node is null new node is inserted (base condition) if not the same node will be returned.
        return node;
    }

    public boolean isBalanced(){
        return isBalanced(root);
    }

    private boolean isBalanced(Node node){
        if(node == null){
            return true;
        }
        return Math.abs(getHeight(node.left) - getHeight(node.right)) <= 1 && isBalanced(node.left) && isBalanced(node.right); //We need to find difference of left and right and it should be <=1 to be balance similarly we need to check for all the further nodes below
    }

    public void populate(int[] nums){
        for(int num : nums){
            this.insert(num);
        }
    }

    public void populateSorted(int[] nums){ //for sorted arr the tree will be like skewed i.e only right side will be available. To make it righe we need to self balance
        populateSorted(nums, 0, nums.length - 1);
    }
    //To do self balancing we take mid element each time and use it to form tree
    public void populateSorted(int[] nums, int start, int end){
        if(start >= end){
            return;
        }
        int mid = (start + end) / 2;
        this.insert(nums[mid]);
        populateSorted(nums, start, mid );
        populateSorted(nums, mid + 1, end);
    }

    public void preOrderTraversal(){ //Node -> left -> right
        preOrderTraversal(root);
    }

    private void preOrderTraversal(Node node){
        if(node == null){
            return;
        }
        System.out.print(node.value + " ");
        preOrderTraversal(node.left);
        preOrderTraversal(node.right  );
    }

    public void inOrderTraversal(){ //Left -> Node -> Right
        inOrderTraversal(root);
    }

    private void inOrderTraversal(Node node){
        if (node == null){
            return;
        }
        inOrderTraversal(node.left);
        System.out.print(node.value + " ");
        inOrderTraversal(node.right);
    }

    public void postOrderTraversal(){//Left -> Right -> Node
        postOrderTraversal(root);
    }

    private void postOrderTraversal(Node node){
        if(node == null){
            return;
        }
        postOrderTraversal(node.left);
        postOrderTraversal(node.right);
        System.out.print(node.value + " ");
    }

    static void main() {
        int[] nums = {5,2,7,1,4,6,9,8,3,10};
        BinarySearchTree binarySearchTree = new BinarySearchTree();
        binarySearchTree.populate(nums);
        binarySearchTree.display();
        int[] sorted = {1,2,3,4,5,6,7,8,9,10};
        binarySearchTree.populateSorted(sorted);
        binarySearchTree.display();
        binarySearchTree.preOrderTraversal();
        System.out.println();
        binarySearchTree.inOrderTraversal();
        System.out.println();
        binarySearchTree.postOrderTraversal();
    }
}
