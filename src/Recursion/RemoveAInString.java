package Recursion;

public class RemoveAInString {
    static void main() {
        String s = "abddsavfsa";
        skipA("",s); //processed is empty initially
    }

    static void skipA(String process, String unProcess){
        if(unProcess.isEmpty()){
            System.out.println(process);
            return;
        }
        char x = unProcess.charAt(0);
        if(x == 'a'){
            skipA(process, unProcess.substring(1));// if char is 'a' dont add that to processed and move to next range
        } else{
            skipA(process + x, unProcess.substring(1)); //if not 'a' add to processed.
        }
    }
}
