package Recursion;

public class Factorial {
    static void main() {
        int fact = 10;
        System.out.println(fact(fact));
    }

    static long fact(int fact){
        if(fact < 2){
            return fact;
        }
        return fact * fact(fact - 1);
    }
}
