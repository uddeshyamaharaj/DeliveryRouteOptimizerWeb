package com.optimizer.service;

import com.optimizer.model.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RouteService {
    private static final int INF = Integer.MAX_VALUE / 4;

    public OptimizationResponse optimize(RouteRequest request) {
        validate(request);
        List<String> names = request.getNames();
        int[][] d = request.getDistances();
        int n = names.size();

        List<AlgorithmResult> results = new ArrayList<>();
        AlgorithmResult greedy = nearestNeighbour(d, names);
        AlgorithmResult hk = heldKarp(d, names);
        results.add(hk);
        results.add(greedy);
        if (n <= 11) results.add(bruteForce(d, names));

        AlgorithmResult best = results.get(0);

for (AlgorithmResult result : results) {
    if (result.getDistance() < best.getDistance()) {
        best = result;
    }
}
        double gap = hk.getDistance() == 0 ? 0 :
                Math.max(0, (greedy.getDistance() - hk.getDistance()) * 100.0 / hk.getDistance());
        return new OptimizationResponse(results, best.getAlgorithm(), best.getDistance(), gap);
    }

    private void validate(RouteRequest r) {
        if (r == null || r.getNames() == null || r.getDistances() == null)
            throw new IllegalArgumentException("Please provide location names and a distance matrix.");
        int n = r.getNames().size();
        if (n < 2 || n > 15) throw new IllegalArgumentException("Enter between 2 and 15 locations.");
        if (r.getDistances().length != n) throw new IllegalArgumentException("Distance matrix size must match locations.");
        for (int i = 0; i < n; i++) {
            if (r.getNames().get(i) == null || r.getNames().get(i).isBlank())
                throw new IllegalArgumentException("Location names cannot be empty.");
            if (r.getDistances()[i] == null || r.getDistances()[i].length != n)
                throw new IllegalArgumentException("Distance matrix must be square.");
            if (r.getDistances()[i][i] != 0) throw new IllegalArgumentException("Diagonal distances must be zero.");
            for (int j = 0; j < n; j++) {
                if (r.getDistances()[i][j] < 0) throw new IllegalArgumentException("Distances cannot be negative.");
                if (r.getDistances()[i][j] != r.getDistances()[j][i])
                    throw new IllegalArgumentException("Distances must be symmetric.");
            }
        }
    }

    private AlgorithmResult nearestNeighbour(int[][] d, List<String> names) {
        long start = System.nanoTime();
        int n = d.length, current = 0, total = 0;
        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();
        visited[0] = true; path.add(0);
        for (int step = 1; step < n; step++) {
            int next = -1, best = INF;
            for (int j = 0; j < n; j++) if (!visited[j] && d[current][j] < best) {
                best = d[current][j]; next = j;
            }
            visited[next] = true; path.add(next); total += best; current = next;
        }
        total += d[current][0]; path.add(0);
        return result("Nearest Neighbour", total, start, path, names, false);
    }

    private AlgorithmResult heldKarp(int[][] d, List<String> names) {
        long start = System.nanoTime();
        int n = d.length, size = 1 << n;
        int[][] dp = new int[size][n], parent = new int[size][n];
        for (int[] row : dp) Arrays.fill(row, INF);
        for (int[] row : parent) Arrays.fill(row, -1);
        dp[1][0] = 0;
        for (int mask = 1; mask < size; mask++) {
            if ((mask & 1) == 0) continue;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0 || dp[mask][i] == INF) continue;
                for (int j = 1; j < n; j++) if ((mask & (1 << j)) == 0) {
                    int nm = mask | (1 << j), candidate = dp[mask][i] + d[i][j];
                    if (candidate < dp[nm][j]) { dp[nm][j] = candidate; parent[nm][j] = i; }
                }
            }
        }
        int full = size - 1, best = INF, last = -1;
        for (int i = 1; i < n; i++) if (dp[full][i] + d[i][0] < best) {
            best = dp[full][i] + d[i][0]; last = i;
        }
        LinkedList<Integer> path = new LinkedList<>();
        path.addFirst(0);
        int mask = full, cur = last;
        while (cur != -1 && cur != 0) {
            path.addFirst(cur);
            int prev = parent[mask][cur];
            mask ^= (1 << cur); cur = prev;
        }
        path.addFirst(0);
        return result("Held–Karp (Dynamic Programming)", best, start, path, names, true);
    }

    private AlgorithmResult bruteForce(int[][] d, List<String> names) {
        long start = System.nanoTime();
        int n = d.length;
        boolean[] visited = new boolean[n];
        int[] path = new int[n], bestPath = new int[n];
        path[0] = 0; visited[0] = true;
        int[] best = {INF};
        permute(d, path, visited, 1, 0, best, bestPath);
        List<Integer> route = new ArrayList<>();
        for (int city : bestPath) route.add(city);
        route.add(0);
        return result("Brute Force", best[0], start, route, names, true);
    }

    private void permute(int[][] d, int[] path, boolean[] visited, int pos, int cost,
                         int[] best, int[] bestPath) {
        int n = d.length;
        if (pos == n) {
            int total = cost + d[path[n - 1]][0];
            if (total < best[0]) { best[0] = total; System.arraycopy(path, 0, bestPath, 0, n); }
            return;
        }
        for (int city = 1; city < n; city++) if (!visited[city]) {
            visited[city] = true; path[pos] = city;
            permute(d, path, visited, pos + 1, cost + d[path[pos - 1]][city], best, bestPath);
            visited[city] = false;
        }
    }

    private AlgorithmResult result(String label, int cost, long start, List<Integer> indexes,
                                   List<String> names, boolean optimal) {
        List<String> route = new ArrayList<>();

for (int index : indexes) {
    route.add(names.get(index));
}
        return new AlgorithmResult(label, cost, (System.nanoTime() - start) / 1_000_000.0, route, optimal);
    }
}
