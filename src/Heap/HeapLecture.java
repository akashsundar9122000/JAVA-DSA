package Heap;

import java.util.ArrayList;
import java.util.Arrays;

public class HeapLecture<T extends  Comparable<T>> {

    //heap sort
    //every insert or delete takes O(logN) and for N numbers its O(NlogN)
    private ArrayList<T> list;

    public HeapLecture(){
        list = new ArrayList<>();
    }

    private void swap(int first, int second){
        T temp = list.get(first);
        list.set(first, list.get(second));
        list.set(second, temp);
    }

    private int parent(int index){
        //in heap parent is i / 2 of child but i starts from 0 so (i - 1) / 2
        return (index - 1) / 2;
    }

    private int left(int index){
        //in heap
        return 2 * index + 1;
    }

    private int right(int index){
        //in heap
        return 2 * index + 2;
    }

    public void insert(T value){
        list.add(value);
        //added number to last index. Now want to traverse up and swap if the parent number is less than this number
        upheap(list.size() - 1);
    }

    private void upheap(int index){
        if(index == 0){
            return;
        }
        int p = parent(index);
        if(list.get(index).compareTo(list.get(p)) < 0){
            //if this index value is less than parent then swap;
            swap(index, p);
            //if swapped one time need to check for above parent also
            upheap(p);
        }
    }

    public T remove() throws Exception{
        if(list.isEmpty()){
            throw new Exception("List is empty");
        }
        //to remove, get the number from 0th index
        T temp = list.get(0);
        //now swap the last number to 0th index
        T last = list.remove(list.size() - 1);
        if(!list.isEmpty()){
            list.set(0, last);
        }
        //now perform downheap (i.e) check from top to go down and swap accordingly
        downHeap(0);
        return temp;
    }

    private void downHeap(int index){
        //check the current index value with left and right and then swap
        int min = index;
        int left = left(index);
        int right = right(index);
        if(left < list.size() && list.get(min).compareTo(list.get(left)) > 0){
            //if current index value is > than left value swap it because small should come up
            min = left;
        }

        if(right < list.size() && list.get(min).compareTo(list.get(right)) > 0){
            //if current index value is > than right value swap it because small should come up
            min = right;
        }
        if(min != index){
            //if left or right is less then swap
            swap(min, index);
            //check for further child
            downHeap(min);
        }
    }

    public ArrayList<T> heapSort() throws Exception{
        ArrayList<T> data = new ArrayList<>();
        while (!list.isEmpty()){
            data.add(this.remove());
        }
        return data;
    }

    static void main() throws Exception{
        HeapLecture<Integer> heapLecture = new HeapLecture<>();

        heapLecture.insert(34);
        heapLecture.insert(45);
        heapLecture.insert(22);
        heapLecture.insert(89);
        heapLecture.insert(76);
        //will be removed in ascending order because of heap
        ArrayList list1 = heapLecture.heapSort();
        System.out.println(list1);
    }
}
