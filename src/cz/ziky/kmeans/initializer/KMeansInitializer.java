package cz.ziky.kmeans.initializer;

import cz.ziky.kmeans.program.Vector;

public interface KMeansInitializer {
	public Vector[] initCentroids(Vector[] data, int k);
}
