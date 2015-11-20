package cz.ziky.kmeans.initializer;

import java.util.Arrays;
import java.util.Random;

import cz.ziky.kmeans.program.Vector;

public class KmeansPlusPlus implements KMeansInitializer {
	private Vector[] centroids; //selected centroids
	private double[] di;		//minimum distance for each vector (from all centroid distances)
	
	@Override
	public Vector[] initCentroids(Vector[] data, int k) {
		centroids = new Vector[k];
		di = new double[data.length];
		Arrays.fill(di, Double.MAX_VALUE);
		
		Random random = new Random(444);
		//first centroid selected randomly
		centroids[0] = data[random.nextInt(data.length)];
		
		//selects remaining k-1 centroids
		for (int j = 1; j < k; j++) {
			double sum = 0;
			//finds minimum distance for each vector
			for (int i = 0; i < data.length; i++) {
				findMinDistance(i, j, data[i]);
				sum += di[i];
			}
			sum = sum * random.nextDouble();
			//select next centroid with probability proportional to D(x)^2
			for (int i = 0; i < data.length; i++) {
				sum -= di[i];
				if (sum > 0) {
					continue;
				}
				centroids[j] = data[i];
				break;
			}
		}
		
		return centroids;
	}
	
	//compares last minimum with new distance between vector and last inserted centroid	
	private void findMinDistance(int i, int count, Vector vector) {
		//distance between vector and last inserted centroid
		double distance = countSqrDistance(centroids[count - 1], vector);
		if (distance < di[i]) {
			di[i] = distance;
		}
	}

	//counts distance (Euclidean distance withnout sqrt)
	private double countSqrDistance(Vector vector, Vector centroid) {
		double sqrDistance = 0;
		for (int i = 0; i < vector.dimension(); i++) {
			sqrDistance += Math.pow((vector.getData()[i] - centroid.getData()[i]), 2);
		}
		return sqrDistance;
	}
}
