package StackAndQueue;

import java.util.Stack;

public class ValidParanthesis {
    //20
    //https://leetcode.com/problems/valid-parentheses/
    /**
     * Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
     *
     * An input string is valid if:
     *
     * Open brackets must be closed by the same type of brackets.
     * Open brackets must be closed in the correct order.
     * Every close bracket has a corresponding open bracket of the same type.
     *
     *
     * Example 1:
     *
     * Input: s = "()"
     *
     * Output: true
     *
     * Example 2:
     *
     * Input: s = "()[]{}"
     *
     * Output: true
     *
     * Example 3:
     *
     * Input: s = "(]"
     *
     * Output: false
     *
     * Example 4:
     *
     * Input: s = "([])"
     *
     * Output: true
     *
     * Example 5:
     *
     * Input: s = "([)]"
     *
     * Output: false
     *
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 104
     * s consists of parentheses only '()[]{}'.
     */

    static void main() {
        String s = "()[]{}";
        System.out.println(isValid(s));
    }
    static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for(char c : s.toCharArray()){
            if(c == '(' || c == '[' || c == '{'){
                stack.push(c);
            } else{
                if(stack.isEmpty()){
                    return false;
                }
                char x = stack.pop();
                if((c == ')' && x != '(') || (c == ']' && x != '[') ||(c == '}' && x != '{')){
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}
