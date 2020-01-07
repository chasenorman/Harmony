import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ratio {
    private static Map<Integer, Iterable<Ratio>> memoized = new HashMap<>();

    static{
        memoized.put(hash(1,1), Collections.singleton(new Ratio(1, new int[]{1})));
    }

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
        int hash = hash(size, complexity);
        if (memoized.containsKey(hash)) {
            return memoized.get(hash);
        }

        if (size < 2) {
            return Collections.emptyList();
        }

        if (size == 2){
            return ratios(complexity);
        }

        List<Ratio> result = new ArrayList<>();
        for (int factor : new Factors(complexity)) {
            if (factor == 5) {
                System.out.println("here");
            }

            Iterable<Ratio> rest = ratios(complexity/factor, size-1);
            for (Ratio r : rest) {
                int[] updated = new int[size];
                System.arraycopy(r.ratio, 0, updated, 0, size-1);
                updated[size-1] = factor;
                result.add(new Ratio(complexity, updated));
            }
        }

        memoized.put(hash, result);
        return result;
    }

    public static Iterable<Ratio> ratios(int complexity) {
        Set<Map.Entry<Integer, Integer>> pf = Primes.primeFactors(complexity).entrySet();
        int[] decomposition = new int[pf.size()];
        int i = 0;
        for (Map.Entry<Integer, Integer> e : pf) {
            decomposition[i] = Factors.pow(e.getKey(), e.getValue());
            i++;
        }
        BitSet bset = new BitSet(decomposition.length + 1);
        int length = 1 << decomposition.length;
        List<Ratio> result = new ArrayList<>(length);

        while(!bset.get(decomposition.length)) {
            int val = 1;
            for (int j = 0; j < decomposition.length; j++) {
                if (bset.get(j))
                    val *= decomposition[j];
            }

            for (int j = 0; j < bset.size(); j++) {
                if (!bset.get(j)) {
                    bset.set(j);
                    break;
                } else
                    bset.clear(j);
            }

            result.add(new Ratio(complexity, new int[]{val, complexity / val}));
        }

        memoized.put(hash(2, complexity), result);
        return result;
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