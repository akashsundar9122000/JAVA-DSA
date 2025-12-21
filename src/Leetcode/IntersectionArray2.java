package Leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IntersectionArray2 {
    //Leetcode - 350
    //https://leetcode.com/problems/intersection-of-two-arrays-ii/?envType=problem-list-v2&envId=array

    /**
     * Given two integer arrays nums1 and nums2, return an array of their intersection. Each element in the result must appear as many times as it shows in both arrays and you may return the result in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: nums1 = [1,2,2,1], nums2 = [2,2]
     * Output: [2,2]
     * Example 2:
     *
     * Input: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
     * Output: [4,9]
     * Explanation: [9,4] is also accepted.
     *
     *
     * Constraints:
     *
     * 1 <= nums1.length, nums2.length <= 1000
     * 0 <= nums1[i], nums2[i] <= 1000
     */

    static void main() {
        int[] nums1 = {1,2,2,1};
        int[] nums2 = {2,2};
        System.out.println(Arrays.toString(intersect(nums1, nums2)));
    }

    static int[] intersect(int[] nums1, int[] nums2) {
        HashMap<Integer,Integer> res1 = new HashMap<>();
        HashMap<Integer,Integer> res2 = new HashMap<>();
        for(int i = 0 ; i < nums1.length ; i ++){
            res1.put(nums1[i],res1.getOrDefault(nums1[i],0) + 1);
        }
        for(int i = 0 ; i < nums2.length ; i ++){
            res2.put(nums2[i],res2.getOrDefault(nums2[i],0) + 1);
        }
        int arrSize = 0;
        ArrayList<Integer> resList = new ArrayList<>();
        for(Map.Entry<Integer,Integer> map : res1.entrySet()){
            if(res2.containsKey(map.getKey())){
                int size = 0;
                if(map.getValue() < res2.get(map.getKey())){
                    arrSize += map.getValue();
                    size = map.getValue();
                } else{
                    arrSize += res2.get(map.getKey());
                    size = res2.get(map.getKey());
                }
                for(int i = 0 ; i < size ; i ++) {
                    resList.add(map.getKey());
                }
            }
        }
        int[] ans = new int[resList.size()];
        for(int i = 0 ; i < resList.size() ; i++){
            ans[i] = resList.get(i);
        }
        return ans;
    }
}
