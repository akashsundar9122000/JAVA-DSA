package Recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubSequence {
    static void main() {
        String s = "abc";
        subSequence("", s); //proceesed will be empty initially
        System.out.println(subSequenceAsList("", s));
        subsequenceAscii("", s);
    }

    static void subSequence(String processed, String s){
        if(s.isEmpty()){
            System.out.println(processed);
            return;
        }
        subSequence(processed + s.charAt(0), s.substring(1)); //taking a character
        subSequence(processed, s.substring(1)); //rejecting a character
    }

    static ArrayList<String> subSequenceAsList(String processed, String s){
        if(s.isEmpty()){
            ArrayList<String> res = new ArrayList<>();
            res.add(processed);
            return res;
        }

        ArrayList<String> left = subSequenceAsList(processed + s.charAt(0), s.substring(1));
        ArrayList<String> right = subSequenceAsList(processed, s.substring(1));
        left.addAll(right);
        return left;
    }

    static void subsequenceAscii(String p, String up){
        if(up.isEmpty()){
            System.out.println(p);
            return;
        }

        subsequenceAscii(p + up.charAt(0), up.substring(1));
        subsequenceAscii(p, up.substring(1));
        subsequenceAscii(p + (up.charAt(0) + 0), up.substring(1));
    }
}
