package TwoPointer;

public class ReverseVowel {
    //345
    //https://leetcode.com/problems/reverse-vowels-of-a-string/?envType=problem-list-v2&envId=two-pointers
    /**
     * Given a string s, reverse only all the vowels in the string and return it.
     *
     * The vowels are 'a', 'e', 'i', 'o', and 'u', and they can appear in both lower and upper cases, more than once.
     *
     *
     *
     * Example 1:
     *
     * Input: s = "IceCreAm"
     *
     * Output: "AceCreIm"
     *
     * Explanation:
     *
     * The vowels in s are ['I', 'e', 'e', 'A']. On reversing the vowels, s becomes "AceCreIm".
     *
     * Example 2:
     *
     * Input: s = "leetcode"
     *
     * Output: "leotcede"
     *
     *
     *
     * Constraints:
     *
     * 1 <= s.length <= 3 * 105
     * s consist of printable ASCII characters.
     */

    static void main() {
        String s = "leetcode";
        int i = 0;
        int j = s.length() - 1;
        char[] x = s.toCharArray();
        char[] res = s.toCharArray();
        while(i < j){
            char vowel1 = x[i];
            if(isVowel(vowel1)){
                while(j > i){
                    char vowel2 = x[j];
                    if(isVowel(vowel2)){
                        char temp = res[i];
                        res[i] = res[j];
                        res[j] = temp;
                        j --;
                        break;
                    }
                    j --;
                }
            }
            i ++;
        }
        System.out.println(new String(res));
    }

    static boolean isVowel(char vowel1){
        if(vowel1 == 'a' || vowel1 == 'e' || vowel1 == 'i' || vowel1 == 'o' || vowel1 == 'u' || vowel1 == 'A' || vowel1 == 'E' || vowel1 == 'I' || vowel1 == 'O' || vowel1 == 'U'){
            return true;
        }
        return false;
    }
}
