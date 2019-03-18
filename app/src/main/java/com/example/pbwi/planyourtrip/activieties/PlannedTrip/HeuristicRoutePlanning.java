package com.example.pbwi.planyourtrip.activieties.PlannedTrip;

import android.util.Log;

import com.example.pbwi.planyourtrip.activieties.Attractions.GoogleAttractionEntry;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;

public class HeuristicRoutePlanning {

    private final int numberOfAttractions;
    private final int numberOfDays; //number of days of sightseeing
    private final int maxKilometersPerDay; //max kilometers per day
    private final int maxVisitPerDay;
    private float overallProfit = 0, length = 0;
    private final ArrayList<List<GoogleAttractionEntry>> route;
    private List<GoogleAttractionEntry> availableAttractions;
    private GoogleAttractionEntry startPlace;



    public HeuristicRoutePlanning(GoogleAttractionEntry startPlace, int maxKilometersPerDay, int numberOfDays, int maxVisitPerDay, List<GoogleAttractionEntry> listOfAttractions) {

        this.numberOfAttractions = listOfAttractions.size();
        this.numberOfDays = numberOfDays;
        this.maxKilometersPerDay = maxKilometersPerDay;
        this.maxVisitPerDay = maxVisitPerDay;

        this.startPlace = startPlace;
        this.route = new ArrayList<>(numberOfDays);
        this.availableAttractions = new LinkedList<>();


        for (int i = 0; i < numberOfDays; i++) {
            route.add(new LinkedList<GoogleAttractionEntry>());
            route.get(i).add(this.startPlace); //adding start place at beginning

        }


        availableAttractions.addAll(listOfAttractions);

    }


    public void greedySolution() {

        int actualDistance = 0;
        double rank = 0;

        GoogleAttractionEntry maxProfitableObject;
        GoogleAttractionEntry actualObject;
        actualObject = startPlace;
        for (int i = 0; i < numberOfDays; i++) {
            while (true) {
                maxProfitableObject = findMaxProfitableObject(actualObject);
                float distance = calculateDistance(actualObject, maxProfitableObject) + actualDistance + calculateDistance(maxProfitableObject, startPlace);
                if (distance <= maxKilometersPerDay && route.get(i).size() <= numberOfAttractions && route.get(i).size() <= maxVisitPerDay) {
                    actualDistance += calculateDistance(actualObject, maxProfitableObject);
                    rank += maxProfitableObject.getRate();
                    route.get(i).add(maxProfitableObject);
                    actualObject = maxProfitableObject;
                } else {
                    route.get(i).add(startPlace);
                    availableAttractions.add(maxProfitableObject);
                    overallProfit += rank;
                    length += actualDistance + calculateDistance(actualObject, startPlace);

                    break;
                }

            }
            actualDistance = 0;
            rank = 0;
            actualObject = startPlace;
        }

        Log.i("GreedSolution profit", String.valueOf(overallProfit));

    }

    private GoogleAttractionEntry findMaxProfitableObject(GoogleAttractionEntry actualObject) {
        GoogleAttractionEntry entry = new GoogleAttractionEntry();
        double maxProfitable = 0;
        double rank;
        float distance;

        for (int i = 0; i < availableAttractions.size(); i++) {
            rank = availableAttractions.get(i).getRate();
            distance = calculateDistance(availableAttractions.get(i), actualObject);

            if ((rank / distance) > maxProfitable) {
                maxProfitable = (rank / distance);
                entry = availableAttractions.get(i);
            }

        }
        availableAttractions.remove(entry);

        return entry;
    }

    float calculateDistanceOtherWay(GoogleAttractionEntry object1, GoogleAttractionEntry object2) {
        float distance;

        distance = (float) ((float) (sqrt(Math.pow((object1.getLat() - object2.getLat()), 2) + Math.pow((object1.getLng() - object2.getLng()), 2))) * 111.2);
        return distance;
    }

    private float calculateDistance(GoogleAttractionEntry object1, GoogleAttractionEntry object2) {
        float distance1;
//        * 111.2


        int earthRadius = 6371;
        double latitudeRad1 = Math.toRadians(object1.getLat());
        double latitudeRad2 = Math.toRadians(object2.getLng());

        double longitudeDelta = Math.toRadians(object2.getLng() - object1.getLng());
        double latitudeDelta = Math.toRadians(object2.getLat() - object1.getLat());

        double a = Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2) + Math.cos(latitudeRad2) * Math.cos(latitudeRad1) * Math.sin(longitudeDelta / 2) * Math.sin(longitudeDelta / 2);
        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance1 = (float) ((earthRadius * b));

        return distance1;
    }

    private boolean twoOPT(int dayNumber) {
        int numberOfVertices;
        boolean improvement = false;

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);

        numberOfVertices = route.get(dayNumber).size();
        for (int i = 0; i < numberOfVertices - 3; i++) {
            for (int j = i + 2; j < numberOfVertices - 1; j++) {


                float distance1 = (float) ((float) Math.round((calculateDistance(route.get(dayNumber).get(i), route.get(dayNumber).get(i + 1)) + calculateDistance(route.get(dayNumber).get(j), route.get(dayNumber).get(j + 1))) * 10.0) / 10.0);
                float distance2 = (float) ((float) Math.round((calculateDistance(route.get(dayNumber).get(i), route.get(dayNumber).get(j)) + calculateDistance(route.get(dayNumber).get(i + 1), route.get(dayNumber).get(j + 1))) * 10.0) / 10.0);


                if (distance1 > distance2) {
                    Collections.swap(route.get(dayNumber), i + 1, j);
                    return true;
                }

            }
        }

        return improvement;
    }


    private void localSearch2OPT() {

        for (int i = 0; i < numberOfDays; i++)
            if (route.get(i).size() > 0) {
//                Log.i("localSearch2OPT", i + "true");
                boolean improvement = true;
                while (improvement) {
                    improvement = twoOPT(i);

                }
            }

    }

    private boolean Insert(int m) {

        double bestH = Integer.MIN_VALUE, h;
        boolean improvement = false;


        for (int i = 0; i < availableAttractions.size() - 1; i++) {
            for (int j = 1; j < route.get(m).size() - 1; j++) {

                try {
                    if (availableAttractions.isEmpty()) return false;
                    route.get(m).add(j, availableAttractions.get(i));
                } catch (IndexOutOfBoundsException ex) {
                    Log.e("Insert", "IndexOutOfBoundsException " + availableAttractions.size()
                            + "     route.get(m).size(): " + route.get(m).size()
                            + "     i: " + i
                            + "     j: " + j
                    );
                }

                h = (availableAttractions.get(i).getRate() / (calculateDistance(availableAttractions.get(i), route.get(m).get(j - 1))));
                if (h > bestH && countPathLength(route.get(m)) < maxKilometersPerDay && route.get(m).size() <= maxVisitPerDay) {
                    bestH = h;
                    overallProfit += availableAttractions.get(i).getRate();
                    availableAttractions.remove(availableAttractions.get(i));
                    improvement = true;
                } else {
                    route.get(m).remove(j);
                }


            }

        }
        return improvement;
    }

    private void localSearchInsert() {
        for (int m = 0; m < numberOfDays; m++) {
            boolean improvement = true;
            while (improvement) {
                improvement = Insert(m);

            }
        }

    }

    private boolean Replace() {
        float bestH = Float.MIN_VALUE, h;
        boolean improvement = false, overallImprovement = false;
        GoogleAttractionEntry bestObject = null;


        for (int x = 0; x < numberOfDays; x++) {
            float pathProfit = countPathLength(route.get(x));
            float newProfit, oldProfit = countPathLength(route.get(x));

            LinkedList<GoogleAttractionEntry> current_list = new LinkedList(route.get(x));
            LinkedList<GoogleAttractionEntry> bestPath = new LinkedList<>();
            for (int i = 0; i < availableAttractions.size(); i++) {
                current_list = new LinkedList(route.get(x));
                GoogleAttractionEntry dost_obiekt = availableAttractions.get(i);

                for (int j = 1; j < current_list.size() - 1; j++) {
                    GoogleAttractionEntry o = current_list.get(j);

                    current_list.set(j, dost_obiekt);
                    newProfit = countPathProfit(current_list);
                    h = (newProfit - oldProfit) / (calculateDistance(current_list.get(j - 1), current_list.get(j)) + calculateDistance(current_list.get(j), current_list.get(j + 1)));
                    if (h > bestH && countPathLength(current_list) < maxKilometersPerDay && newProfit > pathProfit && newProfit > oldProfit) {
                        oldProfit = newProfit;
                        bestH = h;
                        bestPath = new LinkedList(current_list);
                        improvement = true;
                        bestObject = o;
                        current_list.set(j, o);
                        if (!overallImprovement)
                            overallImprovement = true;
                    } else {
                        current_list.set(j, o);
                    }
                }
                if (improvement) {
                    availableAttractions.remove(i);
                    availableAttractions.add(bestObject);
                    route.set(x, bestPath);
                    improvement = false;
                }
                bestH = Float.MIN_VALUE;
            }
        }

        return improvement;
    }


    private void localSearchReplace() {

//        for(int i = 0; i < numberOfDays; i++){
        boolean improvement = true;
        while (improvement) {
            improvement = Replace();
        }
//        }

    }


    private float countPathLength(List<GoogleAttractionEntry> route) {
        float length = 0;

        for (int i = 0; i < route.size(); i++) {

            length += calculateDistance(route.get(i), route.get(i + 1));
            if (route.get(i + 1) == route.get(route.size() - 1)) 
                break;
        }

        return length;
    }

    private boolean ReplaceBetweenPaths(int x) {
        double bestH, h;


        LinkedList<GoogleAttractionEntry> current_list = new LinkedList(route.get(x));
        for (int n = 0; n < numberOfDays; n++) {
            if (n == x) continue;
            LinkedList<GoogleAttractionEntry> current_route = new LinkedList(route.get(n));
            for (int i = 1; i < current_list.size() - 1; i++) {
                GoogleAttractionEntry objectPath1 = current_list.get(i);
                bestH = countPathLength(current_list) + countPathLength(current_route);
                for (int j = 1; j < current_route.size() - 1; j++) {
                    GoogleAttractionEntry objectPath2 = current_route.get(j);
                    current_list.set(i, objectPath2);
                    current_route.set(j, objectPath1);
                    h = countPathLength(current_list) + countPathLength(current_route);
                    if (h < bestH && countPathLength(current_list) < maxKilometersPerDay && countPathLength(current_route) < maxKilometersPerDay) {
                        route.set(x, current_list);
                        route.set(n, current_route);
                        return true;
                    } else {
                        current_list.set(i, objectPath1);
                        current_route.set(j, objectPath2);
                    }
                }
            }
        }
        return false;
    }


    private void localSearchReplaceBetweenPaths() {
        int x = 0;
        for (int i = 0; i < numberOfDays; i++) {
            boolean improvement = true;
            while (improvement) {
                improvement = ReplaceBetweenPaths(i);
                x++;
            }
        }
    }

    void localSearch() {
        float old_profit = overallProfit, new_profit, helpProfit = overallProfit;
        int i = 0;
        while (true) {
            localSearchReplaceBetweenPaths();
            Log.i("ReplaceBetweenPaths", " " + i);
            localSearchReplace();
            Log.i("Replace", " " + i);

            new_profit = countAllProfit();
            if (new_profit > old_profit) {
                overallProfit = new_profit;
                old_profit = new_profit;
            } else if (new_profit == helpProfit) {
                break;
            } else {
                localSearch2OPT();
                Log.i("2OPT", " " + i);
                localSearchInsert();
                Log.i("Insert", " " + i);
                helpProfit = countAllProfit();

            }
            Log.i("profits", "  old_profit: " + old_profit
            + "  new_profit: " + new_profit + "  helpProfit: " + helpProfit);
            i++;
        }

    }


    private float countAllProfit() {
        float profit = 0;
        for (int i = 0; i < numberOfDays; i++) {
            for (int j = 0; j < route.get(i).size(); j++) {
                profit += route.get(i).get(j).getRate();
            }
        }
        return profit;
    }

    private int countPathProfit(List<GoogleAttractionEntry> route) {
        int profit = 0;

        for (int i = 1; i < route.size() - 1; i++) {

            profit += route.get(i).getRate();

        }

        return profit;
    }


    public ArrayList<List<GoogleAttractionEntry>> getRoute() {
        return route;
    }
}