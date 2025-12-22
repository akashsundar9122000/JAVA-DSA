package Recursion;

import java.util.Arrays;

public class BubbleSort {
    static void main() {
        int[] arr = {4,3,0,34,1,2,1};
        bubbleSort(arr, arr.length - 1 , 0);
        System.out.println(Arrays.toString(arr));
    }

    static void bubbleSort(int[] arr, int i, int j){
        if(i == 0){
            return;
        }
        if(j < i) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
            bubbleSort(arr, i, j + 1);
        }else {
            bubbleSort(arr, i - 1, 0);
        }
    }
}
