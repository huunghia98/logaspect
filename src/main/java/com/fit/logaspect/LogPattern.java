package com.fit.logaspect;

import mrmathami.utils.logger.SimpleLogger;
import org.aspectj.lang.JoinPoint;
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

	public static final String UNKNOWN_BLOCK_START = "UBS";
	public static final String UNKNOWN_BLOCK_FINISH = "UBF";

	// not separate Rule and ClassRule, combine to Rule only.
	// a test class in log start
	public static final String TEST_START = "TEST-START";
	public static final String SETUP_START_ALL = "SETUP-START-ALL";
	public static final String SETUP_FINISH_ALL = "SETUP-FINISH-ALL";

	public static final String RULE_SETUP_START = "RULE_SETUP_START";
	public static final String RULE_SETUP_FINISH = "RULE_SETUP_FINISH";

	public static final String SETUP_START_ONE = "SETUP-START-ONE";
	public static final String SETUP_FINISH_ONE = "SETUP-FINISH-ONE";

	public static final String TEST_METHOD_START = "TEST-METHOD-START";

	public static final String TEST_METHOD_FINISH = "TEST-METHOD-FINISH";

	public static final String TEARDOWN_START_ONE = "TEARDOWN-START-ONE";
	public static final String TEARDOWN_FINISH_ONE = "TEARDOWN-FINISH-ONE";

	public static final String RULE_TEARDOWN_START = "RULE_TEARDOWN_START";
	public static final String RULE_TEARDOWN_FINISH = "RULE_TEARDOWN_FINISH ";

	public static final String TEARDOWN_START_ALL = "TEARDOWN-START-ALL";
	public static final String TEARDOWN_FINISH_ALL = "TEARDOWN-FINISH-ALL";
	public static final String TEST_FINISH = "TEST-FINISH";
	// a test class in log end

	public static final String COMMAND_LOG_MODE = "DEBUG";
	public static final String EXCEPTION_LOG_MODE = "ERROR";
	public static final String DELIMITER = "|";
	public static final String MODE_DELIMITER = "||";
	public static final String PARAMS_DELIMITER = "-";
	public static final String UNIT_TEST = "UNIT";
	public static final String INTEGRATION_TEST = "INTEGRATION";
	public static final String FUNCTIONAL_TEST = "FUNCTIONAL";


	public static final String SCOPE_ALL = "ALL";
	public static final String SCOPE_ONE = "ONE";


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
		final MethodSignature signature = (MethodSignature) staticPart.getSignature();
		final Method method = signature.getMethod();
		final Thread thread = Thread.currentThread();
		LOGGER.log(String.join(LogPattern.DELIMITER, LogPattern.COMMAND_LOG_MODE, Long.toString(System.nanoTime()), Long.toString(thread.getId()), message, method.toGenericString()));
	}
}
