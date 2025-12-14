package Leetcode;

import java.util.ArrayList;
import java.util.*;

public class Coupon {
    //leetcode - 3606
    //https://leetcode.com/problems/coupon-code-validator/description/?envType=daily-question&envId=2025-12-13
    /**
     *You are given three arrays of length n that describe the properties of n coupons: code, businessLine, and isActive. The ith coupon has:
     *
     * code[i]: a string representing the coupon identifier.
     * businessLine[i]: a string denoting the business category of the coupon.
     * isActive[i]: a boolean indicating whether the coupon is currently active.
     * A coupon is considered valid if all of the following conditions hold:
     *
     * code[i] is non-empty and consists only of alphanumeric characters (a-z, A-Z, 0-9) and underscores (_).
     * businessLine[i] is one of the following four categories: "electronics", "grocery", "pharmacy", "restaurant".
     * isActive[i] is true.
     * Return an array of the codes of all valid coupons, sorted first by their businessLine in the order: "electronics", "grocery", "pharmacy", "restaurant", and then by code in lexicographical (ascending) order within each category.
     *
     *
     *
     * Example 1:
     *
     * Input: code = ["SAVE20","","PHARMA5","SAVE@20"], businessLine = ["restaurant","grocery","pharmacy","restaurant"], isActive = [true,true,true,true]
     *
     * Output: ["PHARMA5","SAVE20"]
     *
     * Explanation:
     *
     * First coupon is valid.
     * Second coupon has empty code (invalid).
     * Third coupon is valid.
     * Fourth coupon has special character @ (invalid).
     * Example 2:
     *
     * Input: code = ["GROCERY15","ELECTRONICS_50","DISCOUNT10"], businessLine = ["grocery","electronics","invalid"], isActive = [false,true,true]
     *
     * Output: ["ELECTRONICS_50"]
     *
     * Explanation:
     *
     * First coupon is inactive (invalid).
     * Second coupon is valid.
     * Third coupon has invalid business line (invalid).
     */

    static void main() {
        String[]  code = {"SAVE20","","PHARMA5","SAVE@20"};
        String[] businessLine = {"restaurant","grocery","pharmacy","restaurant"};
        boolean[] isActive = {true,true,true,true};
        ArrayList<String> res = new ArrayList();
        List<String> businessList = new ArrayList<>();
        businessList.add("electronics");
        businessList.add("grocery");
        businessList.add("pharmacy");
        businessList.add("restaurant");
        TreeMap<String, ArrayList<String>> map = new TreeMap<>();
        for(int i = 0 ; i < code.length ; i ++){
            if(isAlphaNumericWithUnderscore(code[i]) && businessList.contains(businessLine[i]) && isActive[i]){
                if(map.containsKey(businessLine[i])){
                    ArrayList<String> arr = map.get(businessLine[i]);
                    arr.add(code[i]);
                } else{
                    ArrayList<String> set = new ArrayList<>();
                    set.add(code[i]);
                    map.put(businessLine[i],set);
                }
            }
        }
        map.forEach((key, value) ->{
            Collections.sort(value);
            res.addAll(value);
        });
        System.out.println(res); //19ms solution
    }

    static boolean isAlphaNumericWithUnderscore(String code){
        return code != null && code.matches("\\w+");
    }
}

/**
 * Another way
 * List<String> electronics = new ArrayList<>();
 *         List<String> grocery = new ArrayList<>();
 *         List<String> pharmacy = new ArrayList<>();
 *         List<String> restaurant = new ArrayList<>();
 *
 *         for (int i = 0; i < code.length; i++) {
 *             if (!isActive[i] || code[i].isEmpty()) continue;
 *
 *             boolean validCode = true;
 *             for (char c : code[i].toCharArray()) {
 *                 if (!Character.isLetterOrDigit(c) && c != '_') {
 *                     validCode = false;
 *                     break;
 *                 }
 *             }
 *
 *             if (validCode) {
 *                 if (businessLine[i].equals("electronics")) {
 *                     electronics.add(code[i]);
 *                 }
 *                 else if (businessLine[i].equals("grocery")){
 *                     grocery.add(code[i]);
 *                 }
 *                 else if (businessLine[i].equals("pharmacy")){
 *                     pharmacy.add(code[i]);
 *                 }
 *                 else if (businessLine[i].equals("restaurant")){
 *                     restaurant.add(code[i]);
 *                 }
 *             }
 *         }
 *
 *         Collections.sort(electronics);
 *         Collections.sort(grocery);
 *         Collections.sort(pharmacy);
 *         Collections.sort(restaurant);
 *
 *         List<String> result = new ArrayList<>();
 *         result.addAll(electronics);
 *         result.addAll(grocery);
 *         result.addAll(pharmacy);
 *         result.addAll(restaurant);
 *
 *         return result;
 *
 *         4ms solution
 */
