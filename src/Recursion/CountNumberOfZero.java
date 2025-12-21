package Recursion;

public class CountNumberOfZero {
    static void main() {
        int n = 1335;
        System.out.println(count(n,0));
    }

    static int count(int n, int c){
        if(n == 0){
            return c;
        }
        if(n % 10 == 0){
            c += 1;
        }
        return count(n/10, c);
    }
}
