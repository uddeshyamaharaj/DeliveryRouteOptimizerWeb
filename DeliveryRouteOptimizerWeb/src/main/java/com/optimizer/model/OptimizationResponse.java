package com.optimizer.model;

import java.util.List;

public class OptimizationResponse {
    private List<AlgorithmResult> results;
    private String bestAlgorithm;
    private int bestDistance;
    private double greedyGapPercent;

    public OptimizationResponse(List<AlgorithmResult> results, String bestAlgorithm, int bestDistance, double greedyGapPercent) {
        this.results = results;
        this.bestAlgorithm = bestAlgorithm;
        this.bestDistance = bestDistance;
        this.greedyGapPercent = greedyGapPercent;
    }
    public List<AlgorithmResult> getResults() { return results; }
    public String getBestAlgorithm() { return bestAlgorithm; }
    public int getBestDistance() { return bestDistance; }
    public double getGreedyGapPercent() { return greedyGapPercent; }
}
