package Trees;

import com.sun.jdi.request.StepRequest;

public class SegmentTree {

    Node root;
    public SegmentTree(int[] arr){
        //create tree using this array
        this.root = constructTree(arr, 0, arr.length - 1);

    }
    private class Node{
        int val;
        int startInterval;
        int endInterval;
        Node left;
        Node right;

        public Node(int startInterval, int endInterval){
            this.startInterval = startInterval;
            this.endInterval = endInterval;
        }
    }

    private Node constructTree(int[] arr, int start, int end){
        if(start == end){
            //leaf node
            Node leaf = new Node(start, end);
            leaf.val = arr[start];
            return leaf;
        }
        //create node with index you are at
        Node node = new Node(start, end);
        int mid = ( start + end) / 2;
        node.left = constructTree(arr, start, mid); //first half
        node.right = constructTree(arr, mid + 1, end); //second half
        node.val = node.left.val + node.right.val; //sum of the 2 child nodes is val for parent node
        return node;
    }

    public void display(){
        display(root);
    }

    private void display(Node node){
        String str = "";
        if(node.left != null){
            str += "Interval=[" + node.left.startInterval+ "-" + node.left.endInterval + "]and data - " + node.left.val + " = >";
        } else{
            str += "No left child";
        }

        //for current node
        str += "Interval=[" + node.startInterval+ "-" + node.endInterval + "]and data - " + node.val + " = >";

        //for right

        if(node.right != null){
            str += " <= Interval=[" + node.right.startInterval+ "-" + node.right.endInterval + "]and data - " + node.right.val;
        } else{
            str += "No right child";
        }
        System.out.println(str + "\n");

        if(node.left != null){
            display(node.left);
        }
        if(node.right != null){
            display(node.right);
        }
    }

    //query

    public int query(int startIndex, int endIndex){
        return query(root, startIndex, endIndex);
    }

    private int query(Node node, int startIndex, int endIndex){
        if(node.startInterval >= startIndex && node.endInterval <= endIndex){
            //node is completely inside the query interval
            return node.val; 
        } else if (node.startInterval >endIndex || node.endInterval < startIndex) {
            //completely outside the query interval
            return 0;
        } else{
            //overlapping
            return query(node.left, startIndex, endIndex) + query(node.right, startIndex, endIndex); //want to check in both left and right nodes
        }
    }

    //updating tree

    public void update(int index, int value){
        root.val = update(root, index, value);
    }

    private int update(Node node, int index, int value){
        if(index >= node.startInterval && index <= node.endInterval){
            //lies inbetween
             if(index == node.startInterval && index == node.endInterval){
                 //found the node
                 node.val = value;
                 return node.val;
             } else {
                 node.val = update(node.left, index, value) + update(node.right, index, value);
                 return node.val;
             }
        }
        return node.val; //if its completely outside the range just return the existing value
    }

    static void main() {
         int[] arr = {3,8,6,7,-2,-8,4,9};
         SegmentTree segmentTree = new SegmentTree(arr);
         segmentTree.display();
         System.out.println(segmentTree.query(1, 6));
    }
}
