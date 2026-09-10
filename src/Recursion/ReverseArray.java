package Recursion;

import java.util.Arrays;

public class ReverseArray {
    static void main() {
        int[] arr = {1,2,3,5,6};
        reverseUsingRecursion(0, arr);
        System.out.println(Arrays.toString(arr));
    }

    static void reverseUsingRecursion(int index, int[] arr){
        if(index >= arr.length/2){
            return;
        }
        int temp = arr[index];
        arr[index] = arr[arr.length - index - 1];
        arr[arr.length - index - 1] = temp;
        reverseUsingRecursion(index + 1, arr);
    }
}
