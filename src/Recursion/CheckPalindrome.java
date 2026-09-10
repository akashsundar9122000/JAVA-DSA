package Recursion;

public class CheckPalindrome {
    //Check if the given string is palindrome or not using recursion

    static void main() {
        String str = "11211";
        System.out.println(isPalindrome(0, str));
    }
    static boolean isPalindrome(int index, String str){
        if(index >= str.length()){
            return true;
        }
        if(str.charAt(index) != str.charAt(str.length() - index - 1)){
            return false;
        }
        return isPalindrome(index + 1, str);
    }
}
