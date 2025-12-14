package Sorting;

public class BubbleSort {

    static void main() {
        int arr[] = {32,342,1,34,5,21,55,2};
        bubbleSort(arr);
        for(int num :  arr){
            System.out.print(num + " ");
        }
    }

    static void bubbleSort(int[] arr){
        boolean swapped;
        for(int i = 0 ; i < arr.length ; i++){
            swapped = false;
            for(int j = 1 ; j < arr.length - i ; j ++){ //because for each j iteration the big element goes last so for next iteration we dont want to consider taht because its already sorted.
                if(arr[j] < arr[j-1]){
                    int temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                    swapped = true;
                }
            }
            if(!swapped){ //if no swap happened in last iteration then array is already sorted so we can break the loop
                break;
            }
        }
    }
}
