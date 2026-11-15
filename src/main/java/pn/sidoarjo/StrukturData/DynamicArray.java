package pn.sidoarjo.StrukturData;

import java.util.Objects;

public class DynamicArray<T> implements StrukturData<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] elements;
    private int size;
    private int lastSearchSteps;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Kapasitas awal harus lebih besar dari 0");
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
        this.lastSearchSteps = 0;
    }

    @Override
    public void add(T value) {
        ensureNotNull(value);
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[size++] = value;
    }

    public void insert(int position, T value) {
        ensureNotNull(value);
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException("Posisi sisip tidak valid: " + position);
        }
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        for (int i = size; i > position; i--) {
            elements[i] = elements[i - 1];
        }
        elements[position] = value;
        size++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get(int position) {
        checkElementIndex(position);
        return (T) elements[position];
    }

    @SuppressWarnings("unchecked")
    public T set(int position, T value) {
        ensureNotNull(value);
        checkElementIndex(position);
        T oldValue = (T) elements[position];
        elements[position] = value;
        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove(int position) {
        checkElementIndex(position);
        T removed = (T) elements[position];
        for (int i = position; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;

        if (size > 0 && size <= elements.length / 4 && elements.length > DEFAULT_CAPACITY) {
            resize(Math.max(DEFAULT_CAPACITY, elements.length / 2));
        }
        return removed;
    }

    public int search(T target) {
        lastSearchSteps = 0;
        if (target == null) {
            return -1;
        }
        for (int i = 0; i < size; i++) {
            lastSearchSteps++;
            if (Objects.equals(elements[i], target)) {
                return i;
            }
        }
        return -1;
    }

    public int getLastSearchSteps() {
        return lastSearchSteps;
    }

    public int capacity() {
        return elements.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(T value) {
        return search(value) != -1;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
        lastSearchSteps = 0;
    }

    public void resize(int newCapacity) {
        if (newCapacity < size) {
            throw new IllegalArgumentException("Kapasitas baru tidak boleh lebih kecil dari size saat ini");
        }
        Object[] resized = new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            resized[i] = elements[i];
        }
        elements = resized;
    }

    public void validateInvariant() {
        if (elements == null) {
            throw new IllegalStateException("Invariant dilanggar: array internal null");
        }
        if (size < 0 || size > elements.length) {
            throw new IllegalStateException("Invariant dilanggar: size berada di luar rentang kapasitas");
        }
        for (int i = 0; i < size; i++) {
            if (elements[i] == null) {
                throw new IllegalStateException("Invariant dilanggar: terdapat elemen aktif null pada indeks " + i);
            }
        }
        for (int i = size; i < elements.length; i++) {
            if (elements[i] != null) {
                throw new IllegalStateException("Invariant dilanggar: sisa slot non-aktif tidak null pada indeks " + i);
            }
        }
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Indeks di luar batas: " + index + ", size: " + size);
        }
    }

    private void ensureNotNull(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Elemen tidak boleh null");
        }
    }
}
