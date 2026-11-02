# Delivery Route Optimizer – Browser UI

A Spring Boot web application for your TSP project. It compares:
- Brute Force (only up to 11 locations)
- Held–Karp Dynamic Programming
- Nearest Neighbour Greedy

## Requirements
- Java 17 or newer
- Maven (or IntelliJ IDEA with Maven support)

## Run in IntelliJ
1. Extract this ZIP.
2. Open the `DeliveryRouteOptimizerWeb` folder in IntelliJ IDEA.
3. Allow IntelliJ to load the Maven project and download dependencies.
4. Make sure Project SDK is Java 17 or newer.
5. Open `src/main/java/com/optimizer/DeliveryRouteOptimizerApplication.java`.
6. Click the green Run icon next to `main`.
7. Open http://localhost:8080 in your browser.

## Run from terminal
From this project folder:
```bash
mvn spring-boot:run
```
Then visit http://localhost:8080.

## Use
- The sample six-location matrix is loaded automatically.
- Edit location names and distances, or add/remove locations.
- Click **Optimize route** to run all algorithms.
- The dashboard shows each route, total distance, execution time, and comparison.
- The benchmark section tests increasing input sizes (Brute Force is skipped above 11).

## Notes
- This is a local web app; it is not publicly deployed on the internet.
- Distances are user-entered symmetric distances in kilometres.
- Held–Karp is limited to 15 locations to keep memory use reasonable.
