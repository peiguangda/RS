import org.apache.mahout.clustering.dirichlet.UncommonDistributions;
import org.apache.mahout.clustering.kmeans.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansClusterer;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class KmeanClustering {
    private static void generateSamples(List<Vector> vectors, int num,
                                        double mx, double my, double sd) {
        for (int i = 0; i < num; i++) {
            vectors.add(new DenseVector(
                    new double[] {
                            UncommonDistributions.rNorm(mx, sd),
                            UncommonDistributions.rNorm(my, sd)
                    }
            ));
        }
    }
    public static void main(String[] args) {
        List<Vector> sampleData = new ArrayList<Vector>();
//        generateSamples(sampleData, 400, 1, 1, 3);
//        generateSamples(sampleData, 300, 1, 0, 0.5);
//        generateSamples(sampleData, 300, 0, 2, 0.1);
        generateSamples(sampleData, 1, 1, 1, 1);
        generateSamples(sampleData, 1, 2, 2, 2);
        generateSamples(sampleData, 1, 3, 3, 3);
        int k = 3;

        //chon 1 diem lam trong tam trong sampleData
        List<Vector> randomPoints = RandomPointsUtil.chooseRandomPoints(
                sampleData, k);
        System.out.println("random point: " + randomPoints);

        //khoi tao cluster cung voi trong tam cua moi cluster
        List<Cluster> clusters = new ArrayList<Cluster>();
        int clusterId = 0;
        for (Vector v : randomPoints) {
            clusters.add(new Cluster(v, clusterId++,
                    new EuclideanDistanceMeasure()));
        }

        //run kmean
        List<List<Cluster>> finalClusters
                = KMeansClusterer.clusterPoints(sampleData, clusters,
                new EuclideanDistanceMeasure(), 3, 0.01);

        //print output
        System.out.println("center: " + finalClusters.get(finalClusters.size()-1));
        for(Cluster cluster : finalClusters.get(
                finalClusters.size() - 1)) {
            System.out.println("Cluster id: " + cluster.getId()
                    + " center: " + cluster.getCenter() + "\tcount: " +
                    cluster.count());
        }
    }
}
