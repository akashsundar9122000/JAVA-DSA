package BinarySearch;

public class FindTargetInInfiniteArray {
    // We don't know the size of the array so we cant use array.length.
    //Instead we can start with first 2 number see whether the target lies between them. If not we can move start to 3rd element and end to 2^ like 4 then 8,...
    //If it lies between then we can perform binary search since it is a sorted array.
    static void main() {
        int arr[] = {3, 5, 7, 9, 10, 90, 100, 130, 140, 160, 170};
        int target = 170;
        System.out.println(findRange(arr, target));
    }
    static int findRange(int[] arr, int target){
        int start = 0;
        int end = 1;
        try {
            while (target > arr[end]) {
                int newStart = end + 1; //new start index will be from the next index of end
                end = end + (end - start + 1) * 2; //end - start + 1 is the size of the previously processed chunk now doubling the size for next chunk
                start = newStart;
            }
        } catch (ArrayIndexOutOfBoundsException e){
            end = arr.length; //Need to check for alternative approach. This cant be true coz this is infinite length array
        }
            return binarySearch(arr, target, start, end);
    }

    static int binarySearch(int[] arr, int target, int start, int end){
        while(start <= end){
            int mid = start + (end - start) / 2;
            if(arr[mid] == target){
                return mid;
            } else if (target > arr[mid]) {
                start = mid + 1;
            } else{
                end = mid - 1;
            }
        }
        return -1;
    }
}
