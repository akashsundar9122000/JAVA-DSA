package Recursion;

public class SearchInRotatedArray {
    static void main() {
        int[] arr = {5,6,7,8,9,1,2,3};
        int target = 50;
        System.out.println(searchInRotatedArray(arr, target, 0 , arr.length - 1));
    }

    static int searchInRotatedArray(int[] arr, int target, int start, int end){
        if(start > end){
            return -1;
        }
        int mid = start + (end - start) / 2;
        if(arr[mid] == target){
            return mid;
        }
        if(arr[start] <= arr[mid] && (arr[start] <= target && arr[mid] > target)){
            return searchInRotatedArray(arr, target, start, mid - 1);
        }
        return searchInRotatedArray(arr, target, mid + 1 , end);
    }
}
