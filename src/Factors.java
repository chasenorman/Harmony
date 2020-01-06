import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Factors implements Iterable<Integer> {
    private final int n;

    public Factors(int n) {
        this.n = n;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new FactorIterator();
    }

    public class FactorIterator implements Iterator<Integer> {
        private final int[] factors;
        private final int[] ceiling;
        private final int[] next;
        private boolean hasNext = true;

        public FactorIterator() {
            Set<Map.Entry<Integer, Integer>> pf = Primes.primeFactors(n).entrySet();
            factors = new int[pf.size()];
            ceiling = new int[pf.size()];
            int i = 0;
            for (Map.Entry<Integer, Integer> e : pf) {
                factors[i] = e.getKey();
                ceiling[i] = e.getValue();
                i++;
            }
            next = new int[pf.size()];
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Integer next() {
            int result = 1;
            for (int i = 0; i < factors.length; i++) {
                result *= pow(factors[i], next[i]);
            }
            increment();
            return result;
        }

        private void increment() {
            for (int i = 0; i < factors.length; i++) {
                if (next[i] < ceiling[i]) {
                    next[i]++;
                    return;
                } else {
                    next[i] = 0;
                }
            }
            hasNext = false;
        }
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
