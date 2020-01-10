import java.util.ArrayList;
import java.util.Collections;
import java.util.function.DoubleUnaryOperator;

public class RangeFunction implements DoubleUnaryOperator {
    ArrayList<Double> lows = new ArrayList<Double>();
    ArrayList<Double> highs = new ArrayList<Double>();
    ArrayList<Double> values = new ArrayList<Double>();
    public final double fallback;

    public RangeFunction(double d) {
        fallback = d;
    }

    public RangeFunction() {
        this(0);
    }

    public void add(double low, double high, double value) {
        if (low > high) {
            return;
        }

        int idx = Collections.binarySearch(lows, low);
        if (idx < 0) {
            idx = -(idx + 1);
            if (idx > 0) {
                highs.set(idx - 1, Math.min(highs.get(idx - 1), low));
                if (highs.get(idx - 1) <= lows.get(idx - 1)) {
                    highs.remove(idx - 1);
                    lows.remove(idx - 1);
                    values.remove(idx - 1);
                    idx--;
                }
            }
        }
        lows.add(idx, low);
        highs.add(idx, high);
        values.add(idx, value);
        if (idx < lows.size() - 1) {
            lows.set(idx + 1, Math.max(lows.get(idx + 1), high));
            if (highs.get(idx + 1) <= lows.get(idx + 1)) {
                highs.remove(idx + 1);
                lows.remove(idx + 1);
                values.remove(idx + 1);
            }
        }
    }

    @Override
    public double applyAsDouble(double v) {
        int idx = Collections.binarySearch(lows, v);
        if (idx < 0) {
            idx = -idx - 2;
        }

        if (idx == -1 || highs.get(idx) < v) {
            return fallback;
        }

        return values.get(idx);
    }

    public int rangeAt(double v) {
        int idx = Collections.binarySearch(lows, v);
        if (idx < 0) {
            idx = -idx - 2;
        }

        if (idx == -1) {
            return 0;
        }

        if (highs.get(idx) < v) {
            return idx + 1;
        }

        return idx;
    }
}
