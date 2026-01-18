package LeetcodeContest;

import java.util.Arrays;

public class MaxCapacityWithinBudget {
    //https://leetcode.com/contest/weekly-contest-485/problems/maximum-capacity-within-budget/description/
    static void main() {
        int[] costs = {1,7,3};
        int[] capacity = {7,3,5};
        int budget = 13;
        System.out.println(maxCapacity(costs, capacity, budget));
    }

    static int maxCapacity(int[] costs, int[] capacity, int budget) {
        int n = costs.length;

        // store input midway
        int[][] res = new int[n][2];
        for (int i = 0; i < n; i++) {
            res[i][0] = costs[i];
            res[i][1] = capacity[i];
        }

        // sort by cost
        Arrays.sort(res, (a, b) -> a[0] - b[0]);

        // prefix max capacity (LEFT side only)
        int[] prefixMax = new int[n];
        prefixMax[0] = res[0][1];
        for (int i = 1; i < n; i++) {
            prefixMax[i] = Math.max(prefixMax[i - 1], res[i][1]);
        }

        int ans = 0;

        for (int i = 0; i < n; i++) {
            int costI = res[i][0];
            int capI = res[i][1];

            // single machine
            if (costI < budget) {
                ans = Math.max(ans, capI);
            }

            int remaining = budget - costI - 1;
            if (remaining < 0) continue;

            int j = upperBound(res, remaining);

            // must be strictly before i to avoid reuse
            if (j >= i) j = i - 1;

            if (j >= 0) {
                ans = Math.max(ans, capI + prefixMax[j]);
            }
        }

        return ans;
    }

    // last index with cost <= target
    static int upperBound(int[][] res, int target) {
        int l = 0, r = res.length - 1, ans = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (res[mid][0] <= target) {
                ans = mid;
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return ans;
    }
}
