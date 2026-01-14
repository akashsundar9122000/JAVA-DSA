package StackAndQueue;

import java.util.Stack;

public class EvaluateReversePolishNotation {
    //https://leetcode.com/problems/evaluate-reverse-polish-notation/?envType=problem-list-v2&envId=dsa-linear-shoal-stack
    /**
     * You are given an array of strings tokens that represents an arithmetic expression in a Reverse Polish Notation.
     *
     * Evaluate the expression. Return an integer that represents the value of the expression.
     *
     * Note that:
     *
     * The valid operators are '+', '-', '*', and '/'.
     * Each operand may be an integer or another expression.
     * The division between two integers always truncates toward zero.
     * There will not be any division by zero.
     * The input represents a valid arithmetic expression in a reverse polish notation.
     * The answer and all the intermediate calculations can be represented in a 32-bit integer.
     *
     *
     * Example 1:
     *
     * Input: tokens = ["2","1","+","3","*"]
     * Output: 9
     * Explanation: ((2 + 1) * 3) = 9
     * Example 2:
     *
     * Input: tokens = ["4","13","5","/","+"]
     * Output: 6
     * Explanation: (4 + (13 / 5)) = 6
     * Example 3:
     *
     * Input: tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
     * Output: 22
     * Explanation: ((10 * (6 / ((9 + 3) * -11))) + 17) + 5
     * = ((10 * (6 / (12 * -11))) + 17) + 5
     * = ((10 * (6 / -132)) + 17) + 5
     * = ((10 * 0) + 17) + 5
     * = (0 + 17) + 5
     * = 17 + 5
     * = 22
     *
     *
     * Constraints:
     *
     * 1 <= tokens.length <= 104
     * tokens[i] is either an operator: "+", "-", "*", or "/", or an integer in the range [-200, 200].
     */

    static void main() {
        String[] tokens = {"10","6","9","3","+","-11","*","/","*","17","+","5","+"};
        if(tokens.length == 1){
            System.out.println(Integer.parseInt(tokens[0]));
            return;
        }
        Stack<Integer> stack = new Stack();
        long res = 0;
        for(int i = 0 ; i < tokens.length ; i ++){
            boolean ans = true;
            if(tokens[i].equals("+")){

                    res = stack.pop() + stack.pop();

            }
            else if(tokens[i].equals("-")){

                    int a = stack.pop();
                    int b = stack.pop();
                    res = b - a;

            }
            else if(tokens[i].equals("*")){

                    res = stack.pop() * stack.pop();

            }
            else if(tokens[i].equals("/")){

                    int a = stack.pop();
                    int b = stack.pop();
                    res = b / a;

            }
            else{
                ans = false;
                stack.push(Integer.parseInt(tokens[i]));
            }
            if(ans)
            stack.push(Integer.parseInt(String.valueOf(res)));
        }
        System.out.println((int) res);
    }
}
