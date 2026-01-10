package StackAndQueue;

import java.util.Arrays;

public class TwoStacks {
    //https://www.hackerrank.com/challenges/game-of-two-stacks/problem
    /**
     *
     */

    static void main() {
        int[] a = {4,2,4,6,1};
        int[] b = {2,1,8,5};
        int maxSum = 10;
        System.out.println(twoStack(a, b, maxSum,0, 0) - 1); //-1 because count will be 1 more
    }

    static int twoStack(int[] a, int[] b, int total, int sum, int count){
        if(sum > total || a.length == 0 || b.length == 0){
            return count; //coz even in base case we add 1 count extra we want to remove that
        }

        int ans1 = twoStack(Arrays.copyOfRange(a, 1, a.length), b,total, sum + a[0], count + 1);
        int ans2 = twoStack(a, Arrays.copyOfRange(b, 1, b.length),total, sum + a[0], count + 1);
        return Math.max(ans1, ans2);
    }
}
