package Recursion;

public class ArraySortedOrNot {
    static void main() {
        int[] arr = {1,2,3,3,36,6};
        System.out.println(isArraySorted2(arr, 0));
    }

    static boolean isArraySorted(int[] arr, int index){
        if(index + 1 == arr.length){
            return true;
        }
        if(arr[index] > arr[index + 1]){
            return false;
        }
        return isArraySorted(arr, index + 1);
    }

    //method 2

    static boolean isArraySorted2(int[] arr, int index){
        if(index == arr.length - 1){
            return true;
        }
        return arr[index] <= arr[index + 1] && isArraySorted2(arr, index + 1);
    }
}
