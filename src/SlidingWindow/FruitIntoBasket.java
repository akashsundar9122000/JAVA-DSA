package SlidingWindow;

import java.util.HashMap;
import java.util.Map;

public class FruitIntoBasket {
    //904
    //https://leetcode.com/problems/fruit-into-baskets/
    /**
     * ou are visiting a farm that has a single row of fruit trees arranged from left to right. The trees are represented by an integer array fruits where fruits[i] is the type of fruit the ith tree produces.
     *
     * You want to collect as much fruit as possible. However, the owner has some strict rules that you must follow:
     *
     * You only have two baskets, and each basket can only hold a single type of fruit. There is no limit on the amount of fruit each basket can hold.
     * Starting from any tree of your choice, you must pick exactly one fruit from every tree (including the start tree) while moving to the right. The picked fruits must fit in one of your baskets.
     * Once you reach a tree with fruit that cannot fit in your baskets, you must stop.
     * Given the integer array fruits, return the maximum number of fruits you can pick.
     *
     *
     *
     * Example 1:
     *
     * Input: fruits = [1,2,1]
     * Output: 3
     * Explanation: We can pick from all 3 trees.
     * Example 2:
     *
     * Input: fruits = [0,1,2,2]
     * Output: 3
     * Explanation: We can pick from trees [1,2,2].
     * If we had started at the first tree, we would only pick from trees [0,1].
     * Example 3:
     *
     * Input: fruits = [1,2,3,2,2]
     * Output: 4
     * Explanation: We can pick from trees [2,3,2,2].
     * If we had started at the first tree, we would only pick from trees [1,2].
     */

    static void main() {
        int[] fruits = {3,3,3,1,2,1,1,2,3,3,4};
        //find max length sub array with atmost 2 types of fruit
        int l = 0;
        int r = 0;
        int maxLength = 0;
        Map<Integer, Integer> res = new HashMap<>();
        while(r < fruits.length){
            res.put(fruits[r],res.getOrDefault(fruits[r], 0) + 1);
            if(res.size() > 2){
                res.put(fruits[l], res.get(fruits[l]) - 1);
                if(res.get(fruits[l]) == 0){
                    res.remove(fruits[l]);
                }
                l ++;
            } else{
                maxLength = Math.max(maxLength, r - l + 1);
            }
            r++; //moving r because dont want to reduce the max length
        }
        System.out.println(maxLength);
        System.out.println(kDistinctChar("abcddefg", 3)); //leetcode 340
    }

    static int kDistinctChar(String s, int k) {
        //your code goes here
        int l = 0;
        int r = 0;
        int maxLength = 0;
        Map<Character,Integer> res = new HashMap<>();
        while(r < s.length()){
            res.put(s.charAt(r),res.getOrDefault(s.charAt(r), 0) + 1);
            if(res.size() > k){
                res.put(s.charAt(l), res.get(s.charAt(l)) - 1);
                if(res.get(s.charAt(l)) == 0){
                    res.remove(s.charAt(l));
                }
                l ++;
            } else{
                maxLength = Math.max(maxLength, r - l + 1);
            }
            r++; //moving r because dont want to reduce the max length
        }
        return maxLength;
    }
}
