package cz.ziky.kmeans.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cz.ziky.kmeans.normalization.DataNormalizator;

public class Program {

	public static void main(String[] args) {
		BufferedReader br;
		String line = null;
		Vector data[] = new Vector[581012];
		
		try {
			br = new BufferedReader(new FileReader(new File("covtype.data")));
			int i = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				double[] vector = new double[10];
				for (int j = 0; j < 10; j++) {
					vector[j] = Double.parseDouble(values[j]);
				}
				data[i] = new Vector(vector);
				i++;
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
 
		DataNormalizator normalizator = new DataNormalizator();
		normalizator.normalize(data);
		KMeans kMeans = new KMeans(7, 200, data);
		kMeans.run();
	}

}
