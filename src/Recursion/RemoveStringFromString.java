package Recursion;

public class RemoveStringFromString {
    static void main() {
        String s = "sdfsdappledfds";
        String remove = "apple";
        System.out.println(removeString(s, remove));
    }

    static String removeString(String s, String remove){
        if(s.isEmpty()){
            return "";
        }
        if(s.startsWith(remove)){
            return removeString(s.substring(remove.length()), remove);
        } else{
            return s.charAt(0) + removeString(s.substring(1), remove);
        }
    }
}
