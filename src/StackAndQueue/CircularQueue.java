package StackAndQueue;

public class CircularQueue {
    protected int[] data;
    private static final int DEFAULT_SIZE = 10;
    int end = 0;
    int front = 0;
    int size = 0;

    public CircularQueue(){
        this(DEFAULT_SIZE);
    }

    public CircularQueue(int size){
        this.data = new int[size];
    }

    public boolean isFull(){
        return size == data.length;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public boolean insert(int item){
        if(isFull()){
            return false;
        }
        data[end ++] = item;
        end = end % data.length; //in circular queue data move in cycle
        size ++;
        return true;
    }

    public int remove() throws Exception{
        if(isEmpty()){
            throw new Exception("Queue is empty");
        }
        int remove = data[front ++];
        front = front % data.length;
        size --;
        return remove;
    }

    public int front() throws Exception{
        if(isEmpty()){
            throw new Exception("Queue is empty");
        }
        return data[front];
    }

    public void display(){
        if(isEmpty()){
            System.out.println("empty");
        }
        int i = front;
        do{
            System.out.println(data[i]);
            i ++;
            i %= data.length;
        }while (i != end);
    }
}
