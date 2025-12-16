package Recursion;

public class FibonacciNumber {
    //Leetcode - 509
    //https://leetcode.com/problems/fibonacci-number/description/
    /**
     * Fibonacci number is the sum of its previous 2 fibonacci number
     * Fibo(N) = Fibo(N-1) + Fibo(N+1)
     * 0,1,1,2,3,5,8,13,...
     *
     */

    static void main() {
        System.out.println(fibo(2));
    }

    static int fibo(int n){

        //base conditiin
        if(n < 2){ //if n == 0 or n == 1
            return n;
        }

        return fibo(n - 1) + fibo(n - 2);
    }
}
