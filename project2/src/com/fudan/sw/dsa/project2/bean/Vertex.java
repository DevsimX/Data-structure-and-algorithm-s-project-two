package com.fudan.sw.dsa.project2.bean;

import java.util.*;
public class Vertex implements Comparable<Vertex>{
    public final String name;
    public Double longitude;
    public Double latitude;
    public ArrayList<Integer> subway = new ArrayList<>();
    public ArrayList<Edge> neighbours;
    public LinkedList<Vertex> path;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public int compareTo(Vertex other){
        return Double.compare(minDistance,other.minDistance);
    }
    public Vertex(String name , double longitude , double latitude , int subway){
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.subway.add(subway);
        neighbours = new ArrayList<Edge>();
        path = new LinkedList<Vertex>();
    }
    public String toString(){
        return name;
    }
}
