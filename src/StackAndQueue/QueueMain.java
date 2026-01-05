package StackAndQueue;

public class QueueMain {
    static void main() throws Exception {
        CustomQueue queue = new CustomQueue(5);

        queue.insert(10);
        queue.insert(20);
        queue.insert(30);
        queue.insert(40);
        queue.insert(50);
        queue.insert(60);

        queue.display();

        queue.remove();
        queue.remove();
        queue.remove();
        queue.remove();
        queue.remove();
        queue.remove();
    }
}
