package Recursion;

import java.util.Arrays;

public class MergeSort {
    static void main() {
        int[] arr = {5,4,3,2,1};
        arr = mergeSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    static int[] mergeSort(int[] arr){
        if(arr.length == 1){ //base condition after every split if size is just 1 need to return
            return arr;
        }

        int mid = arr.length / 2; //Findin mid to split array in 2 half

        int[] left = mergeSort(Arrays.copyOfRange(arr, 0 , mid)); //First half of the array
        int[] right = mergeSort(Arrays.copyOfRange(arr, mid , arr.length));

        return merge(left, right); // Need to merge the splited array
    }

    static int[] merge(int[] left, int[] right){
        int i = 0, j = 0, k = 0;
        int[] res = new int[left.length + right.length];
        while (i < left.length && j < right.length){
            if(left[i] < right[j]){
                res[k] = left[i];
                i ++;
            } else{
                res[k] = right[j];
                j++;
            }
            k ++;
        }
        //For extra numbers after size splitted
        while (i < left.length){
            res[k] = left[i];
            i++;
            k ++;
        }
        while (j < right.length){
            res[k] = right[j];
            j ++;
            k ++;
        }
        return res;
    }
}
