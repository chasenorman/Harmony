import java.util.*;

public class Sequence extends AbstractList<Float> {
    private ArrayList<Float> list = new ArrayList<>();

    @Override
    public Float get(int i) {
        return (i < 0 || i >= list.size()) ? null : list.get(i);
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Float set(int index, Float value) {
        if (value == null && index >= list.size()) {
            return null;
        }
        expand(index+1);
        return list.set(index, value);
    }

    @Override
    public Float remove(int index) {
        if (index < list.size()) {
            return list.remove(index);
        }
        return null;
    }

    @Override
    public void add(int index, Float value) {
        if (value != null) {
            expand(index);
            list.add(value);
        }
    }

    private void expand(int index) {
        for (int i = list.size(); i < index; i++) {
            list.add(i, null);
        }
    }
}
