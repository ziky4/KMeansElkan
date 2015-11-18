package cz.ziky.kmeans.clusterer;

import cz.ziky.kmeans.initializer.KMeansInitializer;

public interface KMeansClusterer {
	public void cluster();
	public void initCentroids(KMeansInitializer initializer);
}
