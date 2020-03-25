package com.fit.logaspect;

import mrmathami.utils.logger.SimpleLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogPattern {
	public static final String METHOD_START = "METHOD-START";
	public static final String METHOD_FINISH = "METHOD-FINISH";

	// not separate Rule and ClassRule, combine to Rule only.
	// a test class in log start
	public static final String TEST_START = "TEST-START";
	public static final String TEST_FINISH = "TEST-FINISH";

	public static final String SETUP_START_ONE = "SETUP-START-ONE";
	public static final String SETUP_FINISH_ONE = "SETUP-FINISH-ONE";

	public static final String SETUP_START_ALL = "SETUP-START-ALL";
	public static final String SETUP_FINISH_ALL = "SETUP-FINISH-ALL";

	public static final String TEST_METHOD_START = "TEST-METHOD-START";
	public static final String TEST_METHOD_FINISH = "TEST-METHOD-FINISH";

	public static final String TEARDOWN_START_ONE = "TEARDOWN-START-ONE";
	public static final String TEARDOWN_FINISH_ONE = "TEARDOWN-FINISH-ONE";

	public static final String TEARDOWN_START_ALL = "TEARDOWN-ALL-START";
	public static final String TEARDOWN_FINISH_ALL = "TEARDOWN-ALL-FINISH";

	public static final String RULE_SETUP_START = "RULE-SETUP-START";
	public static final String RULE_SETUP_FINISH = "RULE-SETUP-FINISH";

	public static final String RULE_TEARDOWN_START = "RULE-TEARDOWN-START";
	public static final String RULE_TEARDOWN_FINISH = "RULE-TEARDOWN-FINISH ";

	public static final String STATIC_INIT_START = "STATIC-INIT-START";
	public static final String STATIC_INIT_FINISH = "STATIC-INIT-FINISH ";

	// a test class in log end

	public static final String COMMAND_LOG_MODE = "DEBUG";
	public static final String EXCEPTION_LOG_MODE = "ERROR";
	public static final String DELIMITER = "|";


	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private static final Path LOG_PATH = Paths.get(System.getProperty("logaspect.path", System.getProperty("user.dir")))
			.resolve("logaspect_" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ".log");
	private static final SimpleLogger LOGGER = SimpleLogger.of(LOG_PATH);

//	static void logExceptions(@Nonnull Throwable throwable, @Nonnull JoinPoint.StaticPart staticPart) {
//		final MethodSignature signature = (MethodSignature) staticPart.getSignature();
//		final Method method = signature.getMethod();
//		LOGGER.log(String.join(LogPattern.DELIMITER, LogPattern.EXCEPTION_LOG_MODE, Long.toString(System.nanoTime()), method.toGenericString(), throwable.toString()));
//	}

	static void logDebug(@Nonnull JoinPoint.StaticPart staticPart, @Nonnull String message) {
		final Thread thread = Thread.currentThread();
		final Signature signature = staticPart.getSignature();
		LOGGER.log(String.join(LogPattern.DELIMITER, LogPattern.COMMAND_LOG_MODE, Long.toString(System.nanoTime()), Long.toString(thread.getId()), message, signature.toLongString()));
	}
}
