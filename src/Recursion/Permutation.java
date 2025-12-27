package Recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permutation {
    static void main() {
        String s = "abc";
        permutation("", s); //initially processed will be empty
        System.out.println(permutationAsList("", s));
        System.out.println(permutationCount("", s));
        List<List<Character>> res = new ArrayList<>();
        backtrackMethod(res, new ArrayList<>(), s);
        System.out.println(res);
    }

    static void permutation(String processed, String s){
        if(s.isEmpty()){
            System.out.println(processed);
            return;
        }
        for(int i = 0 ; i <= processed.length() ; i ++){ //No. of recursive calls = size of processed + 1
            String first = processed.substring(0, i); //The string will splitted as the first the new character should be inserted at begining
            String second = processed.substring(i, processed.length()); // Then new character should be inserted at the ith positionn (may be in the middle or at the last)
            String p = first + s.charAt(0) + second; //New permutation string
            permutation(p, s.substring(1));
        }
    }

    static ArrayList<String> permutationAsList(String processed, String s){
        if(s.isEmpty()){
            ArrayList<String> list = new ArrayList<>();
            list.add(processed);
            return list;
        }
        ArrayList<String> ans = new ArrayList<>();
        for(int i = 0 ; i <= processed.length() ; i ++){
            String first = processed.substring(0, i);
            String second = processed.substring(i, processed.length());
            String p = first + s.charAt(0) + second;
            ans.addAll(permutationAsList(p, s.substring(1)));
        }
        return ans;
    }

    static int permutationCount(String processed, String s){
        if(s.isEmpty()){
            return 1;
        }
        int count = 0;
        for(int i = 0 ; i <= processed.length() ; i ++){ //No. of recursive calls = size of processed + 1
            String first = processed.substring(0, i); //The string will splitted as the first the new character should be inserted at begining
            String second = processed.substring(i, processed.length()); // Then new character should be inserted at the ith positionn (may be in the middle or at the last)
            String p = first + s.charAt(0) + second; //New permutation string
            count += permutationCount(p, s.substring(1));
        }
        return count;
    }

    static void backtrackMethod(List<List<Character>> list, List<Character> temp, String s){
        if(temp.size() == s.length()){
            list.add(new ArrayList<>(temp));
        }else {
            for (int i = 0; i < s.length(); i++) {
                if (temp.contains(s.charAt(i))) {
                    continue;
                }
                temp.add(s.charAt(i));
                backtrackMethod(list, temp, s);
                temp.remove(temp.size() - 1);
            }
        }
    }
}
