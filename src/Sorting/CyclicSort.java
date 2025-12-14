package Sorting;

public class CyclicSort {
    static void main() {
        //Shoould user this algorithm if numbers are from 1 to N
        int[] arr = {3,5,2,1,4};
        int i = 0;
        while (i < arr.length){
            int correct = arr[i] - 1;
            if(arr[correct] != arr[i]){
                int tem = arr[correct];
                arr[correct] = arr[i];
                arr[i] = tem;
            } else {
                i ++;
            }
        }
        for (int num : arr){
            System.out.print(num + " ");
        }
    }
}
