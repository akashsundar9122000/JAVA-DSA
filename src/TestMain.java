import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMain {
    static void main() {
        List<List<String >> res = new ArrayList<>();
        List<String> ans = new ArrayList<>();
        String[][] q = {{"1", "2", "5"}, {"1", "3"}};
        for(String[] a: q){
            res.add(Arrays.asList(a));
        }

        for(int i = 0 ; i < res.size() ; i ++){
            List<String> val = res.get(i);
            String x = val.get(0);
            if(val.size() > 1){
                x += "->" + val.get(val.size() - 1);
            }
            ans.add(x);
        }
        System.out.println(ans);
    }
}
