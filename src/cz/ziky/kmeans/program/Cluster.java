package cz.ziky.kmeans.program;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private Vector centroid;
	private List<Integer> assignedData;
	private int count;
	
	public Cluster(Vector centroid) {
		this.centroid = centroid;
		this.assignedData = new ArrayList<Integer>();
		this.count = 0;
	}

	public void add(int index) {
		assignedData.add(index);
		count++;
	}
	
	public void remove(int index) {
		assignedData.remove((Integer) index);
		count--;
	}
	
	public double countClusterSSE(Vector[] data) {
		double value = 0;
		for (int i = 0; i < count; i++) {
			Vector vector = data[assignedData.get(i)];
			value += Math.pow(vector.countDistance(centroid), 2);
		}
		return value;
	}
	
	//computes mean from assigned vectors
	public Vector countCentroid(Vector[] data) {
		double[] newCentroidData = new double[data[0].dimension()];
		for (int i = 0; i < count; i++) {
			double[] vectorData = data[assignedData.get(i)].getData();
				
			for (int j = 0; j < vectorData.length; j++) {
				newCentroidData[j] += vectorData[j];
			}
		}
		for (int i = 0; i < newCentroidData.length; i++) {
			newCentroidData[i] = newCentroidData[i] / count;
		}
		
		return new Vector(newCentroidData);
	}
	
	public void clearAssigments() {
		this.assignedData = new ArrayList<Integer>();
		this.count = 0;
	}
	
	public Vector getCentroid() {
		return centroid;
	}

	public void setCentroid(Vector centroid) {
		this.centroid = centroid;
	}
	
	public int getCount() {
		return count;
	}
	
	public List<Integer> getAssignedData() {
		return assignedData;
	}
}
