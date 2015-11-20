package cz.ziky.kmeans.program;

public class Vector {
	private double[] data;
	private int dimension;
	
	public Vector(int size) {
		this.data = new double[size];
		this.dimension = size;
	}
	
	public Vector(double[] data) {
		this.data = data;
		this.dimension = data.length;
	}
	
	public int dimension() {
		return dimension;
	}
	
	//count euclidean distance
	public double countDistance(Vector vector) {
		double sum = 0;
			
		for (int i = 0; i < data.length; i++) {
			double value = data[i] - vector.data[i];
			sum += (value * value);
		}
			
		return Math.sqrt(sum);
	}
	
	public void setData(double[] data) {
		this.data = data;
	}
	
	public double[] getData() {
		return this.data;
	}
}