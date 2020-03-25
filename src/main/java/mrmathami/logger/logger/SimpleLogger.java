package mrmathami.logger.logger;

import javax.annotation.Nonnull;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SimpleLogger {
	@Nonnull private final ThreadLocalQueue<String> queue;

	private SimpleLogger(@Nonnull ThreadLocalQueue<String> queue) {
		this.queue = queue;
	}

	private static PrintStream outputStreamFromPath(@Nonnull Path path) {
		try {
			return new PrintStream(new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.CREATE,
					StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Cannot create log file, using System.out as log stream.");
		return System.out;
	}

	public static SimpleLogger of(@Nonnull Path logPath) {
		final ThreadLocalQueue<String> queue = new ThreadLocalQueue<>(1024);
		final SimpleLogger simpleLogger = new SimpleLogger(queue);
		final Thread thread = new Thread(SimpleLogger.class.getSimpleName()) {
			@Override
			public void run() {
				final String[] dequeueBuffer = new String[256];
				final PrintStream stream = outputStreamFromPath(logPath);
				//noinspection SynchronizationOnLocalVariableOrMethodParameter
				synchronized (stream) {
					int flushCount = 0;
					for (boolean running = true; running || !queue.isEmpty(); ) {
						final int pollSize = queue.dequeue(dequeueBuffer, 0, 256);
						if (pollSize > 0) for (int i = 0; i < pollSize; i++) stream.println(dequeueBuffer[i]);
						if (pollSize <= 16) {
							if ((flushCount++ & 0x0F) != 0) {
								try {
									Thread.sleep(10);
								} catch (InterruptedException e) {
									running = false;
								}
							} else {
								stream.flush();
							}
						}
					}
				}
				if (stream != System.out) {
					stream.close();
				} else {
					stream.flush();
				}
			}
		};
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
		Runtime.getRuntime().addShutdownHook(new Thread(SimpleLogger.class.getSimpleName()) {
			@Override
			public void run() {
				try {
					thread.interrupt();
					thread.join();
				} catch (InterruptedException ignored) {
				}
			}
		});
		return simpleLogger;
	}

	public void log(@Nonnull String message) {
		try {
			while (!queue.enqueue(message)) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
