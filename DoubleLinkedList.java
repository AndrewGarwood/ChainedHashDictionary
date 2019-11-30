package datastructures.lists;

import datastructures.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    /*
    Warning:
    You may not rename these fields or change their types.
    We will be inspecting these in our secret tests.
    You also may not add any additional fields.

    Note: The fields below intentionally omit the "private" keyword. By leaving off a specific
    access modifier like "public" or "private" they become package-private, which means anything in
    the same package can access them. Since our tests are in the same package, they will be able
    to test these properties directly.
     */
    Node<T> front;
    Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    /**
     * Adds the given item at the end of the list
     *
     * @param item: the element to be added to the list
     */
    @Override
    public void add(T item) {
        if (back != null) {
            back.next = new Node(back, item, null);
            back = back.next;
        } else { // back == null i.e. empty list
            Node<T> input = new Node(null, item, null);
            front = input;
            back = input;
        }
        size++;
    }

    /**
     * Removes the item from the end of the list
     *
     * @return is the item we removed from the end.
     * @throws EmptyContainerException: throws new EmptyContainerException if list is empty
     */
    @Override
    public T remove() throws EmptyContainerException {
        if (size != 0) {
            Node<T> result = back;
            if (size > 1) {
                back = back.prev;
                back.next = null;
            } else { // size == 1
                front = null;
                back = null;
            }
            size--;
            return result.data;
        } else { // size == 0
            throw new EmptyContainerException();
        }
    }

    /**
     * Returns the item located at the given index.
     *
     * @param index: the index from which one wants to get the element from
     * @return is the element located at the given index
     */
    @Override
    public T get(int index) {
        return getNodeAtIndex(index).data;
    }

    /**
     * Overwrites the item located at the given index with the new item. Returns the item replaced.
     *
     * @param index: the index where the user wishes to overwrite the data to the given element
     * @param item:  the element the user wishes to input as the new data at the given index
     * @return is the item that is replaced
     * @throws IndexOutOfBoundsException: throws new IndexOutOfBoundsException if the given index is less than zero
     *                                    or if index is greater than or equal to the size of the list
     */
    @Override
    public T set(int index, T item) throws EmptyContainerException {
        Node<T> previousNode = getNodeAtIndex(index);
        Node<T> newNode = new Node(previousNode.prev, item, previousNode.next);
        if (size > 1) {
            // newNode = new Node(previousNode.prev, item, previousNode.next);
            if (index == size - 1) {
                previousNode.prev.next = newNode;
                back = newNode;
            } else if (index != 0) {
                previousNode.prev.next = newNode;
                previousNode.next.prev = newNode;
            } else { // index == 0
                front = newNode;
                newNode.next.prev = newNode;
            }
        } else if (size == 1) { // size == 1
            // newNode = new Node(null, item, null);
            previousNode = front;
            front = newNode;
            back = newNode;
        }
        return previousNode.data;

    }

    /**
     * Gets a reference to the node at the given index.
     *
     * @param index: the index one wants to get to in the list
     * @return is the node at the given index
     * @throws IndexOutOfBoundsException: throws new IndexOutOfBoundsException if the given index is less than zero
     *                                    or if index is greater than or equal to the size of the list
     */
    private Node<T> getNodeAtIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<T> current;
        if (index < size / 2) {
            current = front;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else { // index >= size / 2
            current = back;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }

        }
        return current;
    }

    /**
     * Inserts the given item at the given index. If there already exists an element
     * at that index, shift over that element and any subsequent elements one index
     * higher.
     *
     * @param index: the given index the user wants to insert an element at
     * @param item: the item the user wishes to insert at the given index
     * @throws IndexOutOfBoundsException: throws new IndexOutOfBoundsException if the given index is less than zero
     *                                    or if the given index is greater than or equal to the size + 1
     */
    @Override
    public void insert(int index, T item) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size() + 1) {
            throw new IndexOutOfBoundsException();
        }

        if (index == this.size()) {
            this.add(item);
        } else {
            Node<T> previousNode = getNodeAtIndex(index);
            Node<T> newNode = new Node(previousNode.prev, item, previousNode);

            if (index == 0) {
                front = newNode;
            } else {
                previousNode.prev.next = newNode;
            }
            previousNode.prev = newNode;
            size++;
        }
    }

    /**
     * Deletes the item at the given index. If there are any elements located at a higher
     * index, shift them all down by one. Returns the item deleted.
     *
     *
     * @param index: the index the user wishes to delete the element from
     * @return is the elemnent that the user deleted
     * @throws IndexOutOfBoundsException: throws new IndexOutOfBoundsException if the given index is less than zero
     *                                    or if index is greater than or equal to the size of the list
     */
    @Override
    public T delete(int index) throws EmptyContainerException {
        Node<T> targetNode = getNodeAtIndex(index);
        if (size > 1) {
            if (index == 0) { // if delete from front
                targetNode.next.prev = targetNode.prev;
                front = targetNode.next;
            } else if (index == size - 1) { // if delete from end
                targetNode.prev.next = targetNode.next;
                back = targetNode.prev;
            } else { // if delete from middle
                targetNode.next.prev = targetNode.prev;
                targetNode.prev.next = targetNode.next;
            }
            targetNode.next = null;
            targetNode.prev = null;
        } else if (size == 1) { // size == 1
            targetNode = front;
            front = null;
            back = null;
        }
        size--;
        return targetNode.data;
    }

    /**
     * Returns the index corresponding to the first occurrence of the given item
     * in the list.
     *
     * If the item does not exist in the list, return -1.
     *
     * @param item: the element the user wants to know the index of
     * @return is the index of the given element; -1 if not found
     */
    @Override
    public int indexOf(T item) {
        Node<T> current = front;
        int index = 0;
        while (current != null && !Objects.equals(current.data, item)) {
            current = current.next;
            index++;
        }
        if (current == null) {
            return -1;
        } else {
            return index;
        }
    }

    /**
     * Returns the number of elements in the container.
     *
     * @return is the current size of the list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns `true` if this container contains the given element, and `false` otherwise.
     *
     * @param other: the item the user wants determine whether or not it is in the list
     * @return is whether or not the list contains the given item
     */
    @Override
    public boolean contains(T other) {
        return (this.indexOf(other) != -1);
    }

    /**
     * Returns a bracketed list with elements of the list separated by commas
     *
     * @return is the string representation of the list as described above
     */
    @Override
    public String toString() {
        // return super.toString();

        /*
        After you've implemented the iterator, comment out the line above and uncomment the line
        below to get a better string representation for objects in assertion errors and in the
        debugger.
        */

        return IList.toString(this);
    }

    /**
     * Returns an iterator over the contents of this list.
     *
     * @return is an iterator of the list
     */
    @Override
    public Iterator<T> iterator() {
        /*
        Note: we have provided a part of the implementation of an iterator for you. You should
        complete the methods stubs in the DoubleLinkedListIterator inner class at the bottom of
        this file. You do not need to change this method.
        */
        return new DoubleLinkedListIterator<>(this.front);
    }

    /**
     * Node representation of data
     *
     * @param <E> the type of the element of the node
     */
    static class Node<E> {
        // You may not change the fields in this class or add any new fields.
        final E data;
        Node<E> prev;
        Node<E> next;

        Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        Node(E data) {
            this(null, data, null);
        }


        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> next;

        public DoubleLinkedListIterator(Node<T> front) {
            // You do not need to make any changes to this constructor.
            this.next = front;
        }

        /**
         * Returns `true` if the iterator still has elements to look at;
         * returns `false` otherwise.
         */
        public boolean hasNext() {
            return next != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T result = next.data;
            next = next.next;
            return result;
        }
    }
}
