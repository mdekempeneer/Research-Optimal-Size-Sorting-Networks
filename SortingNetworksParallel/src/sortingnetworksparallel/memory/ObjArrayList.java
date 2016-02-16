/* Generic definitions */
/* Assertions (useful to generate conditional code) */
/* Current type and class (and size, if applicable) */
/* Value methods */
/* Interfaces (keys) */
/* Interfaces (values) */
/* Abstract implementations (keys) */
/* Abstract implementations (values) */
/* Static containers (keys) */
/* Static containers (values) */
/* Implementations */
/* Synchronized wrappers */
/* Unmodifiable wrappers */
/* Other wrappers */
/* Methods (keys) */
/* Methods (values) */
/* Methods (keys/values) */
/* Methods that have special names depending on keys (but the special names depend on values) */
/* Equality */
/* Object/Reference-only definitions (keys) */
/* Object/Reference-only definitions (values) */
/*		 
 * Copyright (C) 2002-2015 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package sortingnetworksparallel.memory;

import it.unimi.dsi.fastutil.objects.AbstractObjectList;
import it.unimi.dsi.fastutil.objects.AbstractObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterators;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A type-specific array-based list; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * WARNING: This class has been modified so remove will not change the actual
 * array but keeps a null value in its place. fixNulls() should be used to fix
 * the array when this is needed. If this is a unwanted effect, use
 * ObjectArrayList instead.
 *
 *
 * <P>
 * This class implements a lightweight, fast, open, optimized, reuse-oriented
 * version of array-based lists. Instances of this class represent a list with
 * an array that is enlarged as needed when new entries are created (by doubling
 * the current length), but is <em>never</em> made smaller (even on a
 * {@link #clear()}). A family of {@linkplain #trim() trimming methods} lets you
 * control the size of the backing array; this is particularly useful if you
 * reuse instances of this class. Range checks are equivalent to those of
 * {@link java.util}'s classes, but they are delayed as much as possible.
 *
 * <p>
 * The backing array is exposed by the {@link #elements()} method. If an
 * instance of this class was created
 * {@linkplain #wrap(Object[],int) by wrapping}, backing-array reallocations
 * will be performed using reflection, so that {@link #elements()} can return an
 * array of the same type of the original array: the comments about efficiency
 * made in {@link it.unimi.dsi.fastutil.objects.ObjectArrays} apply here.
 * Moreover, you must take into consideration that assignment to an array not of
 * type {@code Object[]} is slower due to type checking.
 *
 * <p>
 * This class implements the bulk methods <code>removeElements()</code>,
 * <code>addElements()</code> and <code>getElements()</code> using
 * high-performance system calls (e.g.,
 * {@link System#arraycopy(Object,int,Object,int,int) System.arraycopy()}
 * instead of expensive loops.
 *
 * @see java.util.ArrayList
 */
public class ObjArrayList<K> extends AbstractObjectList<K> implements RandomAccess, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = -7046029254386353131L;
    /**
     * The initial default capacity of an array list.
     */
    public final static int DEFAULT_INITIAL_CAPACITY = 64;
    /**
     * Whether the backing array was passed to <code>wrap()</code>. In this
     * case, we must reallocate with the same type of array.
     */
    protected final boolean wrapped;
    /**
     * The backing array.
     */
    protected transient K a[];
    /**
     * The current actual size of the list (never greater than the backing-array
     * length).
     */
    protected AtomicInteger size = new AtomicInteger();
    private static final boolean ASSERTS = false;
    private boolean nullFlag = false;

    /**
     * Creates a new array list using a given array.
     *
     * <P>
     * This constructor is only meant to be used by the wrapping methods.
     *
     * @param a the array that will be used to back this array list.
     */
    @SuppressWarnings("unused")
    protected ObjArrayList(final K a[], boolean dummy) {
        this.a = a;
        this.wrapped = true;
    }

    /**
     * Creates a new array list with given capacity.
     *
     * @param capacity the initial capacity of the array list (may be 0).
     */
    @SuppressWarnings("unchecked")
    public ObjArrayList(final int capacity) {
        a = (K[]) new Object[capacity];
        wrapped = false;
    }

    /**
     * Creates a new array list with {@link #DEFAULT_INITIAL_CAPACITY} capacity.
     */
    public ObjArrayList() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new array list and fills it with a given collection.
     *
     * @param c a collection that will be used to fill the array list.
     */
    public ObjArrayList(final Collection<? extends K> c) {
        this(c.size());
        size = new AtomicInteger(ObjectIterators.unwrap(c.iterator(), a));
    }

    /**
     * Creates a new array list and fills it with a given type-specific
     * collection.
     *
     * @param c a type-specific collection that will be used to fill the array
     * list.
     */
    public ObjArrayList(final ObjectCollection<? extends K> c) {
        this(c.size());
        size = new AtomicInteger(ObjectIterators.unwrap(c.iterator(), a));
    }

    /**
     * Creates a new array list and fills it with a given type-specific list.
     *
     * @param l a type-specific list that will be used to fill the array list.
     */
    public ObjArrayList(final ObjectList<? extends K> l) {
        this(l.size());
        l.getElements(0, a, 0, (size = new AtomicInteger(l.size())).get());
    }

    /**
     * Creates a new array list and fills it with the elements of a given array.
     *
     * @param a an array whose elements will be used to fill the array list.
     */
    public ObjArrayList(final K a[]) {
        this(a, 0, a.length);
    }

    /**
     * Creates a new array list and fills it with the elements of a given array.
     *
     * @param a an array whose elements will be used to fill the array list.
     * @param offset the first element to use.
     * @param length the number of elements to use.
     */
    public ObjArrayList(final K a[], final int offset, final int length) {
        this(length);
        System.arraycopy(a, offset, this.a, 0, length);
        size = new AtomicInteger(length);
    }

    /**
     * Creates a new array list and fills it with the elements returned by an
     * iterator..
     *
     * @param i an iterator whose returned elements will fill the array list.
     */
    public ObjArrayList(final Iterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    /**
     * Creates a new array list and fills it with the elements returned by a
     * type-specific iterator..
     *
     * @param i a type-specific iterator whose returned elements will fill the
     * array list.
     */
    public ObjArrayList(final ObjectIterator<? extends K> i) {
        this();
        while (i.hasNext()) {
            this.add(i.next());
        }
    }

    /**
     * Returns the backing array of this list.
     *
     * <P>
     * If this array list was created by wrapping a given array, it is
     * guaranteed that the type of the returned array will be the same.
     * Otherwise, the returned array will be of type {@link Object
     * Object[]} (in spite of the declared return type).
     *
     * <P>
     * <strong>Warning</strong>: This behaviour may cause (unfathomable)
     * run-time errors if a method expects an array actually of type
     * <code>K[]</code>, but this methods returns an array of type
     * {@link Object Object[]}.
     *
     * @return the backing array.
     */
    public K[] elements() {
        return a;
    }

    /**
     * Wraps a given array into an array list of given size.
     *
     * <P>
     * Note it is guaranteed that the type of the array returned by
     * {@link #elements()} will be the same (see the comments in the class
     * documentation).
     *
     * @param a an array to wrap.
     * @param length the length of the resulting array list.
     * @return a new array list of the given size, wrapping the given array.
     */
    public static <K> ObjArrayList<K> wrap(final K a[], final int length) {
        if (length > a.length) {
            throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
        }
        final ObjArrayList<K> l = new ObjArrayList<>(a, false);
        l.size = new AtomicInteger(length);
        return l;
    }

    /**
     * Wraps a given array into an array list.
     *
     * <P>
     * Note it is guaranteed that the type of the array returned by
     * {@link #elements()} will be the same (see the comments in the class
     * documentation).
     *
     * @param a an array to wrap.
     * @return a new array list wrapping the given array.
     */
    public static <K> ObjArrayList<K> wrap(final K a[]) {
        return wrap(a, a.length);
    }

    /**
     * Ensures that this array list can contain the given number of entries
     * without resizing.
     *
     * @param capacity the new minimum capacity for this array list.
     */
    @SuppressWarnings("unchecked")
    public void ensureCapacity(final int capacity) {
        if (wrapped) {
            a = ObjectArrays.ensureCapacity(a, capacity, size.get());
        } else {
            if (capacity > a.length) {
                final Object t[] = new Object[capacity];
                System.arraycopy(a, 0, t, 0, size.get());
                a = (K[]) t;
            }
        }
    }

    /**
     * Grows this array list, ensuring that it can contain the given number of
     * entries without resizing, and in case enlarging it at least by a factor
     * of two.
     *
     * @param capacity the new minimum capacity for this array list.
     */
    @SuppressWarnings("unchecked")
    private void grow(final int capacity) {
        if (wrapped) {
            a = ObjectArrays.grow(a, capacity, size.get());
        } else {
            if (capacity > a.length) {
                final int newLength = (int) Math.max(Math.min(2L * a.length, it.unimi.dsi.fastutil.Arrays.MAX_ARRAY_SIZE), capacity);
                final Object t[] = new Object[newLength];
                System.arraycopy(a, 0, t, 0, size.get());
                a = (K[]) t;
            }
        }
    }

    @Override
    public void add(final int index, final K k) { //TODO check if used.
        System.out.println("used add(int, k)");
        ensureIndex(index);
        grow(size.get() + 1);
        if (index != size.get()) {
            System.arraycopy(a, index, a, index + 1, size.get() - index);
        }
        a[index] = k;
        size.incrementAndGet();
    }

    @Override
    public boolean add(final K k) {
        if (size.get() < a.length) {
            a[size.getAndIncrement()] = k;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null) {
                    a[i] = k;
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Add object k to the list as specified by {@link ObjArrayList#add(java.lang.Object)
     * }.
     *
     * @param k The object to add.
     * @param dummy
     * @return The index of where the object is added. -1 is no index was found.
     */
    public int add(final K k, Object dummy) {
        if (size.get() < a.length) {
            int index = size.getAndIncrement();
            a[index] = k;
            return index;
        } else {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null) {
                    a[i] = k;
                    return i;
                }
            }
            return -1;
        }
    }

    //TODO: Add comments
    public int add(final ObjectArrayList<K> k) {
        if (size.get() < a.length) {
            int r = k.size();
            int index = size.getAndAdd(r);
            for(int i = 0; i < r; i++) {
                a[index+i] = k.get(i);
            }
            return index;
        } else {
            int i = 0;
            ObjectListIterator<K> iter = k.iterator();
            while (iter.hasNext()) {
                for (; i < a.length; i++) {
                    if (a[i] == null) {
                        a[i] = iter.next();
                        break;
                    }
                }
                if(i == a.length) {
                    return -1;
                }
            }
            return -2;
        }
    }

    @Override
    public K get(final int index) {
        return a[index];
    }

    @Override
    public int indexOf(final Object k) {
        for (int i = 0; i < size.get(); i++) {
            if (((k) == null ? (a[i]) == null : (k).equals(a[i]))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object k) {
        for (int i = size.get(); i-- != 0;) {
            if (((k) == null ? (a[i]) == null : (k).equals(a[i]))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public K remove(final int index) {
        if (index >= size.get()) {
            throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
        }
        nullFlag = true;
        final K old = a[index];
        a[index] = null;
        /*if (index == size) {
         size--;
         }*/
        return old;
    }

    /**
     * Fix the null values in the actual array by moving the elements to cover
     * the null values.
     */
    public void fixNulls() {
        if (nullFlag) {
            nullFlag = false;
            for (int i = size.get() - 1; i >= 0; i--) {
                if (a[i] == null) {
                    System.arraycopy(a, (i + 1), a, i, size.get() - i - 1);
                    size.decrementAndGet();
                }
            }
        }
    }

    public boolean rem(final Object k) {
        int index = indexOf(k);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public boolean remove(final Object o) {
        return rem(o);
    }

    @Override
    public K set(final int index, final K k) {
        K old = a[index];
        a[index] = k;
        return old;
    }

    @Override
    public void clear() {
        Arrays.fill(a, 0, size.get(), null);
        size = new AtomicInteger();
    }

    @Override
    public int size() {
        return size.get();
    }

    @Override
    public void size(final int size) {
        if (size > a.length) {
            ensureCapacity(size);
        }
        if (size > this.size.get()) {
            Arrays.fill(a, this.size.get(), size, (null));
        } else {
            Arrays.fill(a, size, this.size.get(), (null));
        }
        this.size = new AtomicInteger(size);
    }

    @Override
    public boolean isEmpty() {
        return size.get() == 0;
    }

    /**
     * Trims this array list so that the capacity is equal to the size.
     *
     * @see java.util.ArrayList#trimToSize()
     */
    public void trim() {
        trim(0);
    }

    /**
     * Trims the backing array if it is too large.
     *
     * If the current array length is smaller than or equal to <code>n</code>,
     * this method does nothing. Otherwise, it trims the array length to the
     * maximum between <code>n</code> and {@link #size()}.
     *
     * <P>
     * This method is useful when reusing lists.
     * {@linkplain #clear() Clearing a list} leaves the array length untouched.
     * If you are reusing a list many times, you can call this method with a
     * typical size to avoid keeping around a very large array just because of a
     * few large transient lists.
     *
     * @param n the threshold for the trimming.
     */
    @SuppressWarnings("unchecked")
    public void trim(final int n) {
        // TODO: use Arrays.trim() and preserve type only if necessary
        if (n >= a.length || size.get() == a.length) {
            return;
        }
        final K t[] = (K[]) new Object[Math.max(n, size.get())];
        System.arraycopy(a, 0, t, 0, size.get());
        a = t;
    }

    /**
     * Copies element of this type-specific list into the given array using
     * optimized system calls.
     *
     * @param from the start index (inclusive).
     * @param a the destination array.
     * @param offset the offset into the destination array where to store the
     * first element copied.
     * @param length the number of elements to be copied.
     */
    @Override
    public void getElements(final int from, final Object[] a, final int offset, final int length) {
        ObjectArrays.ensureOffsetLength(a, offset, length);
        System.arraycopy(this.a, from, a, offset, length);
    }

    /**
     * Removes elements of this type-specific list using optimized system calls.
     *
     * @param from the start index (inclusive).
     * @param to the end index (exclusive).
     */
    @Override
    public void removeElements(final int from, final int to) {
        it.unimi.dsi.fastutil.Arrays.ensureFromTo(size.get(), from, to);
        System.arraycopy(a, to, a, from, size.get() - to);
        size.set(size.get() - (to - from));
        int i = to - from;
        while (i-- != 0) {
            a[size.get() + i] = null;
        }
    }

    /**
     * Adds elements to this type-specific list using optimized system calls.
     *
     * @param index the index at which to add elements.
     * @param a the array containing the elements.
     * @param offset the offset of the first element to add.
     * @param length the number of elements to add.
     */
    @Override
    public void addElements(final int index, final K a[], final int offset, final int length) {
        ensureIndex(index);
        ObjectArrays.ensureOffsetLength(a, offset, length);
        grow(size.get() + length);
        System.arraycopy(this.a, index, this.a, index + length, size.get() - index);
        System.arraycopy(a, offset, this.a, index, length);
        size.set(size.get() + length);
    }

    @Override
    public ObjectListIterator<K> listIterator(final int index) {
        ensureIndex(index);
        return new AbstractObjectListIterator<K>() {
            int pos = index, last = -1;

            @Override
            public boolean hasNext() {
                return pos < size.get();
            }

            @Override
            public boolean hasPrevious() {
                return pos > 0;
            }

            @Override
            public K next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return a[last = pos++];
            }

            @Override
            public K previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                return a[last = --pos];
            }

            @Override
            public int nextIndex() {
                return pos;
            }

            @Override
            public int previousIndex() {
                return pos - 1;
            }

            @Override
            public void add(K k) {
                if (last == -1) {
                    throw new IllegalStateException();
                }
                ObjArrayList.this.add(pos++, k);
                last = -1;
            }

            @Override
            public void set(K k) {
                if (last == -1) {
                    throw new IllegalStateException();
                }
                ObjArrayList.this.set(last, k);
            }

            @Override
            public void remove() {
                if (last == -1) {
                    throw new IllegalStateException();
                }
                ObjArrayList.this.remove(last);
                /* If the last operation was a next(), we are removing an element *before* us, and we must decrease pos correspondingly. */
                if (last < pos) {
                    pos--;
                }
                last = -1;
            }
        };
    }

    @Override
    public ObjArrayList<K> clone() {
        ObjArrayList<K> c = new ObjArrayList<>(size.get());
        System.arraycopy(a, 0, c.a, 0, size.get());
        c.size = size;
        return c;
    }

    private boolean valEquals(final K a, final K b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * Compares this type-specific array list to another one.
     *
     * <P>
     * This method exists only for sake of efficiency. The implementation
     * inherited from the abstract implementation would already work.
     *
     * @param l a type-specific array list.
     * @return true if the argument contains the same elements of this
     * type-specific array list.
     */
    public boolean equals(final ObjArrayList<K> l) {
        if (l == this) {
            return true;
        }
        int s = size();
        if (s != l.size()) {
            return false;
        }
        final K[] a1 = a;
        final K[] a2 = l.a;
        while (s-- != 0) {
            if (!valEquals(a1[s], a2[s])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this array list to another array list.
     *
     * <P>
     * This method exists only for sake of efficiency. The implementation
     * inherited from the abstract implementation would already work.
     *
     * @param l an array list.
     * @return a negative integer, zero, or a positive integer as this list is
     * lexicographically less than, equal to, or greater than the argument.
     */
    @SuppressWarnings("unchecked")
    public int compareTo(final ObjArrayList<? extends K> l) {
        final int s1 = size(), s2 = l.size();
        final K a1[] = a, a2[] = l.a;
        K e1, e2;
        int r, i;
        for (i = 0; i < s1 && i < s2; i++) {
            e1 = a1[i];
            e2 = a2[i];
            if ((r = (((Comparable<K>) (e1)).compareTo(e2))) != 0) {
                return r;
            }
        }
        return i < s2 ? -1 : (i < s1 ? 1 : 0);
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        s.defaultWriteObject();
        for (int i = 0; i < size.get(); i++) {
            s.writeObject(a[i]);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();
        a = (K[]) new Object[size.get()];
        for (int i = 0; i < size.get(); i++) {
            a[i] = (K) s.readObject();
        }
    }
}