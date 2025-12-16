package Recursion;

public class BinarySearch {
    //Binary search using recursion

    static void main() {
        int[] arr = {1,4,6,7,8,9};
        int target = 8;
        System.out.println(search(arr, target, 0, arr.length - 1));
    }

    static int search(int[] arr, int target, int start, int end){
        if(start > end){
            return  - 1;
        }
        int mid = start + (end - start) / 2;
        if(arr[mid] == target){
            return mid;
        } else if (arr[mid] > target) {
            return search(arr, target, start, mid - 1);
        } else{
            return search(arr, target, mid + 1, end);
        }
    }
}
