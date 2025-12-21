package Recursion;

public class SumOfdigits {
    static void main() {
        int n = 1234567;
        int sum = 0;
        System.out.println(sumOfdigits(n, sum));
    }

    static int sumOfdigits(int n, int sum){
        if(n == 0){
            return sum;
        }
        return sumOfdigits(n/10,sum+n%10);
    }
}
