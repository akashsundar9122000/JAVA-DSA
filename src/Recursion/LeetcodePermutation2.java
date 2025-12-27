package Recursion;

import java.util.ArrayList;
import java.util.List;

public class LeetcodePermutation2 {
    //47
    //https://leetcode.com/problems/permutations-ii/description/
    /**
     * Given a collection of numbers, nums, that might contain duplicates, return all possible unique permutations in any order.
     *
     *
     *
     * Example 1:
     *
     * Input: nums = [1,1,2]
     * Output:
     * [[1,1,2],
     *  [1,2,1],
     *  [2,1,1]]
     * Example 2:
     *
     * Input: nums = [1,2,3]
     * Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
     *
     *
     * Constraints:
     *
     * 1 <= nums.length <= 8
     * -10 <= nums[i] <= 10
     */
    static void main() {
        int[] nums = {1,1,2};
        List<List<Integer>> list = new ArrayList<>();
        perm(list, new ArrayList<>(), nums, new boolean[nums.length]);
        System.out.println(list);
    }

    static void perm(List<List<Integer>> list, List<Integer> temp, int[] nums, boolean[] used){
        if(temp.size() == nums.length && !list.contains(temp)){
            list.add(new ArrayList<>(temp));
        } else{
            for(int i = 0 ; i < nums.length ; i ++){
                if(used[i] || i > 0 && nums[i] == nums[i - 1] && !used[i - 1]){
                    continue;
                }
                temp.add(nums[i]);
                used[i] = true; //This will mark when the index is processed
                perm(list, temp, nums, used);
                used[i] = false;
                temp.remove(temp.size() - 1);
            }
        }
    }
}
