package BinarySearch;

public class FloorProblem {
    //Floor problem returns the largest value which is <= the target from the sorted array.
    //input - Array of size N and the target.
    //Since it is sorted array we can use binary search for the process.
    //worst case time complexity of binary search is O(logn)
    static void main() {
        int[] array = {1,2,6,8,13,16,27,48,97};
        int target = 14;
        int result = getFloorNumber(array, target);
        System.out.println(result);
    }

    static int getFloorNumber(int[] array, int target) {
        int start = 0;
        int end = array.length - 1;
        if(target > array[end]){
            return -1;
        }
        while (start <= end){
            int mid = start + (end - start) / 2;
            if(array[mid] == target){
                return array[mid];
            } else if(array[mid] > target){
                end = mid - 1;
            } else{
                start = mid + 1;
            }
        }
        return array[end];
    }
}
