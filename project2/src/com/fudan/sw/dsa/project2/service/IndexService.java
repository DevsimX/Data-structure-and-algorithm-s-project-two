package com.fudan.sw.dsa.project2.service;

import java.util.*;

import com.fudan.sw.dsa.project2.bean.*;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.springframework.stereotype.Service;

import com.fudan.sw.dsa.project2.constant.FileGetter;

/**
 * this class is what you need to complete
 * @author zjiehang
 *
 */
@Service
public class IndexService 
{
	//the subway graph 浦东新区，祖冲之路，555号  杨浦区，四平路，2400号
	private Graph graph = null;
	private double velocity = 250/3.0;
	private double minTime = 0;
	private double distance = 0;
	private Vertex previousVertex;
	private String time;
	private int index;
	private Vertex theStartVertex;
	private Map<String, ArrayList<String>> map = new HashMap<>();
	private long startTime;
	private long endTime;
	private long startTime1;
	private long endTime1;
	
	/**
	 * create the graph use file
	 */
	public void createGraphFromFile()
	{
		formMatrix();

		//如果图未初始化
		if(graph==null)
		{
			graph=makeGraph(graph);
		}
	}

	private Graph makeGraph(Graph graph){
		double startLongitude = 0 , startLatitude = 0;
		String startTime1 = "" , startTime2 = "" , startName = "";
		double endLongitude = 0 , endLatitude = 0;
		String endTime1 = "" , endTime2 = "" , endName = "";
		FileGetter fileGetter= new FileGetter();
		try {
			Workbook wb = Workbook.getWorkbook(fileGetter.readFileFromClasspath()); // 从文件流中获取Excel工作区对象（WorkBook）
			Sheet[] sheets = wb.getSheets(); // 从工作区中取得页（Sheet）
			int sum = 0;
			for (Sheet x : sheets
			) {
				sum += x.getRows();
			}
			graph = new Graph(324);
			for (int s = 0; s < sheets.length; s ++) {
				int i = 1;
				if(s == 9 || s==10){
					for(int m = 0 ; m < 2 ; m++){
						i = 2;
						if(m == 1){
							i = index;
						}
						for (; i < sheets[s].getRows() - 1; i++) { // 循环打印Excel表中的内容
							for (int j = 0; j < sheets[s].getColumns(); j++) {
								Cell cell = sheets[s].getCell(j, i);
								switch (j){
									case 0: startName = cell.getContents();break;
									case 1: startLongitude = Double.parseDouble(cell.getContents());break;
									case 2: startLatitude = Double.parseDouble(cell.getContents());break;
									case 3: startTime1 = cell.getContents();break;
									case 4: startTime2 = cell.getContents();break;
								}
							}
							if(s == 10){
								String string = startTime1;
								startTime1 = startTime2;
								startTime2 = string;
							}
							Vertex startVertex = check(graph,startName,startLongitude,startLatitude,Integer.parseInt(sheets[s].getName().split(" ")[1]));
							i++;
							for (int j = 0; j < sheets[s].getColumns(); j++) {
								Cell cell = sheets[s].getCell(j, i);
								switch (j){
									case 0: endName = cell.getContents();break;
									case 1: endLongitude = Double.parseDouble(cell.getContents());break;
									case 2: endLatitude = Double.parseDouble(cell.getContents());break;
									case 3: endTime1 = cell.getContents();break;
									case 4: endTime2 = cell.getContents();break;
								}
							}
							if(s == 10){
								String string = endTime1;
								endTime1 = endTime2;
								endTime2 = string;
							}
							if(m == 1){
								startTime1 = startTime2;
								endTime1 = endTime2;
							}
							if(endTime1.equals("--")){
								if(!startTime1.equals("--")){
									previousVertex = startVertex;
									time = startTime1;
									index = i-1;
								}
								i--;
								continue;
							}
							if(startTime1.equals("--")){
								startVertex = previousVertex;
								Vertex endVertex = check(graph,endName,endLongitude,endLatitude,Integer.parseInt(sheets[s].getName().split(" ")[1]));
								graph.addEdge(startVertex,endVertex,getTime(endTime1,time),Integer.parseInt(sheets[s].getName().split(" ")[1]));
								graph.addEdge(endVertex,startVertex,getTime(endTime1,time),Integer.parseInt(sheets[s].getName().split(" ")[1]));
								i--;
								continue;
							}
							Vertex endVertex = check(graph,endName,endLongitude,endLatitude,Integer.parseInt(sheets[s].getName().split(" ")[1]));
							graph.addEdge(startVertex,endVertex,getTime(endTime1,startTime1),Integer.parseInt(sheets[s].getName().split(" ")[1]));
							graph.addEdge(endVertex,startVertex,getTime(endTime1,startTime1),Integer.parseInt(sheets[s].getName().split(" ")[1]));
							i--;

						}
					}
					continue;
				}//specially treat some situations , the line 10 and line 11.
				for (; i < sheets[s].getRows() - 1; i++) { // 循环打印Excel表中的内容
					for (int j = 0; j < sheets[s].getColumns(); j++) {
						Cell cell = sheets[s].getCell(j, i);
						switch (j){
							case 0: startName = cell.getContents();break;
							case 1: startLongitude = Double.parseDouble(cell.getContents());break;
							case 2: startLatitude = Double.parseDouble(cell.getContents());break;
							case 3: startTime1 = cell.getContents();break;
						}
					}
					Vertex startVertex = check(graph,startName,startLongitude,startLatitude,Integer.parseInt(sheets[s].getName().split(" ")[1]));
					i++;
					for (int j = 0; j < sheets[s].getColumns(); j++) {
						Cell cell = sheets[s].getCell(j, i);
						switch (j){
							case 0: endName = cell.getContents();break;
							case 1: endLongitude = Double.parseDouble(cell.getContents());break;
							case 2: endLatitude = Double.parseDouble(cell.getContents());break;
							case 3: endTime1 = cell.getContents();break;
						}
					}
					Vertex endVertex = check(graph,endName,endLongitude,endLatitude,Integer.parseInt(sheets[s].getName().split(" ")[1]));
					graph.addEdge(startVertex,endVertex,getTime(endTime1,startTime1),Integer.parseInt(sheets[s].getName().split(" ")[1]));
					graph.addEdge(endVertex,startVertex,getTime(endTime1,startTime1),Integer.parseInt(sheets[s].getName().split(" ")[1]));
					i--;

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}//current path:/Users/xiayutian/Library/tomcat/bin remember

		return graph;
	}
	
	
	public ReturnValue travelRoute(Map<String, Object>params)
	{
		String startAddress = 	params.get("startAddress").toString();	
		String startLongitude = params.get("startLongitude").toString();
		String startLatitude = params.get("startLatitude").toString();
		String endAddress = params.get("endAddress").toString();
		String endLongitude = params.get("endLongitude").toString();
		String endLatitude = params.get("endLatitude").toString();
		String choose = params.get("choose").toString();
		
		System.out.println(startAddress);
		System.out.println(startLongitude);
		System.out.println(startLatitude);
		System.out.println(endAddress);
		System.out.println(endLongitude);
		System.out.println(endLatitude);
		System.out.println(choose);
		
		Address startPoint = new Address(startAddress, startLongitude, startLatitude);
		Address endPoint = new Address(endAddress, endLongitude, endLatitude);
		List<Address> addresses=new ArrayList<Address>();


		minTime = 0;
		distance = 0;
		switch (choose)
		{
		case "1":
			//步行最少
			startTime1 = System.nanoTime();
			Dijkstra dijkstra = new Dijkstra();
			Graph graph1 = copyGraph(graph);
			ArrayList<Vertex> arrayList1 = graph1.getVertices();
			int start = getNearest(arrayList1,Double.parseDouble(startLongitude),Double.parseDouble(startLatitude));
			int end = getNearest(arrayList1,Double.parseDouble(endLongitude),Double.parseDouble(endLatitude));
			Vertex startVertex1 = arrayList1.get(start);
			Vertex endVertex1 = arrayList1.get(end);
			if(startVertex1 == endVertex1){
				endVertex1.path.add(startVertex1);
			}else{
				startTime = System.nanoTime();
				dijkstra.calculate(startVertex1);
				endTime = System.nanoTime();
			}
			for (Vertex vertex:endVertex1.path
				 ) {
				addresses.add(new Address(vertex.name , vertex.longitude+"" , vertex.latitude+""));
			}
			addresses.add(new Address(endVertex1.name,endVertex1.longitude+"",endVertex1.latitude+""));
			getMinTime(endVertex1);
			minTime += getDistance(Double.parseDouble(startLongitude),startVertex1.longitude,Double.parseDouble(startLatitude),startVertex1.latitude)/velocity+
					getDistance(Double.parseDouble(endLongitude),endVertex1.longitude,Double.parseDouble(endLatitude),endVertex1.latitude)/velocity;
			distance += getDistance(Double.parseDouble(startLongitude),startVertex1.longitude,Double.parseDouble(startLatitude),startVertex1.latitude)+
					getDistance(Double.parseDouble(endLongitude),endVertex1.longitude,Double.parseDouble(endLatitude),endVertex1.latitude);
			distance /= 1000;
			endTime1=System.nanoTime();
			System.out.println("Run time is " + (endTime-startTime) + "ns");
			System.out.println("The run time is " + (endTime1-startTime1) + "ns");
			break;
		case "2":
			//换乘最少
			startTime1 = System.nanoTime();
			ArrayList<Vertex> startVertices = getThreeNearest(graph.getVertices(),Double.parseDouble(startLongitude),Double.parseDouble(startLatitude));
			ArrayList<Vertex> endVertices = getThreeNearest(graph.getVertices(),Double.parseDouble(endLongitude),Double.parseDouble(endLatitude));
			ArrayList<Vertex> somesituation = new ArrayList<>();
			interChange bestInterchange = new interChange();
			bestInterchange.setMap(map);
			Vertex bestStartVertex = startVertices.get(0);
			Vertex bestEndVertex = endVertices.get(0);
			bestInterchange.getPath(startVertices.get(0).subway.get(0),endVertices.get(0).subway.get(0));
			startTime = System.nanoTime();
			for (Vertex vertex:startVertices
				 ) {
				if(endVertices.contains(vertex)){
					somesituation.add(vertex);
				}
			}
			if(somesituation.size() != 0){
				bestInterchange = new interChange();
				bestStartVertex = getNearest(somesituation,new Vertex("",Double.parseDouble(startLongitude),Double.parseDouble(startLatitude),0));
			}
			if(bestInterchange.getLinePath().size() != 1 && bestInterchange.getLinePath().size()!=0){
				for (Vertex startVertex : startVertices) {
					for (Vertex endVertex : endVertices) {
						ArrayList<Integer> startSubway = startVertex.subway;
						ArrayList<Integer> endSubway = endVertex.subway;
						for (Integer integer : startSubway) {
							for (Integer integer1 : endSubway) {
								interChange interChange = new interChange();
								interChange.setMap(map);
								interChange.getPath(integer, integer1);
								if (interChange.getLinePath().size() == 1) {
									bestInterchange = interChange;
									bestStartVertex = startVertex;
									bestEndVertex = endVertex;
									break;
								}
								if (interChange.getChange() < bestInterchange.getChange()) {
									bestInterchange = interChange;
									bestStartVertex = startVertex;
									bestEndVertex = endVertex;
								}
							}
						}
					}
				}
			}
			ArrayList<String> bestLinePath = bestInterchange.getLinePath();
			endTime = System.nanoTime();
			if(bestLinePath.size() != 0){
				ArrayList<Vertex> vertexArrayList = getAddress(bestLinePath,bestStartVertex,bestEndVertex);
				addresses.add(new Address(bestStartVertex.name,bestStartVertex.longitude+"",bestStartVertex.latitude+""));
				for(int i = vertexArrayList.size() - 1 ; i >= 0 ; i--){
					addresses.add(new Address(vertexArrayList.get(i).name,vertexArrayList.get(i).longitude+"",vertexArrayList.get(i).latitude+""));
				}
				getMinTime(vertexArrayList,bestStartVertex,bestEndVertex);
			}else
				addresses.add(new Address(bestStartVertex.name,bestStartVertex.longitude+"",bestStartVertex.latitude+""));
			minTime += getDistance(Double.parseDouble(startLongitude),bestStartVertex.longitude,Double.parseDouble(startLatitude),bestStartVertex.latitude)/velocity+
					getDistance(Double.parseDouble(endLongitude),bestEndVertex.longitude,Double.parseDouble(endLatitude),bestEndVertex.latitude)/velocity;
			distance += getDistance(Double.parseDouble(startLongitude),bestStartVertex.longitude,Double.parseDouble(startLatitude),bestStartVertex.latitude)+
					getDistance(Double.parseDouble(endLongitude),bestEndVertex.longitude,Double.parseDouble(endLatitude),bestEndVertex.latitude);
			distance /= 1000;
			endTime1 = System.nanoTime();
			theStartVertex = null;
			for (Vertex vertex:graph.getVertices()
				 ) {
				vertex.previous = null;
			}
			System.out.println("Run time is " + (endTime-startTime) + "ns");
			System.out.println("The run time is " + (endTime1-startTime1) + "ns");
			break;
		case "3":
			//时间最短:
			startTime1 = System.nanoTime();
			Dijkstra dijkstra1 = new Dijkstra();
			Graph graph2 = copyGraph(graph);
			Vertex startVertex = new Vertex(startAddress,Double.parseDouble(startLongitude),Double.parseDouble(startLatitude),0);
			Vertex endVertex = new Vertex(endAddress,Double.parseDouble(endLongitude),Double.parseDouble(endLatitude),0);
			ArrayList<Vertex> arrayList = graph2.getVertices();
			for (Vertex vertex:arrayList
				 ) {
				graph2.addEdge(startVertex,vertex,getDistance(Double.parseDouble(startLongitude),vertex.longitude,Double.parseDouble(startLatitude),vertex.latitude)/velocity,0);
				graph2.addEdge(vertex,endVertex,getDistance(Double.parseDouble(endLongitude),vertex.longitude,Double.parseDouble(endLatitude),vertex.latitude)/velocity,0);
			}
			arrayList.add(startVertex);
			arrayList.add(endVertex);
			startTime = System.nanoTime();
			dijkstra1.calculate(startVertex);
			endTime = System.nanoTime();
			for (Vertex pathVertex:endVertex.path
			) {
				if(pathVertex != startVertex && pathVertex != endVertex)
					addresses.add(new Address(pathVertex.name , pathVertex.longitude+"" , pathVertex.latitude+""));

			}
			getMinTime(endVertex);
			distance(endVertex);
			distance /= 1000;
			endTime1 = System.nanoTime();
			System.out.println("Run time is " + (endTime-startTime) + "ns");
			System.out.println("The run time is " + (endTime1-startTime1) + "ns");
			break;
		default:
			break;
		}
		
		ReturnValue returnValue = new ReturnValue();
		returnValue.setStartPoint(startPoint);
		returnValue.setEndPoint(endPoint);
		returnValue.setSubwayList(addresses);
		returnValue.setMinutes(minTime);
		returnValue.setWalkDistance(distance);
		return returnValue;
	}

	//check whether the vertex exists in the graph , if so , return it;else add it and return it
	private Vertex check(Graph graph , String name , double longitude , double latitude ,int subway){
		if(graph == null){
			return null;
		}
		for (Vertex vertex : graph.getVertices()
			 ) {
			if(vertex.name.equals(name)){
				if(!vertex.subway.contains(subway)){
					vertex.subway.add(subway);
				}
				return vertex;
			}
		}
		Vertex vertex = new Vertex(name , longitude ,latitude , subway);
		graph.getVertices().add(vertex);
		return  vertex;
	}

	//get the distance between two vertices by their longitudes and latitudes
	private double getDistance(double longitude1 , double longitude2 , double latitude1 , double latitude2){
		double a = (latitude1 * Math.PI / 180.00) - (latitude2 * Math.PI / 180.00);
		double b = (longitude1 * Math.PI / 180.00) - (longitude2 * Math.PI / 180.00);
		double s =  2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(b / 2), 2)));
		s *= 6378137.0;
		return s;
	}

	//get the time between two subways,no consider the situation if each subway has two times
	private double getTime(String endTime , String startTime){
		if(endTime.equals("--") || startTime.equals("--")){
			return 0;
		}
		String endHour_String = endTime.split(":")[0];
		String endMinute_String = endTime.split(":")[1];
		String startHour_String = startTime.split(":")[0];
		String startMinute_String = startTime.split(":")[1];
		if(endHour_String.charAt(0) == '次'){
			endHour_String = (Integer.parseInt(endHour_String.substring(1)) + 24) + "";
	}
		if(startHour_String.charAt(0) == '次'){
			startHour_String = (Integer.parseInt(startHour_String.substring(1)) + 24) + "";
		}
		int endHour = Integer.parseInt(endHour_String);
		int endMinute = Integer.parseInt(endMinute_String);
		int startHour = Integer.parseInt(startHour_String);
		int startMinute = Integer.parseInt(startMinute_String);
		return (endHour - startHour)*60 + (endMinute - startMinute);
	}

	//get the nearest subway to the target place
	private int getNearest(ArrayList<Vertex> arrayList , double longitude , double latitude){
		double minDistance = getDistance(longitude,arrayList.get(0).longitude,latitude,arrayList.get(0).latitude);
		int minNumber = 0;
		for(int i = 1 ; i < arrayList.size() ; i++){
			if(getDistance(longitude,arrayList.get(i).longitude,latitude,arrayList.get(i).latitude) < minDistance){
				minDistance = getDistance(longitude,arrayList.get(i).longitude,latitude,arrayList.get(i).latitude);
				minNumber = i;
			}
		}
		return minNumber;
	}

	private ArrayList<Vertex> getThreeNearest(ArrayList<Vertex> arrayList , double longitude , double latitude){
		ArrayList<Vertex> arrayList1 = new ArrayList<>();
		while (arrayList1.size() < 3){
			double minDistance = getDistance(longitude,arrayList.get(0).longitude,latitude,arrayList.get(0).latitude);
			Vertex minVertex = arrayList.get(0);
			for (Vertex vertex:arrayList
			) {
				if(arrayList1.contains(vertex))
					continue;
				if(getDistance(vertex.longitude,longitude,vertex.latitude,latitude) < minDistance){
					minDistance = getDistance(vertex.longitude,longitude,vertex.latitude,latitude);
					minVertex = vertex;
				}
			}
			arrayList1.add(minVertex);
		}
		return arrayList1;
	}
	//get minTime
	private void getMinTime(Vertex endVertex){
		for(int i = 0 ; i < endVertex.path.size()-1 ; i++){
			for(int j = 0 ; j < endVertex.path.get(i).neighbours.size() ; j++){
				if(endVertex.path.get(i).neighbours.get(j).target == endVertex.path.get(i+1)){
					minTime += endVertex.path.get(i).neighbours.get(j).weight;
				}
			}
		}
		for(int i = 0 ; i < endVertex.path.get(endVertex.path.size()-1).neighbours.size() ; i++){
			if(endVertex.path.get(endVertex.path.size()-1).neighbours.get(i).target == endVertex)
				minTime += endVertex.path.get(endVertex.path.size()-1).neighbours.get(i).weight;
		}
	}
	private void getMinTime(ArrayList<Vertex> vertices,Vertex startVertex,Vertex endVertex){
		for(int i = vertices.size() - 1 ; i >0 ; i--){
			Vertex first = vertices.get(i);
			Vertex second = vertices.get(i-1);
			for (Edge edge:first.neighbours
				 ) {
				if(edge.target == second)
					minTime += edge.weight;
			}
		}
		for (Edge edge:vertices.get(vertices.size()-1).neighbours
			 ) {
			if(edge.target == startVertex)
				minTime += edge.weight;
		}
		for (Edge edge:vertices.get(0).neighbours
			 ) {
			if(edge.target == endVertex)
				minTime += edge.weight;
		}
	}

	//get the distance
	private void distance(Vertex endVertex){
		distance += getDistance(endVertex.path.get(0).longitude,endVertex.path.get(1).longitude,endVertex.path.get(0).latitude,endVertex.path.get(1).latitude);
		distance += getDistance(endVertex.path.get(endVertex.path.size()-1).longitude,endVertex.longitude,endVertex.path.get(endVertex.path.size()-1).latitude,endVertex.latitude);
	}

	private ArrayList<Vertex> getAddress(ArrayList<String> arrayList,Vertex startVertex,Vertex endVertex){
		ArrayList<Vertex> arrayList1 = new ArrayList<>();
		int index = 0;
		if(arrayList.size() == 1){
			int line = Integer.parseInt(arrayList.get(index).split(" ")[1]);
			oneLine(startVertex,endVertex,line);
			Vertex currentVertex = endVertex;
			while(currentVertex != startVertex){
				arrayList1.add(currentVertex);
				currentVertex = currentVertex.previous;
			}
		}else {
			theStartVertex = startVertex;
			while(index + 2 <= arrayList.size()){
				int currentLine = Integer.parseInt(arrayList.get(index).split(" ")[1]);
				int nextLine = Integer.parseInt(arrayList.get(index+1).split(" ")[1]);
				manyLines(theStartVertex,currentLine,nextLine,endVertex);
				index++;
			}
			oneLine(theStartVertex,endVertex,Integer.parseInt(arrayList.get(arrayList.size()-1).split(" ")[1]));
			Vertex currentVertex = endVertex;
			while(currentVertex != startVertex){
				arrayList1.add(currentVertex);
				currentVertex = currentVertex.previous;
			}
		}
		return arrayList1;
	}

	private void oneLine(Vertex currentVertex,Vertex endVertex,int line){
		if(currentVertex == endVertex){
			return;
		}
		for (Edge edge:currentVertex.neighbours
		) {
			if(edge.path.contains(line) && edge.target != currentVertex.previous){
				edge.target.previous = currentVertex;
				oneLine(edge.target,endVertex,line);
			}
			if(endVertex.previous != null)
				return;
		}
	}

	private void manyLines(Vertex currentVertex,int firstLine,int secondLine,Vertex endVertex){
		if(currentVertex == endVertex){
			return;
		}
		for (Edge edge:currentVertex.neighbours
		) {
			if(edge.path.contains(firstLine) && !edge.target.subway.contains(secondLine) && edge.target != currentVertex.previous){
				edge.target.previous = currentVertex;
				manyLines(edge.target,firstLine,secondLine,endVertex);
			}else if(edge.path.contains(firstLine) && edge.target.subway.contains(secondLine) && edge.target != currentVertex.previous){
				edge.target.previous = currentVertex;
				theStartVertex = edge.target;
				return;
			}
		}
	}

	private void formMatrix(){
		FileGetter file = new FileGetter();
		try {
			Workbook wb = Workbook.getWorkbook(file.readFileFromClasspath()); // 从文件流中获取Excel工作区对象（WorkBook）
			Sheet[] sheets = wb.getSheets(); // 从工作区中取得页（Sheet）
			for (int s = 0; s < sheets.length; s++) {
				map.put(sheets[s].getName(),new ArrayList<>());
				a: for(int s1 = 0 ; s1 < sheets.length; s1++){
					if(s==s1)
						continue;
					for(int i = 1 ; i < sheets[s].getColumn(0).length ; i++){
						if(sheets[s1].findCell(sheets[s].getColumn(0)[i].getContents()) != null){
							map.get(sheets[s].getName()).add(sheets[s1].getName());
							continue a;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Graph copyGraph(Graph graph){
		Graph graph1 = new Graph(graph.getVertices().size());
		for (Vertex vertex:graph.getVertices()
			 ) {
			graph1.getVertices().add(new Vertex(vertex.name,vertex.longitude,vertex.latitude,0));
		}
		for(int i = 0 ; i < graph.getVertices().size() ; i++){
			for (Edge edge:graph.getVertex(i).neighbours
				 ) {
				graph1.addEdge(graph1.getVertex(i),search(graph1,edge.target.name),edge.weight,0);
			}
		}
		return graph1;
	}

	private Vertex search(Graph graph,String name){
		for (Vertex vertex:graph.getVertices()
			 ) {
			if(vertex.name.equals(name))
				return vertex;
		}
		return null;
	}

	private Vertex getNearest(ArrayList<Vertex> vertices,Vertex startVertex){
		Vertex min = vertices.get(0);
		for (Vertex vertex:vertices
			 ) {
			if(getDistance(vertex.longitude,startVertex.longitude,vertex.latitude,startVertex.latitude) < getDistance(min.longitude,startVertex.longitude,min.latitude,startVertex.latitude)){
				min = vertex;
			}
		}
		return min;
	}
}
