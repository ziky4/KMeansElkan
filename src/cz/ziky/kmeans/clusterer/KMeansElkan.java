package cz.ziky.kmeans.clusterer;

import java.util.Arrays;
import java.util.Random;

import cz.ziky.kmeans.initializer.KMeansInitializer;
import cz.ziky.kmeans.program.Cluster;
import cz.ziky.kmeans.program.Vector;

public class KMeansElkan implements KMeansClusterer{
	private int k;					//number of clusters
	private int maxIterations;		//maximum number of iterations
	private Cluster[] clusters;		//clusters with indexes of assigned vectors
	private Vector[] data;			//input data set
	private int[] assignments;		//information about to which cluster is vector assigned
	private double[][] lowerBounds;	//distances for vector and all clusters centroids
	private double[] upperBounds;	//distance between each point and its assigned cluster centroid
	private int iterationCount;		//current iteration count
	
	public KMeansElkan(int k, int maxIterations, Vector[] data) {
		this.k = k;
		this.maxIterations = maxIterations;
		this.clusters = new Cluster[k];			
		this.data = data;				
		this.assignments = new int[data.length];
		this.lowerBounds = new double[data.length][k];
		this.upperBounds = new double[data.length];
		this.iterationCount = 0;
	}
	
	@Override
	public void cluster() {
		run();
	}

	@Override
	public void initCentroids(KMeansInitializer initializer) {
		//returns vectors from input data set
		Vector[] centroids = initializer.initCentroids(data, k);
		initializer = null;
		
		//sets centroid for each cluster
		for (int i = 0; i < k; i++) {
			clusters[i] = new Cluster(new Vector(centroids[i].getData().clone()));
		}
	}
	
	public void run() {
		initArrays();
		int moves; //number of moves in each iteration
		
		do {
			//no need to remember old assignments
			clearAssignments();
			double[][] centroidDistances = countCentroidsDistances();
			double[] sc = computeSc(centroidDistances);
			moves = assignData(centroidDistances, sc);
			double[] moveDistances = updateCentroids();
			updateBounds(moveDistances);
			iterationCount++;
			//System.out.println(moves);
		} while (converges(iterationCount, moves));
		System.out.println(iterationCount);
		countSSE();
	}
	
	//assign each vector to cluster
	private int assignData(double[][] centroidDistances, double[] sc) {
		int moves = 0;
		
		for (int i = 0; i < data.length; i++) {
			int assignment = assignments[i]; //cluster index to which will be vector assigned
			//vector remain in same cluster - no need to calculate distances
			if (upperBounds[i] > sc[assignment]) {
				boolean r = true; //indicates that upper bound needs to be recalculated
				for (int j = 0; j < k; j++) {
					//different cluster indexes
					if (j != assignment) {
						double z = Math.max(lowerBounds[i][j], (centroidDistances[assignment][j] * 0.5));
						//vector can't be closer to cluster j => at this point will remain in same cluster
						//no need to calculate distance => can continue with another cluster
						if (upperBounds[i] <= z) {
							continue;
						}
						//vector should move to cluster j => need to know distance to currently assigned vector
						if (r) {
							double distance = countDistance(data[i], clusters[assignment].getCentroid());
							upperBounds[i] = distance;
							lowerBounds[i][assignment] = distance;
							r = false;
							//vector i'snt closer to cluster j => will remain in same cluster
							//no need to calculate distance => can continue with another cluster 
							if (upperBounds[i] <= z) {
								continue;
							}
						}
						double distance = countDistance(data[i], clusters[j].getCentroid());
						lowerBounds[i][j] = distance;
						//vector is closer to cluster j => need to change his assignment and upper bound
						if (distance < upperBounds[i]) {
							assignment = j;
							upperBounds[i] = distance;
							//moves++;
						}
					}
				}
				//vector assignment was changed
				if (assignment != assignments[i]) {
					assignments[i] = assignment;
					moves++;
				//first iteration => initial assignment
				} else if (iterationCount == 0) {
					moves++;
				}
			}
			clusters[assignment].add(i);
		}
		return moves;
	}
	
	//update upper and lower bound for each vector =>
	//increase/decreases value by distance moved by centroid
	private void updateBounds(double[] moveDistances) {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < k; j++) {
				lowerBounds[i][j] -= moveDistances[j];
				if (lowerBounds[i][j] < 0) {
					lowerBounds[i][j] = 0;
				}
			}
			upperBounds[i] += moveDistances[assignments[i]];
			//rx[i] = true;
		}
	}
	
	//removes assignments from clusters
	private void clearAssignments() {
		for (int i = 0; i < k; i++) {
			clusters[i].clearAssigments();
		}
	}
	
	//computes distance between each centroid and its new position
	private double[] updateCentroids() {
		double[] moveDistances = new double[k];
		
		for (int i = 0; i < k; i++) {
			Vector newCentroid = clusters[i].countCentroid(data);
			moveDistances[i] = countDistance(clusters[i].getCentroid(), newCentroid);
			clusters[i].setCentroid(newCentroid);
		}
		
		return moveDistances;
	}

	//count distance between each centroid
	private double[][] countCentroidsDistances() {
		double[][] centroidDistances = new double[k][k];
		
		for (int i = 0; i < k - 1; i++) {
			for (int j = 1 + i; j < k; j++) {
				double distance = countDistance(clusters[i].getCentroid(), clusters[j].getCentroid());
				centroidDistances[i][j] = distance;
				centroidDistances[j][i] = distance;
			}
		}
		
		return centroidDistances;
	}
	
	//compute s(c) = 0.5 * min(d(c,c')) for each centroid
	private double[] computeSc(double[][] centroidDistances) {
		double sc[] = new double[k];
		for (int i = 0; i < k; i++) {
			int minIndex = 0;
			if (i == 0) {
				minIndex = 1;
			}
			
			for (int j = 0; j < k; j++) {
				if (i != j) {
					if (centroidDistances[i][j] < centroidDistances[i][minIndex]) {
						minIndex = j;
					}
				}
			}
			sc[i] = 0.5 * centroidDistances[i][minIndex];
		}
		return sc;
	}
	
	private double countUpperBound(int pointIndex, Vector[] clustersMeans) {
		double upperBound = upperBounds[pointIndex] + 
				countDistance(clustersMeans[assignments[pointIndex]],
						clusters[assignments[pointIndex]].getCentroid());
		
		return upperBound;
	}
	
	private double countLowerBound(int pointIndex, int clusterIndex, Vector[] clustersMeans) {
		double lowerBound = lowerBounds[pointIndex][clusterIndex] - 
				countDistance(clusters[clusterIndex].getCentroid(), clustersMeans[clusterIndex]);
		if (lowerBound < 0) {
			return 0;
		}
		return lowerBound;
	}
	
	private Vector[] countClustersMeans() {
		Vector[] clustersMeans = new Vector[k];
		
		for (int i = 0; i < k; i++) {
			Vector clusterMean = new Vector(data[0].dimension());
			for (int j = 0; j < clusters[i].getCount(); j++) {
				Vector curentVector = data[clusters[i].getAssignedData().get(j)];
				for (int k = 0; k < clusterMean.dimension(); k++) {
					clusterMean.getData()[k] += curentVector.getData()[k];
				}
			}
			for (int j = 0; j < clusterMean.dimension(); j++) {
				clusterMean.getData()[i] = clusterMean.getData()[i] / clusters[i].getCount();
			}
			clustersMeans[i] = clusterMean;
		}
		return clustersMeans;
	}
	
	private void initCentroids() {
		Random random = new Random();
		int dataLength = data.length;
		int[] tmp = new int[k];
		
		for (int i = 0; i < k; i++) {
			int randInt = random.nextInt(dataLength);
			if (contains(tmp, randInt)) {
				i--;
			} else {
				tmp[i] = randInt;
			}
		}
		
		for (int i = 0; i < k; i++) {
			clusters[i] = new Cluster(data[tmp[i]]);
		}
	}
	
	private boolean contains(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value) {
				return true;
			}
		}
		return false;
	}
	
	private void initArrays() {
		//Arrays.fill(lowerBounds, 0.0);
		//Arrays.fill(assignments, 0);
		Arrays.fill(upperBounds, Double.MAX_VALUE);
		//Arrays.fill(rx, true);
		//initialAssign();
	}
	
	private void initialAssign() {
		double[][] centroidDistances = countCentroidsDistances();
		for (int i = 0; i < data.length; i++) {
			int nearestClusterIndex = 0;
			//distance to assigned vector
			double currentDistance = countDistance(data[i], clusters[nearestClusterIndex].getCentroid());
			lowerBounds[i][nearestClusterIndex] = currentDistance;
			
			for (int j = 1; j < k; j++) {
				//need to compute distance
				if (centroidDistances[nearestClusterIndex][j] * 0.5 < currentDistance) {
					double distance = countDistance(data[i], clusters[j].getCentroid());
					lowerBounds[i][j] = distance;
					//need to change assigment
					if (distance < currentDistance) {
						currentDistance = distance;
						nearestClusterIndex = j;
					}
				}
			}
			assignments[i] = nearestClusterIndex;
			clusters[nearestClusterIndex].add(i);
			//need to set upper bound
			//currentDistance is equal to lowest distance
			//=> distance between assigned centroid
			upperBounds[i] = currentDistance;
		}
	}
	
	//count distance between two vectors
	private double countDistance(Vector vector, Vector centroid) {
		return vector.countDistance(centroid);
	}
	
	private boolean converges(int iterationCount, int moves) {
		if (moves == 0) {
			return false;
		}
		if (iterationCount >= maxIterations) {
			return false;
		}
		return true;
	}
	
	public void countSSE() {
		double sse = 0;
		for (int i = 0; i < k; i++) {
			sse += clusters[i].countClusterSSE(data);
		}
		System.out.println(sse);
	}
}
