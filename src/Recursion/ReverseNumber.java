package Recursion;

public class ReverseNumber {
    static void main() {
        int n = 654535;
        int res = 0;
        System.out.println(reverse(n, res));
    }

    static int reverse(int n, int res){
        if(n == 0){
            return res;
        }

        return reverse(n/10, res * 10 + (n%10));
    }
}
