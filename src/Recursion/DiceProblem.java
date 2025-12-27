package Recursion;

public class DiceProblem {
    //amazon
    static void main() {
        int target = 3;
        diceRoll("", target);
    }

    static void diceRoll(String processed, int target){
        if(target == 0){
            System.out.println(processed);
            return;
        }
        for(int i = 1 ; i <= target ; i++){
            diceRoll(processed + i, target - i);
        }
    }
}
