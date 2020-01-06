public class Harmony {
    public static double tolerance = 0.02;
    public static int maxComplexity = Integer.MAX_VALUE;

    public static Ratio ratio(double... frequencies) {
        for (int complexity = 1; complexity < maxComplexity; complexity++) {
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

        double total = 0;
        for (int i = 0; i < frequencies.length; i++) {
            total += Math.abs(arr[i]-average);
        }
        return total/average;
    }

    public static double fundamental(double... frequencies) {
        return fundamental(ratio(frequencies), frequencies);
    }

    public static double fundamental(Ratio r, double... frequencies) {
        double s1 = 0, s2 = 0;
        for (int i = 0; i < frequencies.length; i++) {
            s1 += frequencies[i];
            s2 += r.ratio[i];
        }
        return s1/s2;
    }
}
