package cz.ziky.kmeans.program;

import cz.ziky.kmeans.clusterer.KMeansClusterer;
import cz.ziky.kmeans.clusterer.KMeansElkan;
import cz.ziky.kmeans.initializer.KMeansInitializer;
import cz.ziky.kmeans.initializer.KmeansPlusPlus;

public class KMeans {
	private KMeansInitializer initializer;
	private KMeansClusterer clusterer;
	
	public KMeans(int k, int maxIterations, Vector[] data) {
		this.initializer = new KmeansPlusPlus();
		this.clusterer = new KMeansElkan(k, maxIterations, data);
	}
	
	public void run() {
		clusterer.initCentroids(initializer);
		long time1 = System.nanoTime();
		clusterer.cluster();
		long time2 = System.nanoTime();
		System.out.println((time2 - time1)/1000000);
	}
}
