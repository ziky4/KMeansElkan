package cz.ziky.kmeans.initializer;

import java.util.Random;

import cz.ziky.kmeans.program.Vector;

public class KmeansPlusPlus implements KMeansInitializer {
	private Vector[] centroids;
	
	@Override
	public Vector[] initCentroids(Vector[] data, int k) {
		centroids = new Vector[k];
		
		Random random = new Random();
		centroids[0] = data[random.nextInt(data.length)];
		
		double[] di = new double[data.length];
		for (int j = 1; j < k; j++) {
			double sum = 0;
			for (int i = 0; i < data.length; i++) {
				di[i] = findMinDistance(j, data[i]);
				sum += di[i];
			}
			sum = sum * random.nextDouble(); 
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
	
	private double findMinDistance(int count, Vector vector) {
		double nearestDistance = countSqrDistance(centroids[0], vector);
		for (int i = 1; i < count; i++) {
			double distance = countSqrDistance(centroids[i], vector);
			if (distance < nearestDistance) {
				nearestDistance = distance;
			}
		}
		return nearestDistance * nearestDistance;
	}

	private double countSqrDistance(Vector vector, Vector centroid) {
		double sqrDistance = 0;
		for (int i = 0; i < vector.dimension(); i++) {
			sqrDistance += Math.pow((vector.getData()[i] - centroid.getData()[i]), 2);
		}
		return sqrDistance;
	}
	
}
