package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;

//To represent the edges in the graph.
public class Edge{
    public final Vertex target;
    public double weight;
    public ArrayList<Integer> path = new ArrayList<>();
    public Edge(Vertex target, double weight,int path){
        this.target = target;
        this.weight = weight;
        this.path.add(path);
    }
}
