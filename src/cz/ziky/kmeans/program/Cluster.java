package cz.ziky.kmeans.program;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private Vector centroid;
	private List<Integer> assignedData;
	private int count;
	private Vector newMean;
	private boolean needUpdate;
	
	public Cluster(Vector centroid) {
		this.centroid = centroid;
		this.assignedData = new ArrayList<Integer>();
		this.count = 0;
		this.newMean = new Vector(centroid.dimension());
		this.needUpdate = true;
	}

	public void add(int index, Vector vector) {
		/*if (count == assignedData.length) {
			int[] tmp = new int[assignedData.length + 10];
			System.arraycopy(assignedData, 0, tmp, 0, count);
			assignedData = tmp;
		}*/
		//assignedData[count++] = index;
		
		/*for (int i = 0; i < newMean.dimension(); i++) {
			newMean.getData()[i] += vector.getData()[i]; 
		}*/
		assignedData.add(index);
		count++;
	}
	
	public void remove(int index, Vector vector) {
		for (int i = 0; i < newMean.dimension(); i++) {
			newMean.getData()[i] -= vector.getData()[i]; 
		}
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
		//centroid.setData(newCentroidData);
		return new Vector(newCentroidData);
	}
	
	public Vector countNewMean() {
		Vector mean = new Vector(newMean.dimension());
		
		for (int i = 0; i < newMean.dimension(); i++) {
			mean.getData()[i] = newMean.getData()[i] / count;
		}
		
		return mean;
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
	
	public boolean needUpdate() {
		return needUpdate;
	}
	
	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}
}
