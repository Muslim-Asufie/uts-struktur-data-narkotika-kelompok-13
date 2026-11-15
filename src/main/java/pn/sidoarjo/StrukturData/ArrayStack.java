package pn.sidoarjo.StrukturData;

import java.util.EmptyStackException;
import java.util.Objects;

public class ArrayStack<T> implements StrukturData<T> {
    private static final int DEFAULT_CAPACITY = 16;

    private Object[] data;
    private int top;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Kapasitas awal stack harus lebih dari 0");
        }
        this.data = new Object[initialCapacity];
        this.top = -1;
    }

    public void push(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Nilai push tidak boleh null");
        }
        if (top + 1 == data.length) {
            resize(data.length * 2);
        }
        data[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T value = (T) data[top];
        data[top--] = null;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return (T) data[top];
    }
    
    public int searchPreserve(T target) {
        if (target == null || isEmpty()) {
            return -1;
        }
        ArrayStack<T> buffer = new ArrayStack<>(size());
        int depth = 0;
        int foundDepth = -1;

        while (!isEmpty()) {
            T current = pop();
            buffer.push(current);
            if (Objects.equals(current, target)) {
                foundDepth = depth;
                break;
            }
            depth++;
        }

        restore(buffer);
        return foundDepth;
    }

    public void restore(ArrayStack<T> buffer) {
        if (buffer == null) {
            return;
        }
        while (!buffer.isEmpty()) {
            push(buffer.pop());
        }
    }

    @Override
    public void add(T value) {
        push(value);
    }

    @Override
    public T get(int position) {
        if (position < 0 || position > top) {
            throw new IndexOutOfBoundsException("Posisi stack di luar batas: " + position);
        }
        ArrayStack<T> buffer = new ArrayStack<>(position + 1);
        for (int i = 0; i < position; i++) {
            buffer.push(pop());
        }
        T target = peek();
        restore(buffer);
        return target;
    }

    @Override
    public T remove(int position) {
        if (position < 0 || position > top) {
            throw new IndexOutOfBoundsException("Posisi stack di luar batas: " + position);
        }
        ArrayStack<T> buffer = new ArrayStack<>(position);
        for (int i = 0; i < position; i++) {
            buffer.push(pop());
        }
        T removed = pop();
        restore(buffer);
        return removed;
    }

    @Override
    public int size() {
        return top + 1;
    }

    @Override
    public boolean isEmpty() {
        return top == -1;
    }

    @Override
    public boolean contains(T value) {
        return searchPreserve(value) != -1;
    }

    @Override
    public void clear() {
        for (int i = 0; i <= top; i++) {
            data[i] = null;
        }
        top = -1;
    }

    public void validateInvariant() {
        if (data == null) {
            throw new IllegalStateException("Invariant dilanggar: array stack null");
        }
        if (top < -1 || top >= data.length) {
            throw new IllegalStateException("Invariant dilanggar: pointer top tidak valid (" + top + ")");
        }
        for (int i = 0; i <= top; i++) {
            if (data[i] == null) {
                throw new IllegalStateException("Invariant dilanggar: elemen aktif stack null pada indeks " + i);
            }
        }
        for (int i = top + 1; i < data.length; i++) {
            if (data[i] != null) {
                throw new IllegalStateException("Invariant dilanggar: slot di atas top tidak bersih pada indeks " + i);
            }
        }
    }

    private void resize(int newCapacity) {
        Object[] resized = new Object[newCapacity];
        for (int i = 0; i <= top; i++) {
            resized[i] = data[i];
        }
        data = resized;
    }
}

