import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.WeightedPropertyVectorWritable;
import org.apache.mahout.clustering.WeightedVectorWritable;
import org.apache.mahout.clustering.canopy.Canopy;
import org.apache.mahout.clustering.canopy.CanopyClusterer;
import org.apache.mahout.clustering.kmeans.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HelloWorldClustering {

    public static final double[][] points = { {1, 1,1,1,1}, {2, 1,4,5,3}, {1, 2,2,4,3},
            {2, 2,2,2,2}, {3, 3,3,3,3}, {8, 8,8,8,8},
            {9, 8,8,9,8}, {8, 9,9,8,7}, {9, 9,8,8,7}};
    public static void writePointsToFile(List<Vector> points,
                                         String fileName,
                                         FileSystem fs,
                                         Configuration conf) throws IOException {
        Path path = new Path(fileName);

        //ghi 2 truong key= long, value=vecto
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
                path, LongWritable.class, VectorWritable.class);
        long recNum = 0;
        VectorWritable vec = new VectorWritable();
        for (Vector point : points) {
            vec.set(point);
            //append(key, value);
            writer.append(new LongWritable(recNum++), vec);
        }
        writer.close();
    }
    public static List<Vector> getPoints(double[][] raw) {
        List<Vector> points = new ArrayList<Vector>();
        for (int i = 0; i < raw.length; i++) {
            double[] fr = raw[i];

            //doc tu raw vao vecto
            Vector vec = new RandomAccessSparseVector(fr.length);
            vec.assign(fr);

            System.out.println(vec);

            //them vao arraylist
            points.add(vec);
        }
        return points;
    }

    public static void main(String args[]) throws Exception {
        List<Vector> vectors = getPoints(points);

        File testData = new File("testdata");
        if (!testData.exists()) {
            testData.mkdir();
        }
        testData = new File("testdata/points");
        if (!testData.exists()) {
            testData.mkdir();
        }

        Configuration conf = new Configuration();

        FileSystem fs = FileSystem.get(conf);

        //ghi list vecto vao file testdata/points/file1
        writePointsToFile(vectors,
                "testdata/points/file1", fs, conf);

        //ghi 2 vecto ngau nhien lam trong tam vao file part-00000
        Path path = new Path("testdata/clusters/part-00000");

        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, Cluster.class);

        //get k, list vecto  trong tam
        List<Vector> vectorsCanopy = new ArrayList<Vector>(vectors);

        List<Canopy> canopies = CanopyClusterer.createCanopies(vectorsCanopy, new EuclideanDistanceMeasure(), 7,4);

        //ghi vecto trong tam vao file
        for (Canopy canopy: canopies) {

            //canopy nhu la 1 vecto
            Cluster cluster = new Cluster(canopy.getCenter(), canopy.getId(), new EuclideanDistanceMeasure());
            writer.append(new Text(cluster.getIdentifier()), cluster);          //cluster.getIdentifier()--> CL-0, CL-1
        }
        writer.close();

        //run kmean
        long currentTime = System.currentTimeMillis();

        KMeansDriver.run(conf, new Path("testdata/points/file1"),     //file chua tat ca vecto
                new Path("testdata/clusters/part-00000"),             //file chua tat ca vecto trong tam
                new Path("output/" + currentTime + "/"), new EuclideanDistanceMeasure(),
                0.001, 10, true, false);

        System.out.println("output/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000");

        SequenceFile.Reader reader = new SequenceFile.Reader(fs,
                new Path("output/" + currentTime + "/" + Cluster.CLUSTERED_POINTS_DIR + "/part-m-00000"),
                conf);

        IntWritable key = new IntWritable();

        WeightedPropertyVectorWritable value = new WeightedPropertyVectorWritable();

        while (reader.next(key, value)) {

            System.out.println(value.toString() + " belongs to cluster " + key.toString());
        }
        reader.close();
    }
}
