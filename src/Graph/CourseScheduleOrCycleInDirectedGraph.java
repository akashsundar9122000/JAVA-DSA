package Graph;

import java.util.ArrayList;
import java.util.List;

public class CourseScheduleOrCycleInDirectedGraph {
    //207
    //https://leetcode.com/problems/course-schedule/description/?envType=problem-list-v2&envId=depth-first-search
    /**
     * There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.
     *
     * For example, the pair [0, 1], indicates that to take course 0 you have to first take course 1.
     * Return true if you can finish all courses. Otherwise, return false.
     *
     *
     *
     * Example 1:
     *
     * Input: numCourses = 2, prerequisites = [[1,0]]
     * Output: true
     * Explanation: There are a total of 2 courses to take.
     * To take course 1 you should have finished course 0. So it is possible.
     * Example 2:
     *
     * Input: numCourses = 2, prerequisites = [[1,0],[0,1]]
     * Output: false
     * Explanation: There are a total of 2 courses to take.
     * To take course 1 you should have finished course 0, and to take course 0 you should also have finished course 1. So it is impossible.
     *
     *
     * Constraints:
     *
     * 1 <= numCourses <= 2000
     * 0 <= prerequisites.length <= 5000
     * prerequisites[i].length == 2
     * 0 <= ai, bi < numCourses
     * All the pairs prerequisites[i] are unique.
     */

    static void main() {
        int numOfCourses = 2;
        int[][] pre = {{1,0},{0,1}};
        System.out.println(canFinish(numOfCourses, pre));
    }

    //approach - check if cycle for directed graph exisits. If exists return false
    /**
     How to detect cycle in direct graph?
     Do DFS and keep 2 arr, visited and path visited
     When you reach a node mark it as visited and also path visited
     But when u come out from one recursive path make the path visited as false because it may be involved with other paths
     This is how you will find the cycle in directed graph
     */
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adjList = new ArrayList<>();
        for(int i = 0 ; i < numCourses ; i ++){
            adjList.add(new ArrayList<>());
        }
        for(int i = 0; i < prerequisites.length ; i ++){
            adjList.get(prerequisites[i][1]).add(prerequisites[i][0]);
        }
        int[] visited = new int[numCourses];
        int[] pathVisited = new int[numCourses];
        for(int i = 0 ; i < numCourses ; i ++){
            if(visited[i] == 0 && dfs(i, adjList, visited, pathVisited)){
                return false;
            }
        }
        return true;
    }

    static boolean dfs(int i, List<List<Integer>> adjList, int[] visited, int [] pathVisited){
        visited[i] = 1;
        pathVisited[i] = 1;
        for(int j : adjList.get(i)){
            if(visited[j] == 0){
                if(dfs(j, adjList, visited, pathVisited)){
                    return true;
                }
            }
            //if this path is already visited in this node's traversal then there exist an cycle
            if(pathVisited[j] == 1){
                return true;
            }
        }
        //changing pathvisited back because this node may have been in other paths
        pathVisited[i] = 0;
        return false;
    }
}
