package com.fit.logaspect;

import mrmathami.utils.logger.SimpleLogger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public aspect Interceptor {
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
	private static final Path LOG_PATH = Paths.get(System.getProperty("logaspect.path", System.getProperty("user.dir")))
			.resolve("logaspect_" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + ".log");
	private static final SimpleLogger LOGGER = SimpleLogger.of(LOG_PATH);

	pointcut setUpAll(): (execution(@org.junit.BeforeClass * *(..)) || execution(@org.junit.jupiter.api.BeforeAll * *(..))) ;
	pointcut setUpOnce(): (execution(@org.junit.Before * *(..)) || execution(@org.junit.jupiter.api.BeforeEach * *(..)));
	pointcut running(): (execution(@org.junit.Test * *(..)) || execution(@org.junit.jupiter.api.Test * *(..)));
	pointcut tearDownOnce(): (execution(@org.junit.After * *(..)) || execution(@org.junit.jupiter.api.AfterEach * *(..)));
	pointcut tearDownAll(): (execution(@org.junit.AfterClass * *(..)) || execution(@org.junit.jupiter.api.AfterAll * *(..)));
	pointcut traceMethods(): (execution(* *(..)) && !cflow(within(Interceptor))
			&& !within(org.junit.rules.TestRule+) && !within(org.junit.rules.MethodRule+)
			&& !setUpOnce() && !setUpAll() && !running() && !tearDownOnce() && !tearDownAll()
			&& !ruleSetup() && !ruleTearDown());

	pointcut staticInit(): (staticinitialization(*Test));

	@Pointcut("execution(* org.junit.rules.ExternalResource+.before(..))")
	public void ruleSetup() {
	}

	@Pointcut("execution(* org.junit.rules.ExternalResource+.after(..))")
	public void ruleTearDown() {
	}

	before(): traceMethods(){
		logDebug(thisJoinPointStaticPart, LogPattern.METHOD_START);
	}
	after(): traceMethods(){
		logDebug(thisJoinPointStaticPart, LogPattern.METHOD_FINISH);
	}
//	after() throwing (Throwable throwable): traceMethods(){
//		logExceptions(throwable, thisJoinPointStaticPart);
//	}

	before(): setUpAll(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEST_START);
		logDebug(thisJoinPointStaticPart, LogPattern.SETUP_START_ALL);
	}
	after(): setUpAll(){
		logDebug(thisJoinPointStaticPart, LogPattern.SETUP_FINISH_ALL);
	}

	before(): setUpOnce(){
		logDebug(thisJoinPointStaticPart, LogPattern.SETUP_START_ONE);
	}
	after(): setUpOnce(){
		logDebug(thisJoinPointStaticPart, LogPattern.SETUP_FINISH_ONE);
	}

	before(): running(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEST_METHOD_START);
	}
	after(): running(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEST_METHOD_FINISH);
	}

	before(): tearDownOnce(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_START_ONE);
	}
	after(): tearDownOnce(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_FINISH_ONE);
	}

	before(): tearDownAll(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_START_ALL);
	}
	after(): tearDownAll(){
		logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_FINISH_ALL);
		logDebug(thisJoinPointStaticPart, LogPattern.TEST_FINISH);
	}

	before(): ruleSetup(){
		logDebug(thisJoinPointStaticPart, LogPattern.RULE_SETUP_START);
	}
	after(): ruleSetup(){
		logDebug(thisJoinPointStaticPart, LogPattern.RULE_SETUP_FINISH);
	}

	before(): ruleTearDown(){
		logDebug(thisJoinPointStaticPart, LogPattern.RULE_TEARDOWN_START);
	}
	after(): ruleTearDown(){
		logDebug(thisJoinPointStaticPart, LogPattern.RULE_TEARDOWN_FINISH);
	}

//	private void logExceptions(@Nonnull Throwable throwable, @Nonnull JoinPoint.StaticPart staticPart) {
//		final MethodSignature signature = (MethodSignature) staticPart.getSignature();
//		final Method method = signature.getMethod();
//		LOGGER.log(String.join(LogPattern.DELIMITER, LogPattern.EXCEPTION_LOG_MODE, Long.toString(System.nanoTime()), method.toGenericString(), throwable.toString()));
//	}

	private void logDebug(@Nonnull JoinPoint.StaticPart staticPart, @Nonnull String message) {
		final MethodSignature signature = (MethodSignature) staticPart.getSignature();
		final Method method = signature.getMethod();
		final Thread thread = Thread.currentThread();
		LOGGER.log(String.join(LogPattern.DELIMITER, LogPattern.COMMAND_LOG_MODE, Long.toString(System.nanoTime()), message, method.toGenericString(), Long.toString(thread.getId())));
	}
}
