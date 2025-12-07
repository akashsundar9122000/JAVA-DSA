package BinarySearch;

public class FindGuessingNumber {
    //leetcode - 374 - easy
    //https://leetcode.com/problems/guess-number-higher-or-lower/description/
    /**
     * We are playing the Guess Game. The game is as follows:
     *
     * I pick a number from 1 to n. You have to guess which number I picked (the number I picked stays the same throughout the game).
     *
     * Every time you guess wrong, I will tell you whether the number I picked is higher or lower than your guess.
     *
     * You call a pre-defined API int guess(int num), which returns three possible results:
     *
     * -1: Your guess is higher than the number I picked (i.e. num > pick).
     * 1: Your guess is lower than the number I picked (i.e. num < pick).
     * 0: your guess is equal to the number I picked (i.e. num == pick).
     * Return the number that I picked.
     *
     *
     *
     * Example 1:
     *
     * Input: n = 10, pick = 6
     * Output: 6
     * Example 2:
     *
     * Input: n = 1, pick = 1
     * Output: 1
     * Example 3:
     *
     * Input: n = 2, pick = 1
     * Output: 1
     */

    static void main() {
        int number = 10;
        int pick = 6;
        int start = 1;
        int end = number;
        while (start <= number){
            int mid = start + (end - start) / 2;
            int value = guess(mid,pick);
            if(value == -1){
                end = mid - 1;
            } else if (value == 1) {
                start = mid + 1;
            } else {
                System.out.println(mid);
                return;
            }
        }
    }

    static int guess(int value, int number){
        if(value < number){
            return 1;
        } else if (value > number) {
            return -1;
        }
        return 0;
    }
}
