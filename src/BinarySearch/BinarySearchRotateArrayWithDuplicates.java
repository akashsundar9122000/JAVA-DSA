package BinarySearch;

public class BinarySearchRotateArrayWithDuplicates {
    /**
     * The array will have pivot element, before numbers are in ascending order after numbers also will be in ascending order.
     * First we need to find that pivot index then we can do binary search separately 2 times for before and after arrays of the pivot
     * To find pivot element there are 4 cases.
     * Case 1 - if(arr[mid] > arr[mid + 1]) then arr[mid] is obviously pivot element
     * Case 2 - if(arr[mid] < arr[mid - 1]) then arr[mid - 1] is obviously pivot element.
     * This is the problem where array contains duplicate
     * Case 3 - We should skil the duplicates for eg if arr[start] == arr[mid] && arr[mid]==arr[end] we can skip start and end if they are not pivot by start++ and end --
     * but before doing case 3 we need to check if start or end is the pivot.
     * After finding the pivot index we can run binary search for both arr
     */

    static void main() {
        int arr[] = {4,5,5,6,6,7,0,1,2,2,2};
        int target = 1;
        int pivotIndex = findPivotIndex(arr);
        int resIndex = getBinarySearchResult(arr, target, 0, pivotIndex);
        if(resIndex == -1){
            resIndex = getBinarySearchResult(arr, target, pivotIndex + 1, arr.length - 1);
        }
        System.out.println(resIndex);
    }

    static int findPivotIndex(int[] arr){
        int start = 0;
        int end = arr.length - 1;
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(end > mid  && arr[mid] > arr[mid + 1]){ //case 1
                return mid;
            }
            if(start < mid && arr[mid] < arr[mid - 1]){ //case 2
                return mid - 1;
            }
            //case 3 we want to check if there are duplicates
            if(arr[start] == arr[mid] && arr[mid] == arr[end]){
                //then these are duplicates we should increment the start and decrement the end.
                //but before doing that we should check if start and end are pivot
                if(arr[start] > arr[start + 1]){
                    return start;
                }
                if(arr[end] < arr[end - 1]){
                    return end - 1;
                }
                start ++;
                end --;
            }
            //if left side is sorted that is arr[start] < arr[mid] then pivot should be in right side
            if(arr[start] < arr[mid] || (arr[start] == arr[mid] && arr[mid] > arr[end])){ //if arr[start] == arr[mid] then we should see if the end is < arr[mid] so we can move start index
                start = mid + 1;
            } else{ //if not the case then end will be moved before to find pivot in that range.
                end = mid - 1;
            }
        }
        return -1;
    }

    static int getBinarySearchResult(int[] arr, int target, int start, int end){
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] < target){
                start = mid + 1;
            } else if(arr[mid] > target){
                end = mid - 1;
            } else{
                return mid;
            }
        }
        return -1;
    }
}
