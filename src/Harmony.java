import com.sun.security.jgss.GSSUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Harmony {
    public static double tolerance = 0.015;
    private static int maxComplexity = 10;
    public static int n = 1000;

    public static RangeFunction harmony = harmony(1);

    public static Ratio ratio(double... frequencies) {
        for (int complexity = 1; complexity < maxComplexity(frequencies.length); complexity++) {
            for (Ratio r : Ratio.ratios(complexity, frequencies.length)) {
                if (difference(r, frequencies) < tolerance) {
                    return r;
                }
            }
        }
        return null;
    }

    public static int maxComplexity(int n) {
        return Primes.pow(maxComplexity, n);
    }

    private static double difference(Ratio r, double[] frequencies) {
        double[] arr = new double[frequencies.length];
        double average = 0;
        for (int i = 0; i < frequencies.length; i++) {
            arr[i] = frequencies[i]*r.complexity/r.ratio[i];
            average += arr[i];
        }
        average /= frequencies.length;

        for (int i = 0; i < frequencies.length; i++) {
            arr[i] -= average;
        }

        return norm2(arr)/average;
    }

    public static double norm1(double[] point) {
        double total = 0;
        for (int i = 0; i < point.length; i++) {
            total += Math.abs(point[i]);
        }
        return total;
    }

    public static double norm2(double[] point) {
        double total = 0;
        for (int i = 0; i < point.length; i++) {
            total += point[i]*point[i];
        }
        return Math.sqrt(total);
    }

    public static double fundamental(double... frequencies) {
        return fundamental(ratio(frequencies), frequencies);
    }

    public static double fundamental(Ratio r, double... frequencies) {
        double total = 0;
        for (int i = 0; i < frequencies.length; i++) {
            total += frequencies[i]/r.ratio[i];
        }
        return total/frequencies.length; //TODO maybe change this metric and distance metric along with.
    }

    public static RangeFunction harmony(double... frequencies) {
        RangeFunction f = new RangeFunction(Double.MAX_VALUE);
        for (int complexity = maxComplexity(frequencies.length + 1) - 1; complexity > 0; complexity--) {
            for (Ratio r : Ratio.ratios(complexity, frequencies.length+1)) {
                double[] arr = new double[frequencies.length];

                double avg_estimate = 0; //TODO make true value.
                for (int i = 0; i < frequencies.length; i++) {
                    arr[i] = frequencies[i]*complexity/r.ratio[i];
                    avg_estimate += arr[i];
                }
                avg_estimate /= frequencies.length;

                double b = 0;

                for (int i = 0; i < frequencies.length; i++) {
                    double j = arr[i] - avg_estimate;
                    b += j*j;
                }

                double toSquare = tolerance*avg_estimate/(frequencies.length+1);
                double descriminant = toSquare*toSquare - b;

                if (descriminant <= 0) {
                    continue;
                }

                double width = Math.sqrt(descriminant);
                double multiplier = r.ratio[frequencies.length]/(double)complexity;

                f.add(multiplier*(avg_estimate-width), multiplier*(avg_estimate+width), complexity);
            }
        }
        return f;
    }

    /*public static RangeFunction harmony(double... frequencies) {
        RangeFunction f = new RangeFunction(Double.MAX_VALUE);
        int n = frequencies.length+1;
        for (int complexity = maxComplexity(n) - 1; complexity > 0; complexity--) {
            for (Ratio r : Ratio.ratios(complexity, n)) {
                double avg = 0;
                for (int i = 0; i < frequencies.length; i++) {
                    avg += complexity*frequencies[i]/r.ratio[i];
                }
                avg /= n;

                double nrc = complexity/(double)(n*r.ratio[frequencies.length]);

                double avgSum = 0;
                double avgSquareSum = 0;
                for (double frequency : frequencies) {
                    avgSum += (frequency - avg);
                    avgSquareSum += (frequency - avg) * (frequency - avg);
                }

                double a = (n-1-tolerance*tolerance)*(nrc*nrc) + (1-nrc)*(1-nrc);
                double b = -2*(avgSum*nrc + avg*(1+(tolerance*tolerance-1)*nrc));
                double c = avgSquareSum + avg*avg*(1-tolerance*tolerance);

                double descriminant = b*b - 4*a*c;

                System.out.println(descriminant);

                if (descriminant <= 0) {
                    continue;
                }

                double center = -b/(2*a);
                double width = Math.sqrt(descriminant)/(2*a);

                System.out.println(center);

                f.add(center-width, center+width, complexity);
            }
        }
        return f;
    }*/

    public static int complexity(float f1, float f2) {
        return (int)harmony.applyAsDouble(f1/f2);
    }
}
