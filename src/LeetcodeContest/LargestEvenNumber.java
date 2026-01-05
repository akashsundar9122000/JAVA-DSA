package LeetcodeContest;

public class LargestEvenNumber {


    static void main() {
        String s = "1112";
        System.out.println(largestEven(s));
    }
    static String largestEven(String s) {
        String x = "";
        for(int i = s.length() - 1 ; i >= 0 ; i --){
            int digit = s.charAt(i) - '0';
            if((digit & 1) != 1){
                return s.substring(0,i + 1);
            }
        }
        return x;
    }
}
