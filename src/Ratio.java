import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ratio {
    private static Map<Integer, List<Ratio>> memoized = new HashMap<>();

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
            throw new IllegalArgumentException("size >= 2");
        }

        if (size == 2){
            return ratios(complexity);
        }

        List<Ratio> result = new ArrayList<>();
        for (int factor : new Factors(complexity)) {
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

        Iterator<Ratio> iterator = new Iterator<Ratio>() {
            @Override
            public boolean hasNext() {
                return !bset.get(decomposition.length);
            }

            @Override
            public Ratio next() {
                int result = 1;
                for(int i = 0; i < decomposition.length; i++) {
                    if(bset.get(i))
                        result *= decomposition[i];
                }
                //increment bset
                for(int i = 0; i < bset.size(); i++) {
                    if(!bset.get(i)) {
                        bset.set(i);
                        break;
                    } else
                        bset.clear(i);
                }

                return new Ratio(complexity, new int[]{result, complexity/result});
            }
        };

        return () -> iterator;
    }

    public String toString() {
        return IntStream.of(ratio)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(":"));
    }
}