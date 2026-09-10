package Sorting;

import java.util.Arrays;

public class QuickSort {

    /**
     * How to approach?
     * First take a pivot element it will be the first element in the array then swap that element untill all the smaller elements are to its left and all the larger elements are to its right
     * Then perform recursion on left array and right array until 1 element in array left
     */

    static void main(String[] args) {
        int[] arr = {34,46,23,2,54,7};
        quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    static void quickSort(int[] arr, int low, int high){
        if(low < high){
            int partition = getPivotPlaced(arr, low, high); //placing the pivot at correct place
            quickSort(arr, low, partition); //going left side unsorted array
            quickSort(arr, partition + 1, high); //going right side unsorted array
        }
    }

    static int getPivotPlaced(int[] arr, int low, int high){
        int pivot = arr[low]; //always first element is pivot
        /**
         * To place the pivot we need to find the highest element from left to right and lowest from right to left comparing with pivot and swqp it untill j > i
         */
        int i = low, j = high;
        while (i < j){
            while (arr[i] <= pivot && i < high){
                i ++;
            }
            while (arr[j] > pivot && j > low){
                j --;
            }
            //We found immediate big and immediate small numbers compared to pivot now swap them only if i < j
            if(i < j) {
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        //Now to put pivot to its correct place
        int temp = arr[low];
        arr[low] = arr[j];
        arr[j] = temp;
        return j;
    }
}
