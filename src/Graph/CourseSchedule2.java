package Graph;

import java.util.*;

public class CourseSchedule2 {
    //210
    //https://leetcode.com/problems/course-schedule-ii/description/
    /**
     * There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.
     *
     * For example, the pair [0, 1], indicates that to take course 0 you have to first take course 1.
     * Return the ordering of courses you should take to finish all courses. If there are many valid answers, return any of them. If it is impossible to finish all courses, return an empty array.
     *
     *
     *
     * Example 1:
     *
     * Input: numCourses = 2, prerequisites = [[1,0]]
     * Output: [0,1]
     * Explanation: There are a total of 2 courses to take. To take course 1 you should have finished course 0. So the correct course order is [0,1].
     * Example 2:
     *
     * Input: numCourses = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
     * Output: [0,2,1,3]
     * Explanation: There are a total of 4 courses to take. To take course 3 you should have finished both courses 1 and 2. Both courses 1 and 2 should be taken after you finished course 0.
     * So one correct course order is [0,1,2,3]. Another correct ordering is [0,2,1,3].
     * Example 3:
     *
     * Input: numCourses = 1, prerequisites = []
     * Output: [0]
     *
     *
     * Constraints:
     *
     * 1 <= numCourses <= 2000
     * 0 <= prerequisites.length <= numCourses * (numCourses - 1)
     * prerequisites[i].length == 2
     * 0 <= ai, bi < numCourses
     * ai != bi
     * All the pairs [ai, bi] are distinct.
     */

    static void main() {
        int numCourses = 4;
        int[][] prerequisites = {
                {1,0},
                {2,0},
                {3,1},
                {3,2}
        };
        System.out.println(Arrays.toString(findOrder(numCourses, prerequisites)));
    }

    static int[] findOrder(int numCourses, int[][] prerequisites) {
        return dfsMethod(numCourses, prerequisites);
        //return bfsMethod(numCourses, prerequisites);
    }

    static int[] dfsMethod(int numCourses, int[][] prerequisites){
        List<List<Integer>> adjList = new ArrayList<>();
        List<Integer> orderOfCourse = new ArrayList<>();
        for(int i = 0 ; i < numCourses ; i ++){
            adjList.add(new ArrayList<>());
        }
        //u cant take course[0] without taking course[1] so it 1 -> 0
        for(int pre[] : prerequisites){
            adjList.get(pre[1]).add(pre[0]);
        }
        int visited[] = new int[numCourses];
        int pathVisited[] = new int[numCourses];

        for(int i = 0 ; i < numCourses ; i ++){
            if(visited[i] == 0){
                //if cycle exists we cant take all courses so return empty arr
                if(dfs(i, adjList, visited, pathVisited, orderOfCourse)){
                    return new int[0];
                }
            }
        }
        int[] res = new int[adjList.size()];
        int index = 0;
        //order is in reverse so looping backwards
        for(int i = orderOfCourse.size() - 1 ; i >= 0 ; i --){
            res[index++] = orderOfCourse.get(i);;
        }
        return !orderOfCourse.isEmpty() ? res : new int[0];
    }

    static boolean dfs(int i, List<List<Integer>> adjList, int[] visited, int[] pathVisited, List<Integer> order){
        visited[i] = 1;
        pathVisited[i] = 1;
        for(int adjNodes : adjList.get(i)){
            if(visited[adjNodes] == 0){
                if(dfs(adjNodes, adjList, visited, pathVisited, order)){
                    return true;
                }
            }
            if(pathVisited[adjNodes] == 1){
                //cycleDetected
                return true;
            }
        }
        //if cycle not detected
        pathVisited[i] = 0;
        //adding to be taken courses to list in reverse order
        order.add(i);
        return false;
    }

    //bfs
//    static int[] bfsMethod(int numCourses, int[][] prerequisites){
//        List<List<Integer>> adjList = new ArrayList<>();
//        List<Integer> orderOfCourse = new ArrayList<>();
//        for(int i = 0 ; i < numCourses ; i ++){
//            adjList.add(new ArrayList<>());
//        }
//        //u cant take course[0] without taking course[1] so it 1 -> 0
//        for(int pre[] : prerequisites){
//            adjList.get(pre[1]).add(pre[0]);
//        }
//        int visited[] = new int[numCourses];
//        int pathVisited[] = new int[numCourses];
//
//        for(int i = 0 ; i < numCourses ; i ++){
//            if(visited[i] == 0){
//                //if cycle exists we cant take all courses so return empty arr
//                if(bfs(i, adjList, visited, pathVisited, orderOfCourse)){
//                    return new int[0];
//                }
//            }
//        }
//        int[] res = new int[adjList.size()];
//        int index = 0;
//        //order is in reverse so looping backwards
//        for(int i = orderOfCourse.size() - 1 ; i >= 0 ; i --){
//            res[index++] = orderOfCourse.get(i);;
//        }
//        return !orderOfCourse.isEmpty() ? res : new int[0];
//    }

//    static boolean bfs(int i, List<List<Integer>> adjList, int[] visited, int[] pathVisited, List<Integer> order){
//        visited[i] = 1;
//        pathVisited[i] = 1;
//        Queue<Integer> queue = new LinkedList<>();
//        for(int j: adjList.get(i)){
//            queue.offer(j);
//        }
//        while (!queue.isEmpty()){
//            int size = queue.size();
//            for (int k = 0 ; k < size ; k ++) {
//                int node = queue.poll();
//                for (int j : adjList.get(node)) {
//                    if (visited[j] == 0) {
//                        visited[j] = 1;
//                        pathVisited[j] = 1;
//                        queue.offer(j);
//                    } else if (pathVisited[j] == 1) {
//                        return true;
//                    } else {
//                        order.add(j);
//                        pathVisited[j] = 0;
//                    }
//                }
//            }
//        }
//        return false;
//    }
}
