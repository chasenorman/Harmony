import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ratio {
    private static Map<Integer, ArrayList<int[]>> memoized = new HashMap<>();

    int[] ratio;
    int complexity;

    public Ratio(int... numbers) {
        ratio = numbers;
        int gcd = gcd(numbers);

        int product = 1;
        for (int i = 0; i < ratio.length; i++) {
            ratio[i] /= gcd;
            product *= ratio[i];
        }
        complexity = product;

    }

    public Ratio(int c, int[] numbers) {
        ratio = numbers;
        complexity = c;
    }

    public static int lcm(int... numbers) {
        int product = 1;
        for (int i : numbers) {
            product *= i;
        }
        return product/gcd(numbers);
    }

    private static int gcd0(int a, int b) {
        if (a == 0)
            return b;
        return gcd0(b % a, a);
    }

    public static int gcd(int... arr) {
        int result = arr[0];
        for (int i = 1; i < arr.length; i++){
            result = gcd0(arr[i], result);

            if(result == 1)
            {
                return 1;
            }
        }

        return result;
    }

    private static int hash(int a, int b) {
        return (((a+b)*(a+b+1))/2) + b;
    }

    public static Iterable<Ratio> ratios(int complexity, int size) {
        if (complexity == 1) {
            int[] result = new int[size];
            for (int i = 0; i < size; i++){
                result[i] = 1;
            }
            return Collections.singleton(new Ratio(1, result));
        }

        List<ArrayList<int[]>> distributions = new ArrayList<>();
        List<Integer> primes = new ArrayList<>();
        Map<Integer, Integer> pf = Primes.primeFactors(complexity);
        for (Map.Entry<Integer, Integer> e : pf.entrySet()) {
            distributions.add(distributions(size, e.getValue()));
            primes.add(e.getKey());
        }

        int[] indices = new int[primes.size()];

        return ()-> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return indices[indices.length-1] != distributions.get(primes.size()-1).size();
            }

            @Override
            public Ratio next() {
                int[] result = new int[size];
                for (int i = 0; i < size; i++) {
                    result[i] = 1;
                }

                for (int i = 0; i < indices.length; i++) {
                    for (int x = 0; x < size; x++) {
                        result[x] *= Primes.pow(primes.get(i), distributions.get(i).get(indices[i])[x]);
                    }
                }

                increment();

                return new Ratio(complexity, result);
            }

            private void increment() {
                int i = 0;
                while(i < indices.length-1 && indices[i] == distributions.get(i).size() - 1) {
                    indices[i++] = 0;
                }
                indices[i]++;
            }
        };
    }

    public static ArrayList<int[]> distributions(int length, int allocate) {
        int hash = hash(length, allocate);
        if (memoized.containsKey(hash)) {
            return memoized.get(hash);
        }

        ArrayList<int[]> total = new ArrayList<>();
        for (int zeroes = Math.max(length - allocate, 1); zeroes < length; zeroes++) {
            for (int[] locations : zeroLocations(length, zeroes)) {
                for (int[] composition : compositions(length - zeroes, allocate)) {
                    int[] result = new int[length];
                    int zi = 0;
                    int ci = 0;
                    for (int i = 0; ci < length - zeroes; i++) {
                        if (zi < zeroes && locations[zi] == i) {
                            zi++;
                        } else {
                            result[i] = composition[ci++];
                        }
                    }
                    total.add(result);
                }
            }
        }

        memoized.put(hash, total);
        return total;
    }

    public static Iterable<int[]> zeroLocations(int length, int zeroes) {
        int[] locations = new int[zeroes];
        for (int i = 0; i < zeroes; i++) {
            locations[i] = i;
        }
        locations[zeroes-1]--;

        return ()->new Iterator<>() {
            @Override
            public boolean hasNext() {
                return locations[0] < length - zeroes;
            }

            @Override
            public int[] next() {
                for (int i = zeroes-1; i >= 0; i--) {
                    if (locations[i] < length - i) {
                        locations[i]++;
                        for (int j = i+1; j < zeroes; j++) {
                            locations[j] = locations[i] + (j-i);
                        }
                        break;
                    }
                }

                return locations;
            }
        };
    }

    public static Iterable<int[]> compositions(int length, int allocate) {
        if (allocate < length) {
            return Collections.emptyList();
        }

        if (length == 1) {
            return Collections.singleton(new int[]{allocate});
        }

        int[] s = new int[length];
        for (int i = 0; i < length-1; i++) {
            s[i] = 1;
        }
        s[length - 1] = allocate - length + 2;
        s[length - 2] = 0;


        return ()-> new Iterator<>() {
            @Override
            public boolean hasNext() {
                return s[0] != allocate - length + 1;
            }

            @Override
            public int[] next() {
                for (var i = length - 1; i >= 1; i--) {
                    if (s[i] > 1) {
                        s[i]--;
                        s[i-1]++;
                        for (; i < length - 1; i++) {
                            s[length-1] += s[i] - 1;
                            s[i] = 1;
                        }
                        return s;
                    }
                }
                return null;
            }
        };
    }

    public String toString() {
        return IntStream.of(ratio)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(":"));
    }

    public boolean equals(Object o) {
        return o instanceof Ratio && Arrays.equals(((Ratio)o).ratio, ratio);
    }
}