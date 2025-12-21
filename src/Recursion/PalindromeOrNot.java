package Recursion;

public class PalindromeOrNot {
    static void main() {
        int n = 613316;
        int rev = 0;
        System.out.println(n == palindrome(n, rev));
    }

    static int palindrome(int n, int rev){
        if(n == 0){
            return rev;
        }
        return palindrome(n / 10, rev * 10 + (n % 10));
    }
}
