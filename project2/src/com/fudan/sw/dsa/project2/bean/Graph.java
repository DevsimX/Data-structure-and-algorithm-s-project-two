package com.fudan.sw.dsa.project2.bean;

/**
 * for subway graph
 * @author 夏禹天
 *
 */
import java.util.*;
public class Graph {
    private ArrayList<Vertex> vertices;
    public Graph(int numberVertices){
        vertices = new ArrayList<Vertex>(numberVertices);
    }

    public void addEdge(Vertex startVertex , Vertex endVertex , double weight,int path){
        for (Edge edge:startVertex.neighbours
             ) {
            if(edge.target == endVertex){
                edge.path.add(path);
            }
        }
        Edge new_edge = new Edge(endVertex,weight,path);
        startVertex.neighbours.add(new_edge);
    }

    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertex(int vert){
        return vertices.get(vert);
    }
}
