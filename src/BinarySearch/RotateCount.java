package BinarySearch;

public class RotateCount {
    //In this question we are going to find the count of how many times the array is rotated.
    //Its simple we can just find the pivot and the pivotIndex + 1 is the answer.

    static void main() {
        int arr[] = {4,5,6,7,0,1,2};
        System.out.println(findPivotIndex(arr) + 1);
    }

    static int findPivotIndex(int[] arr){
        int start = 0;
        int end = arr.length - 1;
        while (start < end){
            int mid = start + (end - start) / 2;
            if(end > mid && arr[mid] > arr[mid + 1]){
                return mid;
            }
            if(start < mid && arr[mid] < arr[mid - 1]){
                return mid - 1;
            }
            if(arr[start] >= arr[mid]){
                end = mid - 1;
            } else{
                end = start + 1;
            }
        }
        return -1;
    }
}
