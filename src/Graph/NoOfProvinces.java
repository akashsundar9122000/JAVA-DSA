package Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class NoOfProvinces {
    //547
    //https://leetcode.com/problems/number-of-provinces/description/
    /**
     * There are n cities. Some of them are connected, while some are not. If city a is connected directly with city b, and city b is connected directly with city c, then city a is connected indirectly with city c.
     *
     * A province is a group of directly or indirectly connected cities and no other cities outside of the group.
     *
     * You are given an n x n matrix isConnected where isConnected[i][j] = 1 if the ith city and the jth city are directly connected, and isConnected[i][j] = 0 otherwise.
     *
     * Return the total number of provinces.
     *
     *
     *
     * Example 1:
     *
     *
     * Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
     * Output: 2
     * Example 2:
     *
     *
     * Input: isConnected = [[1,0,0],[0,1,0],[0,0,1]]
     * Output: 3
     *
     *
     * Constraints:
     *
     * 1 <= n <= 200
     * n == isConnected.length
     * n == isConnected[i].length
     * isConnected[i][j] is 1 or 0.
     * isConnected[i][i] == 1
     * isConnected[i][j] == isConnected[j][i]
     */

    static void main() {
        int[][] isConnected = {
                {1,1,0},
                {1,1,0},
                {0,0,1}};
        System.out.println(findCircleNum(isConnected));
    }
    static int findCircleNum(int[][] isConnected) {
        //return dfsTraversal(isConnected);
        return bfsTraversal(isConnected);
    }

    static int dfsTraversal(int[][] isConnected){
        int n = isConnected.length;
        //create adjecency list from matrix.
        List<List<Integer>> adjList = new ArrayList<>();
        for(int i = 0 ; i < n ; i ++){
            adjList.add(new ArrayList<>());
        }
        //now fill matrix value to adjList. adjList - neibhour nodes for the current node relationship
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(isConnected[i][j] == 1 && i != j){
                    adjList.get(i).add(j); //i and j are neighbours
                    adjList.get(j).add(i); //j and i are neighbours
                }
            }
        }
        //created visited arr
        int[] visited = new int[n + 1]; //1 indexed so size + 1
        int count = 0;
        for(int i = 0 ; i < n ; i ++){
            if(visited[i] == 0){
                //it will only come in if not alrady visited so new province each time
                count ++;
                DFS(i, adjList, visited);
            }
        }
        return count;
    }

    static void DFS(int currentNode, List<List<Integer>> adjList, int[] visited){
        visited[currentNode] = 1; //mark curr node as visited
        for(Integer i : adjList.get(currentNode)){ //get neighbours for traversing
            if(visited[i] == 0){//if neighbours not visited traverse them in recursion and mark them visited
                DFS(i, adjList, visited);
            }
        }
    }

    static int bfsTraversal(int[][] isConnected){
        int n = isConnected.length;
        //create adjecency list from matrix.
        List<List<Integer>> adjList = new ArrayList<>();
        for(int i = 0 ; i < n ; i ++){
            adjList.add(new ArrayList<>());
        }
        //now fill matrix value to adjList. adjList - neibhour nodes for the current node relationship
        for(int i = 0 ; i < n ; i ++){
            for(int j = 0 ; j < n ; j ++){
                if(isConnected[i][j] == 1 && i != j){
                    adjList.get(i).add(j); //i and j are neighbours
                    adjList.get(j).add(i); //j and i are neighbours
                }
            }
        }
        int[] visited = new int[n + 1];
        int count = 0;
        for(int i = 0 ; i < n ; i ++){
            if(visited[i] == 0){
                count ++;
                BFS(i, adjList, visited);
            }
        }
        return count;
    }

    static void BFS(int curr, List<List<Integer>> adjList, int[] visited){
        Queue<Integer> queue = new LinkedList<>();
        //put nodes to queue
        queue.offer(curr);
        visited[curr] = 1;
        while(!queue.isEmpty()){
            int x = queue.poll();
            //get neighbouring nodes
            for(Integer i : adjList.get(x)){
                if(visited[i] == 0){
                    //mark visited for neighbour if not already visited
                    queue.add(i);
                    visited[i] = 1;
                }
            }
        }
    }
}
