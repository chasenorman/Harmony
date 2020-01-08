public class Harmony {
    public static double tolerance = 0.015;
    public static int maxComplexity = 10;

    public static Ratio ratio(double... frequencies) {
        for (int complexity = 1; complexity < Factors.pow(maxComplexity, frequencies.length); complexity++) {
            for (Ratio r : Ratio.ratios(complexity, frequencies.length)) {
                if (difference(r, frequencies) < tolerance) {
                    return r;
                }
            }
        }
        return null;
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
        for (int complexity = Factors.pow(maxComplexity, frequencies.length+1) - 1; complexity > 0; complexity--) {
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
}
