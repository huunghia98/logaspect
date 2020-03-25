package mrmathami.logger.logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class ThreadLocalQueue<E> {
	private final int limit;
	@Nullable private Queue<E> current = null;
	@Nonnull private final ThreadLocal<Queue<E>> threadLocal = ThreadLocal.withInitial(this::createQueue);
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

	public boolean enqueue(@Nonnull E value) {
		return threadLocal.get().enqueue(value);
	}

	@Nullable
	public synchronized E dequeue() {
		Queue<E> current = this.current;
		if (current == null) return null;
		final Queue<E> mask = current;
		do {
			final E value = current.dequeue();
			if (value != null) {
				this.current = current;
				return value;
			}
		} while ((current = current.next) != mask);
		return null;
	}

	public synchronized int dequeue(@Nonnull E[] values, int offset, int length) {
//		assert offset >= 0 && length > 0 && offset + length <= values.length;
		if (offset < 0) throw new IllegalArgumentException("Offset cannot be negative.");
		if (length <= 0) throw new IllegalArgumentException("Length cannot be zero or negative.");
		if (offset + length > values.length) throw new IndexOutOfBoundsException("Output array index out of bound.");

		Queue<E> current = this.current;
		if (current == null) return 0;
		int emptyOffset = offset, emptyLen = length;
		Queue<E> marker = current;
		do {
			final Queue<E> next = current.next;
			final int pollSize = current.dequeue(values, emptyOffset, emptyLen);
			if (pollSize > 0) {
				emptyOffset += pollSize;
				emptyLen -= pollSize;
				if (emptyLen == 0) break;
			} else if (current == marker) {
				final Thread thread = current.get();
				if (thread == null || (cleanCount++ & 0xFF) == 0 && !thread.isAlive()) {
					if (current != next) {
						final Queue<E> prev = current.prev;
						next.prev = prev;
						prev.next = next;
						marker = next;
					} else {
						current = null;
						break;
					}
				}
			}
			current = next;
		} while (current != marker);
		this.current = current;
		return length - emptyLen;
	}

	public synchronized boolean isEmpty() {
		Queue<E> current = this.current;
		if (current == null) return true;
		final Queue<E> mask = current;
		do {
			if (!current.isEmpty()) {
				this.current = current;
				return false;
			}
		} while ((current = current.next) != mask);
		return true;
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
			this.head = this.tail = new Node<>(0, null); // initial blank node
		}

		private Queue(int limit, @Nonnull Thread thread, @Nonnull Queue<E> next, @Nonnull Queue<E> prev) {
			super(thread);
			this.limit = limit;
			this.next = next;
			this.prev = prev;
			this.head = this.tail = new Node<>(0, null); // initial blank node
		}

		private boolean enqueue(@Nonnull E value) {
			if (tail.count - head.count >= limit) return false;
			this.tail = tail.next = new Node<>(tail.count + 1, value);
			return true;
		}

		@Nullable
		private E dequeue() {
			if (head.next == null) return null;
			this.head = head.next;
			return head.value;
		}

		private int dequeue(@Nonnull E[] values, final int offset, final int length) {
//		assert offset >= 0 && length > 0 && offset + length <= values.length;
			int index = offset, limit = offset + length;
			while (index < limit && head.next != null) {
				this.head = head.next;
				values[index++] = head.value;
			}
			return index - offset;
		}

		private boolean isEmpty() {
			return head.next == null;
		}
	}

	private static final class Node<E> {
		private final int count;
		@Nullable private final E value;
		@Nullable private Node<E> next;

		private Node(int count, @Nullable E value) {
			this.count = count;
			this.value = value;
		}
	}
}











