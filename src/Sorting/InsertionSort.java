package Sorting;

import java.util.Arrays;

public class InsertionSort {

    static void main() {
        int[] arr = {43,123,532,13,52,1,54,2,66};
        for (int i = 0 ; i < arr.length - 1 ; i++){
            for(int j = i + 1; j > 0 ; j--){
                if(arr[j] < arr[j-1]){
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                } else{
                    break;
                }
            }
        }
        for(int num : arr){
            System.out.print(num + " ");
        }
    }
}
