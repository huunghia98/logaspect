package mrmathami.utils.logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadLocalQueue<E> {
	@Nonnull private final ThreadLocal<Queue<E>> threadLocal = ThreadLocal.withInitial(this::createQueue);
	@Nonnull private final AtomicInteger tailIndex = new AtomicInteger();

	private final int limit;
	@Nullable private Queue<E> current = null;

	private int headIndex = 0;
	private int cleanCount = 0;

	public ThreadLocalQueue(int limit) {
		this.limit = limit;
	}

	@Nonnull
	private synchronized Queue<E> createQueue() {
		final Thread currentThread = Thread.currentThread();
		final Queue<E> current = this.current;
		if (current == null) {
			return this.current = new Queue<>(limit, currentThread);
		} else {
			final Queue<E> previous = current.prev;
			final Queue<E> node = new Queue<>(limit, currentThread, current, previous);
			return previous.next = current.prev = node;
		}
	}

	private static <E> boolean cleanUp(@Nonnull Queue<E> current, @Nonnull Queue<E> next, @Nonnull Thread thread) {
		if (thread.isAlive()) return false;
		assert current != next;
		final Queue<E> prev = current.prev;
		next.prev = prev;
		prev.next = next;
		return true;
	}

	public boolean enqueue(@Nonnull E value) {
		return threadLocal.get().enqueue(tailIndex, value);
	}

	@Nullable
	public synchronized E dequeue() {
		Queue<E> current = this.current;
		if (current == null) return null;

		final int tail = tailIndex.get();
		final int head = headIndex;
		if (head == tail) return null; // empty

		while (true) {
			final E value = current.dequeue(head);
			if (value != null) {
				this.headIndex += 1;
				this.current = current;
				return value;
			}

			// clean up
			final Queue<E> next = current.next;
			final Thread thread;
			if ((thread = current.get()) == null || ++cleanCount == 257 && cleanUp(current, next, thread)) {
				this.cleanCount = 0;
			}
			current = next;
		}
	}

	public synchronized int dequeue(@Nonnull E[] values, int offset, int length) {
//		assert offset >= 0 && length > 0 && offset + length <= values.length;
		if (offset < 0) throw new IllegalArgumentException("Offset cannot be negative.");
		if (length <= 0) throw new IllegalArgumentException("Length cannot be zero or negative.");
		if (offset + length > values.length) throw new IndexOutOfBoundsException("Output array index out of bound.");

		Queue<E> current = this.current;
		if (current == null) return 0;

		final int tail = tailIndex.get();
		final int head = headIndex;
		if (head == tail) return 0; // empty

		final int size = Math.min(tail - head, length);
		int count = 0;
		while (true) {
			final int dequeue = current.dequeue(values, offset, head, size);
			if (dequeue > 0) {
				count += dequeue;
				assert count <= dequeue;
				current = current.next;
				if (count == size) {
					this.headIndex += size;
					this.current = current;
					return size;
				}
			} else {
				// clean up
				final Queue<E> next = current.next;
				final Thread thread;
				if ((thread = current.get()) == null || ++cleanCount == 257 && cleanUp(current, next, thread)) {
					this.cleanCount = 0;
				}
				current = next;
			}
		}
	}

	public synchronized boolean isEmpty() {
		return current == null || headIndex == tailIndex.get();
	}

	private static final class Queue<E> extends WeakReference<Thread> {
		private final int limit;
		@Nonnull private Queue<E> next;
		@Nonnull private Queue<E> prev;
		@Nonnull private Node<E> head;
		@Nonnull private Node<E> tail;

		private Queue(int limit, @Nonnull Thread thread) {
			super(thread);
			this.limit = limit;
			this.next = this.prev = this;
			this.head = this.tail = new Node<>(0, 0, null); // initial blank node
		}

		private Queue(int limit, @Nonnull Thread thread, @Nonnull Queue<E> next, @Nonnull Queue<E> prev) {
			super(thread);
			this.limit = limit;
			this.next = next;
			this.prev = prev;
			this.head = this.tail = new Node<>(0, 0, null); // initial blank node
		}

		private boolean enqueue(@Nonnull AtomicInteger tailIndex, @Nonnull E value) {
			if (tail.count - head.count >= limit) return false;
			this.tail = tail.next = new Node<>(tailIndex.getAndIncrement(), tail.count + 1, value);
			return true;
		}

		@Nullable
		private E dequeue(int headIndex) {
			final Node<E> next;
			if ((next = head.next) == null || next.index != headIndex) return null;
			this.head = next;
			return next.value;
		}

		private int dequeue(@Nonnull E[] values, int offset, int headIndex, int size) {
			int count = 0;
			Node<E> head = this.head, next;
			while ((next = head.next) != null) {
				final int index = next.index - headIndex;
				if (index >= size) break;
				values[offset + index] = next.value;
				head = next;
				count += 1;
			}
			this.head = head;
			return count;
		}
	}

	private static final class Node<E> {
		private final int index;
		private final int count;
		@Nullable private final E value;
		@Nullable private Node<E> next;

		private Node(int index, int count, @Nullable E value) {
			this.index = index;
			this.count = count;
			this.value = value;
		}
	}
}
