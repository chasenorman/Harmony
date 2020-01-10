import java.util.*;

public class Primes implements Iterable<Integer> {
    private static Map<Integer, Map<Integer, Integer>> factorizations = new HashMap<>();
    private static ArrayList<Integer> memoized = new ArrayList<>();
    private static ArrayList<Integer> counts = new ArrayList<>();

    @Override
    public Iterator<Integer> iterator() {
        return new PrimesIterator();
    }

    public static class PrimesIterator implements Iterator<Integer> {
        int next = 2;
        int index = 0;

        public PrimesIterator() {
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public Integer next() {
            if (index < memoized.size()) {
                next = memoized.get(index) + 1;
                return memoized.get(index++);
            }
            boolean composite;
            while (true) {
                composite = false;
                for (int i = 0; i < memoized.size(); i++) {
                    counts.set(i, counts.get(i) + 1);
                    if (counts.get(i).equals(memoized.get(i))) {
                        composite = true;
                        counts.set(i, 0);
                    }
                }
                if (!composite) {
                    memoized.add(next);
                    counts.add(0);
                    index++;
                    return next++;
                }
                next++;
            }
        }
    }

    public static Map<Integer, Integer> primeFactors(int number) {
        return primeFactors0(number, new Primes.PrimesIterator());
    }

    private static Map<Integer, Integer> primeFactors0(int number, PrimesIterator pi) {
        if (factorizations.containsKey(number)) {
            return factorizations.get(number);
        }

        Map<Integer, Integer> factors = new HashMap<>();
        int k;
        int n = number;
        int p = pi.next();
        k = 0;
        while (n % p == 0) {
            k++;
            n /= p;
        }

        if (k != 0) {
            factors.put(p, k);
        }

        if (n > 1) {
            factors.putAll(primeFactors0(n, pi));
        }

        factorizations.put(number, factors);
        return factors;
    }

    public static int pow (int a, int b) {
        if ( b == 0)
            return 1;
        if ( b == 1)
            return a;
        if (b % 2 == 0)
            return pow ( a * a, b/2);
        else
            return a * pow ( a * a, b/2);
    }
}