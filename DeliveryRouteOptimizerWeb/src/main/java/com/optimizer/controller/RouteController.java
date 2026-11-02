package com.optimizer.controller;

import com.optimizer.model.*;
import com.optimizer.service.RouteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RouteController {
    private final RouteService routeService;
    public RouteController(RouteService routeService) { this.routeService = routeService; }

    @PostMapping("/optimize")
    public OptimizationResponse optimize(@RequestBody RouteRequest request) {
        return routeService.optimize(request);
    }
}
