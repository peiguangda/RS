import org.apache.mahout.clustering.canopy.Canopy;
import org.apache.mahout.clustering.canopy.CanopyClusterer;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;

import java.util.ArrayList;
import java.util.List;

public class CanopyExample {
    public static void CanopyExample() {
        List<Vector> sampleData = new ArrayList<Vector>();
        RandomPointsUtil.generateSamples(sampleData, 400, 1, 1, 2);
        RandomPointsUtil.generateSamples(sampleData, 300, 1, 0, 0.5);
        RandomPointsUtil.generateSamples(sampleData, 300, 0, 2, 0.1);
        List<Canopy> canopies = CanopyClusterer.createCanopies(
                sampleData, new EuclideanDistanceMeasure(), 3.0, 1.5);
        for(Canopy canopy : canopies) {
            System.out.println("Canopy id: " + canopy.getId()
                    + " center: " +
                    canopy.getCenter().asFormatString());
        }
    }

    public static void main(String[] args) {
        CanopyExample();
    }
}
