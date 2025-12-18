package GFG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class RotateArray {
    //https://www.geeksforgeeks.org/batch/gfg-160-problems/track/arrays-gfg-160/problem/rotate-array-by-n-elements-1587115621
    /**
     * Given an array arr[]. Rotate the array to the left (counter-clockwise direction) by d steps, where d is a positive integer. Do the mentioned change in the array in place.
     *
     * Note: Consider the array as circular.
     *
     * Examples :
     *
     * Input: arr[] = [1, 2, 3, 4, 5], d = 2
     * Output: [3, 4, 5, 1, 2]
     * Explanation: when rotated by 2 elements, it becomes 3 4 5 1 2.
     * Input: arr[] = [2, 4, 6, 8, 10, 12, 14, 16, 18, 20], d = 3
     * Output: [8, 10, 12, 14, 16, 18, 20, 2, 4, 6]
     * Explanation: when rotated by 3 elements, it becomes 8 10 12 14 16 18 20 2 4 6.
     * Input: arr[] = [7, 3, 9, 1], d = 9
     * Output: [3, 9, 1, 7]
     * Explanation: when we rotate 9 times, we'll get 3 9 1 7 as resultant array.
     */

    static void main() {
        int[] arr = {1,2,3,4,5};
        int d = 2;
        rotateArr(arr, d);
        System.out.println(Arrays.toString(arr));
    }

    static void rotateArr(int[] arr, int d){
        int mod = 0;
        while(d > arr.length){
            d = d % arr.length;
        }
        HashSet<Integer> set = new HashSet<>();

        for(int i = 0 ; i < arr.length-d +1; i ++){
            int index = i - d;
//            if(set.contains(i))
//                break;
            if(index < 0){
                int temp = arr[arr.length + index];
                arr[arr.length + index] = arr[i];
                arr[i] = temp;
                set.add(arr.length  +index);
            } else{
                int temp = arr[index];
                arr[index] = arr[i];
                arr[i] = temp;
                set.add(index);
            }
        }
    }
}
