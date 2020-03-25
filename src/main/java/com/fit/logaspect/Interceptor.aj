package com.fit.logaspect;

public aspect Interceptor {
	pointcut setUpAll(): execution(@org.junit.BeforeClass * *(..)) || execution(@org.junit.jupiter.api.BeforeAll * *(..));
	pointcut setUpOnce(): execution(@org.junit.Before * *(..)) || execution(@org.junit.jupiter.api.BeforeEach * *(..));
	pointcut running(): execution(@org.junit.Test * *(..)) || execution(@org.junit.jupiter.api.Test * *(..));
	pointcut tearDownOnce(): execution(@org.junit.After * *(..)) || execution(@org.junit.jupiter.api.AfterEach * *(..));
	pointcut tearDownAll(): execution(@org.junit.AfterClass * *(..)) || execution(@org.junit.jupiter.api.AfterAll * *(..));
	pointcut traceMethods(): execution(* *(..));
//	 && !cflow(within(Interceptor))
//			&& !within(org.junit.rules.TestRule+) && !within(org.junit.rules.MethodRule+)
//			&& !setUpOnce() && !setUpAll() && !running() && !tearDownOnce() && !tearDownAll()
//			&& !ruleSetup() && !ruleTearDown();

	// Look red? Don't worry, IntelliJ f*cked up hard here. AJC compile it just fine.
	pointcut ruleSetup(): execution(* org.junit.rules.ExternalResource+.before(..));
	pointcut ruleTearDown(): execution(* org.junit.rules.ExternalResource+.after(..));
	pointcut staticInit(): staticinitialization(*);


	before(): traceMethods() {
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.METHOD_START);
	}
	after(): traceMethods(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.METHOD_FINISH);
	}
	//	after() throwing (Throwable throwable): traceMethods(){
//		logExceptions(throwable, thisJoinPointStaticPart);
//	}

	before(): setUpAll(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEST_START);
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.SETUP_START_ALL);
	}
	after(): setUpAll(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.SETUP_FINISH_ALL);
	}

	before(): setUpOnce(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.SETUP_START_ONE);
	}
	after(): setUpOnce(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.SETUP_FINISH_ONE);
	}

	before(): running(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEST_METHOD_START);
	}
	after(): running(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEST_METHOD_FINISH);
	}

	before(): tearDownOnce(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_START_ONE);
	}
	after(): tearDownOnce(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_FINISH_ONE);
	}

	before(): tearDownAll(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_START_ALL);
	}
	after(): tearDownAll(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEARDOWN_FINISH_ALL);
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.TEST_FINISH);
	}

	before(): ruleSetup(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.RULE_SETUP_START);
	}
	after(): ruleSetup(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.RULE_SETUP_FINISH);
	}

	before(): ruleTearDown(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.RULE_TEARDOWN_START);
	}
	after(): ruleTearDown(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.RULE_TEARDOWN_FINISH);
	}

	before(): staticInit(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.STATIC_INIT_START);
	}
	after(): staticInit(){
		LogPattern.logDebug(thisJoinPointStaticPart, LogPattern.STATIC_INIT_FINISH);
	}
}
