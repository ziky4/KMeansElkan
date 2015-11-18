package cz.ziky.kmeans.normalization;

import cz.ziky.kmeans.program.Vector;

public class DataNormalizator {
	
	
	public void normalize(Vector[] data) {
		minMaxNormalization(data);
		//zScoreNormalization(data);
	}
	
	private void minMaxNormalization(Vector[] data) {
		int dimension = data[0].dimension();
		double[] minVector = data[0].getData().clone();
		double[] maxVector = data[0].getData().clone();
		
		for (int i = 1; i < data.length; i++) {
			for (int j = 0; j < dimension; j++) {
				if (data[i].getData()[j] < minVector[j]) {
					minVector[j] = data[i].getData()[j];
				} else if (data[i].getData()[j] > maxVector[j]) {
					maxVector[j] = data[i].getData()[j];
				}
			}
		}
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < dimension; j++) {
				data[i].getData()[j] = (data[i].getData()[j] - minVector[j]) / (maxVector[j] - minVector[j]);
			}
		}
	}
	
	private void zScoreNormalization(Vector[] data) {
		int dimension = data[0].dimension();
		double[] mean = new double[dimension];
		double[] standartDeviation = new double[dimension];
			
		for (int i = 0; i < dimension; i++) {
			double value = 0;
				
			for (int j = 0; j < data.length; j++) {
				value += data[j].getData()[i];
			}
			mean[i] = value / data.length;
		}
		
		
		for (int i = 0; i < dimension; i++) {
			double value = 0;
			
			for (int j = 0; j < data.length; j++) {
				value += Math.pow((data[j].getData()[i] - mean[i]), 2);
			}
			standartDeviation[i] = Math.sqrt((value / data.length));
		}
		
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < dimension; j++) {
				data[i].getData()[j] = (data[i].getData()[j] - mean[j]) / standartDeviation[j];
			}
		}
	}
}
