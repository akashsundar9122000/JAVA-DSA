package Graph;

import java.util.ArrayList;
import java.util.List;

public class FindEventualState {
    //802
    //https://leetcode.com/problems/find-eventual-safe-states/description/

    /**
     * There is a directed graph of n nodes with each node labeled from 0 to n - 1. The graph is represented by a 0-indexed 2D integer array graph where graph[i] is an integer array of nodes adjacent to node i, meaning there is an edge from node i to each node in graph[i].
     * <p>
     * A node is a terminal node if there are no outgoing edges. A node is a safe node if every possible path starting from that node leads to a terminal node (or another safe node).
     * <p>
     * Return an array containing all the safe nodes of the graph. The answer should be sorted in ascending order.
     * <p>
     * <p>
     * <p>
     * Example 1:
     * <p>
     * Illustration of graph
     * Input: graph = [[1,2],[2,3],[5],[0],[5],[],[]]
     * Output: [2,4,5,6]
     * Explanation: The given graph is shown above.
     * Nodes 5 and 6 are terminal nodes as there are no outgoing edges from either of them.
     * Every path starting at nodes 2, 4, 5, and 6 all lead to either node 5 or 6.
     * Example 2:
     * <p>
     * Input: graph = [[1,2,3,4],[1,2],[3,4],[0,4],[]]
     * Output: [4]
     * Explanation:
     * Only node 4 is a terminal node, and every path starting at node 4 leads to node 4.
     * <p>
     * <p>
     * Constraints:
     * <p>
     * n == graph.length
     * 1 <= n <= 104
     * 0 <= graph[i].length <= n
     * 0 <= graph[i][j] <= n - 1
     * graph[i] is sorted in a strictly increasing order.
     * The graph may contain self-loops.
     * The number of edges in the graph will be in the range [1, 4 * 104].
     */

    static void main() {
        int[][] graph = {
                {1, 2},
                {2, 3},
                {5},
                {0},
                {5},
                {},
                {}
        };
        System.out.println(eventualSafeNodes(graph));
    }

    static List<Integer> eventualSafeNodes(int[][] graph) {
        //List<List<Integer>> adjList = new ArrayList<>();
        int n = graph.length;
        //here graph itself an adjList because every index values says to which node it goes to for eg index 0 = [1,2] so 0 node goes to 1 and 2 and so on
        /**for(int i = 0 ; i < n ; i ++){
         adjList.add(new ArrayList<>());
         }
         //create adjList
         for(int i = 0 ; i < n ; i ++){
         for(int j : graph[i]){
         adjList.get(i).add(j);
         }
         }**/
        int[] visited = new int[n];
        int[] pathVisited = new int[n];
        for(int i = 0 ; i < n ; i ++){
            if(visited[i] == 0){
                dfs(i, graph, visited, pathVisited);
            }
        }
        List<Integer> res = new ArrayList<>();
        //not cycle path nodes are the answer
        for(int i = 0 ; i < pathVisited.length ; i ++){
            if(pathVisited[i] == 0){
                res.add(i);
            }
        }
        return res;
    }

    static boolean dfs(int i, int[][] graph, int[] visited, int[] pathVisited){
        visited[i] = 1;
        pathVisited[i] = 1;
        for(int j : graph[i]){
            if(visited[j] == 0 && dfs(j, graph, visited, pathVisited)){
                return true;
            }
            //if path already visited now visiting again then cycle exixts
            if(pathVisited[j] == 1){
                return true;
            }
        }
        //mark 0 for not cycle paths
        pathVisited[i] = 0;
        return false;
    }

}
