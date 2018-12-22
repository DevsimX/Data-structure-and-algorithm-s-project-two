package com.fudan.sw.dsa.project2.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class interChange {
    private Map<String, ArrayList<String>> map = new HashMap<>();
    private ArrayList<String> linePath = new ArrayList<>();
    private Graph graph;
    //line's matrix


    public void setMap(Map<String, ArrayList<String>> map) {
        this.map = map;
        graph = new Graph(map.size());
        for(int i = 1 ; i < 14 ; i++){
            Vertex vertex = new Vertex("Line " + i,0,0,0);
            graph.getVertices().add(vertex);
        }
        graph.getVertices().add(new Vertex("Line " + 16,0,0,0));
        graph.getVertices().add(new Vertex("Line " + 17,0,0,0));
        for(int i = 1 ; i < 14 ; i++){
            ArrayList<String> arrayList = map.get("Line " + i);
            for(int j = 0 ; j < arrayList.size() ; j++){
                graph.addEdge(getVertex("Line " + i,graph),getVertex(arrayList.get(j),graph),1,0);
            }
        }
        for(int i = 16 ; i < 18 ; i++){
            ArrayList<String> arrayList = map.get("Line " + i);
            for(int j = 0 ; j < arrayList.size() ; j++){
                graph.addEdge(getVertex("Line " + i,graph),getVertex(arrayList.get(j),graph),1,0);
            }
        }
    }

    private Vertex getVertex(String line,Graph graph){
        for (Vertex vertex:graph.getVertices()
             ) {
            if(vertex.name.equals(line))
                return vertex;
        }
        return null;
    }

    public void getPath(int subway1 , int subway2){
        Dijkstra dijkstra = new Dijkstra();
        dijkstra.calculate(getVertex("Line " + subway1,graph));
        Vertex currentVertex = getVertex("Line "+subway2,graph);
        for (Vertex vertex:currentVertex.path
             ) {
            linePath.add(vertex.name);
        }
        linePath.add(currentVertex.name);
    }

    public int getChange(){
        return linePath.size()-1;
    }

    public ArrayList<String> getLinePath(){
        return linePath;
    }
}
