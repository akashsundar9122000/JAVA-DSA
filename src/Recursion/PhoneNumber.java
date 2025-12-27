package Recursion;

import java.util.ArrayList;

public class PhoneNumber {
    //17
    //Google
    //https://leetcode.com/problems/letter-combinations-of-a-phone-number/description/
    /**
     * Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent. Return the answer in any order.
     *
     * A mapping of digits to letters (just like on the telephone buttons) is given below. Note that 1 does not map to any letters.
     *
     *
     *
     *
     * Example 1:
     *
     * Input: digits = "23"
     * Output: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
     * Example 2:
     *
     * Input: digits = "2"
     * Output: ["a","b","c"]
     *
     *
     * Constraints:
     *
     * 1 <= digits.length <= 4
     * digits[i] is a digit in the range ['2', '9'].
     */

    static void main() {
        String number = "23";
        printPad("", number);
        System.out.println(numberPadAsList("", number));
    }

    static void printPad(String processed, String unProcessed){
        if(unProcessed.isEmpty()){
            System.out.println(processed);
            return;
        }
        int digit = unProcessed.charAt(0) - '0'; //this will convert '2' to 2.
        int start = (digit - 2) * 3; //In number pad 2 has "abc" so to get the char the start will be (2-2) * 3 = 0 so when we do 'a' + 0 the result will be a, similarly 'a'+1 = b and so on
        if(digit > 7){ //for digits from 7 the start will be incremented by one because digit 7 have 4 characters 'pqrs'
            start ++;
        }
        int len = start + 3;
        if(digit == 7 || digit == 9){ //for digits 7 and 9 the total length is 4 in the number pad.
            len ++;
        }
        for(int i = start; i < len ; i ++){
            char ch = (char) ('a' + i);
            printPad(processed + ch, unProcessed.substring(1));
        }
    }

    static ArrayList<String> numberPadAsList(String processed, String unProcessed){
        if(unProcessed.isEmpty()){
            ArrayList<String> res = new ArrayList<>();
            res.add(processed);
            return res;
        }
        ArrayList<String> ans = new ArrayList<>();
        int digit = unProcessed.charAt(0) - '0';
        int start = (digit - 2) * 3;
        if(digit > 7){
            start ++;
        }
        int end = start + 3;
        if(digit == 7 || digit == 9){
            end ++;
        }
        for(int i = start ; i < end ; i ++){
            char ch = (char) ('a' + i);
            ans.addAll(numberPadAsList(processed + ch, unProcessed.substring(1)));
        }
        return ans;
    }
}
