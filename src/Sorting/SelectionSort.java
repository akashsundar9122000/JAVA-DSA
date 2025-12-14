package Sorting;

public class SelectionSort {

    static void main() {
        int[] arr = {342,64, 67, 54,7, 99, 3};
        for(int i = 0 ; i < arr.length - 1 ; i ++){
            int greatestIndex = findGreatestIndex(arr, 0, arr.length - i - 1);
            swap(arr, arr.length - i - 1, greatestIndex);
        }
        for (int nums: arr){
            System.out.print(nums + " ");
        }
    }

    static int findGreatestIndex(int[] arr, int start, int end){
        int max = start;
        for (int i = start; i <= end ; i ++){
            if(arr[i] > arr[max]){
                max = i;
            }
        }
        return max;
    }

    static void swap(int[] arr, int start,int end){
        int temp = arr[start];
        arr[start] = arr[end];
        arr[end] = temp;
    }
}
