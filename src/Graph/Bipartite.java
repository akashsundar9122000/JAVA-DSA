package Graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Bipartite {
    //785
    //https://leetcode.com/problems/is-graph-bipartite/description/

    /**
     * There is an undirected graph with n nodes, where each node is numbered between 0 and n - 1. You are given a 2D array graph, where graph[u] is an array of nodes that node u is adjacent to. More formally, for each v in graph[u], there is an undirected edge between node u and node v. The graph has the following properties:
     *
     * There are no self-edges (graph[u] does not contain u).
     * There are no parallel edges (graph[u] does not contain duplicate values).
     * If v is in graph[u], then u is in graph[v] (the graph is undirected).
     * The graph may not be connected, meaning there may be two nodes u and v such that there is no path between them.
     * A graph is bipartite if the nodes can be partitioned into two independent sets A and B such that every edge in the graph connects a node in set A and a node in set B.
     *
     * Return true if and only if it is bipartite.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: graph = [[1,2,3],[0,2],[0,1,3],[0,2]]
     * Output: false
     * Explanation: There is no way to partition the nodes into two independent sets such that every edge connects a node in one and a node in the other.
     * Example 2:
     *
     *
     * Input: graph = [[1,3],[0,2],[1,3],[0,2]]
     * Output: true
     * Explanation: We can partition the nodes into two sets: {0, 2} and {1, 3}.
     *
     *
     * Constraints:
     *
     * graph.length == n
     * 1 <= n <= 100
     * 0 <= graph[u].length < n
     * 0 <= graph[u][i] <= n - 1
     * graph[u] does not contain u.
     * All the values of graph[u] are unique.
     * If graph[u] contains v, then graph[v] contains u.
     */

    static void main() {
        int[][] graph = {
                {1,2,3},
                {0,2},
                {0,1,3},
                {0,2}
        };
        System.out.println(isBipartite(graph));
    }

    static boolean isBipartite(int[][] graph) {
        int n = graph.length;
        //create a color array and fill it with -1 initially then during process we can add 2 different colors
        int[] color = new int[n];
        Arrays.fill(color, -1);
        for(int i = 0 ; i < n ; i ++){
            if(color[i] == -1){
                // if(!bfs(color, graph, i, 0)){ //1ms
                //     return false;
                // }
                if(!dfs(color, graph, i, 0)){ //0ms
                    return false;
                }
            }
        }
        return true;
    }

    static boolean bfs(int[] color, int[][] graph, int index, int startColor){
        ///initial color will be 0
        color[index] = startColor;
        Queue<Integer> queue = new LinkedList<>();
        queue.add(index);
        while(!queue.isEmpty()){
            int node = queue.poll();
            //get the nodes nebighbours and mark its color if not already visited
            for(int n : graph[node]){
                if(color[n] == -1){
                    queue.offer(n);
                    //1 - color[node] because neighbou color should be different
                    color[n] = 1 - color[node];
                }
                //if(neighbour have same color then its not bipartite)
                if(color[n] == color[node]){
                    return false;
                }
            }
        }
        return true;
    }

    static boolean dfs(int[] color, int[][] graph, int index, int currColor){
        color[index] = currColor;
        //get neighbouring nodes
        for(int i : graph[index]){
            if(color[i] == -1){
                //color for neighbouringn odes changes 1 - currColor
                if(!dfs(color, graph, i, 1 - currColor)){
                    return false;
                }
            }
            //if current and neibhour color is equal not a bipartite
            if(color[i] == currColor){
                return false;
            }
        }
        return true;
    }
}
