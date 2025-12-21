package Leetcode;

import java.util.ArrayList;
import java.util.List;

public class SummaryRanges {
    //leetcode - 228
    //https://leetcode.com/problems/summary-ranges/description/?envType=problem-list-v2&envId=array

    /**
     * ou are given a sorted unique integer array nums.
     *
     * A range [a,b] is the set of all integers from a to b (inclusive).
     *
     * Return the smallest sorted list of ranges that cover all the numbers in the array exactly. That is, each element of nums is covered by exactly one of the ranges, and there is no integer x such that x is in one of the ranges but not in nums.
     *
     * Each range [a,b] in the list should be output as:
     *
     * "a->b" if a != b
     * "a" if a == b
     *
     *
     * Example 1:
     *
     * Input: nums = [0,1,2,4,5,7]
     * Output: ["0->2","4->5","7"]
     * Explanation: The ranges are:
     * [0,2] --> "0->2"
     * [4,5] --> "4->5"
     * [7,7] --> "7"
     * Example 2:
     *
     * Input: nums = [0,2,3,4,6,8,9]
     * Output: ["0","2->4","6","8->9"]
     * Explanation: The ranges are:
     * [0,0] --> "0"
     * [2,4] --> "2->4"
     * [6,6] --> "6"
     * [8,9] --> "8->9"
     *
     *
     * Constraints:
     *
     * 0 <= nums.length <= 20
     * -231 <= nums[i] <= 231 - 1
     * All the values of nums are unique.
     * nums is sorted in ascending order.
     */

    static void main() {
        int[] nums = {0,1,2,4,5,7};
        System.out.println(summaryRanges(nums));
    }

    static List<String> summaryRanges(int[] nums) {
        List<String> res = new ArrayList<>();
        int i = 0;
        while(i < nums.length){
            StringBuilder sb = new StringBuilder();
            int j = i;
            while(j + 1 < nums.length && nums[j + 1] == nums[j] + 1){
                j ++;
            }
            if(i == j){
                sb.append(nums[i]);
            } else{
                sb.append(nums[i]);
                sb.append("->");
                sb.append(nums[j]);
            }
            res.add(sb.toString());
            i=j+1;
        }
        return res;
    }
}
