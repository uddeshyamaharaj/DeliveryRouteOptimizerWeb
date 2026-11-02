package com.optimizer.model;

import java.util.List;

public class RouteRequest {
    private List<String> names;
    private int[][] distances;

    public List<String> getNames() { return names; }
    public void setNames(List<String> names) { this.names = names; }
    public int[][] getDistances() { return distances; }
    public void setDistances(int[][] distances) { this.distances = distances; }
}
