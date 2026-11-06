import java.util.*;

public class Main {

    static class Edge {
        char target;
        int weight;

        public Edge(char target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {

        char[] vertices = {'A', 'B', 'C', 'D', 'E'};
        int n = vertices.length;

        Map<Character, List<Edge>> adjList = new LinkedHashMap<>();
        for (char v : vertices) {
            adjList.put(v, new ArrayList<>());
        }

        addEdge(adjList, 'A', 'B', 2);
        addEdge(adjList, 'A', 'C', 5);
        addEdge(adjList, 'B', 'C', 1);
        addEdge(adjList, 'B', 'D', 4);
        addEdge(adjList, 'C', 'E', 3);
        addEdge(adjList, 'D', 'E', 2);

        System.out.println("СПИСОК СУМІЖНОСТІ");
        for (Map.Entry<Character, List<Edge>> entry : adjList.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            List<String> neighbors = new ArrayList<>();
            for (Edge e : entry.getValue()) {
                neighbors.add(e.target + " (вага " + e.weight + ")");
            }
            System.out.println(String.join(", ", neighbors));
        }

        System.out.println("\nМАТРИЦЯ СУМІЖНОСТІ");
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (Edge e : adjList.get(vertices[i])) {
                int j = e.target - 'A';
                matrix[i][j] = e.weight;
            }
        }

        System.out.print("    ");
        for (char v : vertices) System.out.print(v + " ");
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.print(vertices[i] + " | ");
            for (int j = 0; j < n; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nОБХІД BFS (Старт: A)");
        runBFS(adjList, 'A');

        System.out.println("\nОБХІД DFS (Старт: A)");
        runDFS(adjList, 'A');


        System.out.println("\n АЛГОРИТМ ДЕЙКСТРИ (A -> E) ");
        runDijkstra(adjList, vertices, 'A', 'E');
    }

    private static void addEdge(Map<Character, List<Edge>> map, char u, char v, int w) {
        map.get(u).add(new Edge(v, w));
        map.get(v).add(new Edge(u, w));
    }

    private static void runBFS(Map<Character, List<Edge>> adjList, char start) {
        Set<Character> visited = new LinkedHashSet<>();
        Queue<Character> queue = new LinkedList<>();
        Map<Character, Integer> distances = new HashMap<>();

        queue.add(start);
        visited.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            char current = queue.poll();
            System.out.println("Відвідуємо: " + current + " | Черга: " + queue);

            List<Edge> neighbors = new ArrayList<>(adjList.get(current));
            neighbors.sort(Comparator.comparingInt(e -> e.target));

            for (Edge e : neighbors) {
                if (!visited.contains(e.target)) {
                    visited.add(e.target);
                    distances.put(e.target, distances.get(current) + 1);
                    queue.add(e.target);
                }
            }
        }
        System.out.println("Порядок обходу BFS: " + visited);
        System.out.println("Відстані (кількість ребер): " + distances);
    }

    private static void runDFS(Map<Character, List<Edge>> adjList, char start) {
        Set<Character> visited = new LinkedHashSet<>();
        dfsRecursive(adjList, start, visited);
        System.out.println("Порядок обходу DFS: " + visited);
    }

    private static void dfsRecursive(Map<Character, List<Edge>> adjList, char current, Set<Character> visited) {
        visited.add(current);
        List<Edge> neighbors = new ArrayList<>(adjList.get(current));
        neighbors.sort(Comparator.comparingInt(e -> e.target));

        for (Edge e : neighbors) {
            if (!visited.contains(e.target)) {
                dfsRecursive(adjList, e.target, visited);
            }
        }
    }

    private static void runDijkstra(Map<Character, List<Edge>> adjList, char[] vertices, char start, char target) {
        Map<Character, Integer> distances = new HashMap<>();
        Map<Character, Character> previous = new HashMap<>();
        Set<Character> unvisited = new HashSet<>();

        for (char v : vertices) {
            distances.put(v, Integer.MAX_VALUE);
            unvisited.add(v);
        }
        distances.put(start, 0);

        while (!unvisited.isEmpty()) {
            char current = ' ';
            int minDist = Integer.MAX_VALUE;
            for (char v : unvisited) {
                if (distances.get(v) < minDist) {
                    minDist = distances.get(v);
                    current = v;
                }
            }

            if (current == ' ' || minDist == Integer.MAX_VALUE) break;
            if (current == target) break;

            unvisited.remove(current);
            System.out.println("Обрано вершину: " + current + " з поточною відстанню " + minDist);

            for (Edge e : adjList.get(current)) {
                if (unvisited.contains(e.target)) {
                    int newDist = distances.get(current) + e.weight;
                    if (newDist < distances.get(e.target)) {
                        distances.put(e.target, newDist);
                        previous.put(e.target, current);
                        System.out.println("  Оновлено відстань до " + e.target + " = " + newDist);
                    }
                }
            }
        }

        List<Character> path = new ArrayList<>();
        char curr = target;
        while (curr != '\0' && (previous.containsKey(curr) || curr == start)) {
            path.add(0, curr);
            if (curr == start) break;
            curr = previous.getOrDefault(curr, '\0');
        }

        System.out.println("Найкоротший шлях до " + target + ": " + String.join(" -> ", path.stream().map(String::valueOf).toArray(String[]::new)));
        System.out.println("Загальна вага шляху: " + distances.get(target));
    }
}