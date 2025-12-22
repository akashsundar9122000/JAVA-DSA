package Recursion;

import java.util.Arrays;

public class SelectionSort {
    static void main() {
        int[] arr = {32,42,1,5,6,245,6,3};
        selectionSort(arr, arr.length - 1,0, 0);
        System.out.println(Arrays.toString(arr));
    }

    static void selectionSort(int[] arr, int i, int j, int max){
        if(i == 0){
            return;
        }
        if(j < i){
            if(arr[j] > arr[max]){
                selectionSort(arr, i, j + 1, j); //arr[j] will be the new max
            } else{
                selectionSort(arr, i , j + 1, max);
            }
        } else {
            int temp = arr[max];
            arr[max] = arr[i];
            arr[i] = temp;
            selectionSort(arr, i - 1, 0 , 0);
        }
    }
}
