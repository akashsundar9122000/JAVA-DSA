package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;

public class AlternatingSubstring {

    static void main() {
        String s = "ABA";
        int[][] queries = {{2,1,2},{1,1},{2,0,2}};
        int[] ans = minDeletions(s,queries);
        for(int nums: ans){
            System.out.print(nums + " ");
        }
    }

    static int[] minDeletions(String s, int[][] queries) {
        ArrayList<Integer> ans = new ArrayList<>();
        int index = 0;
        for(int i = 0 ; i < queries.length ; i ++){
            try{
                int rValue = queries[i][2];
                int findValue = findAlternatingCount(s, queries[i][1], queries[i][2]);
                ans.add(findValue);
            }
            catch(Exception e){
                StringBuilder res = new StringBuilder(s);
                if(s.charAt(queries[i][1]) == 'A'){
                    res.setCharAt(queries[i][1], 'B');
                } else{
                    res.setCharAt(queries[i][1], 'A');
                }
                s = res.toString();
            }
        }
        //int[] arr =
        return ans.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }

    static int findAlternatingCount(String s, int start, int end){
        int count = 0;
        for(int i = start ; i < end ; i ++){
            if(s.charAt(i) == s.charAt(i + 1)){
                count ++;
            }
        }
        return count;
    }
}
