package Recursion;

public class LinearSearch {
    static void main() {
        int[] arr = {1,34,3,65,224,76};
        int target = 225;
        System.out.println(linearSearchUsingRecursion(arr, target, 0));
    }

    static int linearSearchUsingRecursion(int[] arr, int target, int index){
        if(arr[index] == target){
            return index;
        }
        if(index == arr.length - 1){
            return -1;
        }
        return linearSearchUsingRecursion(arr, target, index + 1);
    }
}
