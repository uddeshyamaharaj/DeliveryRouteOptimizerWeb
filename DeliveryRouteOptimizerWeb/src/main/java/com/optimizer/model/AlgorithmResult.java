package com.optimizer.model;

import java.util.List;

public class AlgorithmResult {
    private String algorithm;
    private int distance;
    private double timeMs;
    private List<String> route;
    private boolean optimal;

    public AlgorithmResult(String algorithm, int distance, double timeMs, List<String> route, boolean optimal) {
        this.algorithm = algorithm;
        this.distance = distance;
        this.timeMs = timeMs;
        this.route = route;
        this.optimal = optimal;
    }
    public String getAlgorithm() { return algorithm; }
    public int getDistance() { return distance; }
    public double getTimeMs() { return timeMs; }
    public List<String> getRoute() { return route; }
    public boolean isOptimal() { return optimal; }
}
