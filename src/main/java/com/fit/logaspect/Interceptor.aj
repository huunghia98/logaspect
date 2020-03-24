package com.fit.logaspect;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import sun.rmi.runtime.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public aspect Interceptor {
    private static final Logger LOGGER = Logger.getLogger(Interceptor.class.getName());
    private static long stackTrace = 0;
    private static ArrayList<WriterAppender> writerAppenders = getAllWriterAppender(LOGGER);
    private static boolean flushNow = false;

    pointcut setUpAll() : (execution(@org.junit.BeforeClass * *(..)) || execution(@org.junit.jupiter.api.BeforeAll * *(..))) ;
    pointcut setUpOnce(): (execution(@org.junit.Before * *(..)) || execution(@org.junit.jupiter.api.BeforeEach * *(..)));
    pointcut running(): (execution(@org.junit.Test * *(..)) || execution(@org.junit.jupiter.api.Test * *(..)));
    pointcut tearDownOnce(): (execution(@org.junit.After * *(..)) || execution(@org.junit.jupiter.api.AfterEach * *(..)));
    pointcut tearDownAll(): (execution(@org.junit.AfterClass * *(..)) || execution(@org.junit.jupiter.api.AfterAll * *(..)));

    @Pointcut("execution(* org.junit.rules.ExternalResource+.before(..))")
    public void ruleSetup(){}

    @Pointcut("execution(* org.junit.rules.ExternalResource+.after(..))")
    public void ruleTearDown(){}

    pointcut traceMethods() : (execution(* *(..)) && !cflow(within(Interceptor))
        && !within(org.junit.rules.TestRule+) && !within(org.junit.rules.MethodRule+)
        && !setUpOnce() && !setUpAll() && !running() && !tearDownOnce() && !tearDownAll()
        && !ruleSetup() && !ruleTearDown());

    pointcut staticInit(): (staticinitialization(*Test));

    before(): traceMethods(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
//        String line =""+ thisJoinPointStaticPart.getSourceLocation().getLine();
        if (stackTrace == 1)
            logDebug(method, LogPattern.UNKNOWN_BLOCK_START);
        logDebug(method, LogPattern.METHOD_START);
    }
    after(): traceMethods(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
//        String line =""+ thisJoinPointStaticPart.getSourceLocation().getLine();
        logDebug(method, LogPattern.METHOD_FINISH);
        if (stackTrace == 0)
            logDebug(method, LogPattern.UNKNOWN_BLOCK_FINISH);
    }
//    after() throwing (Throwable t): traceMethods(){
//        logExceptions(t,thisJoinPoint);
//    }


    before(): setUpAll(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TEST_START);
        logDebug(method, LogPattern.SETUP_START_ALL);
    }
    after(): setUpAll(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.SETUP_FINISH_ALL);
    }


    before(): setUpOnce(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.SETUP_START_ONE);
    }
    after(): setUpOnce(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.SETUP_FINISH_ONE);
    }

    before(): running(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TESTMETHOD_START);
    }
    after(): running(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TESTMETHOD_FINISH);
    }


    before(): tearDownOnce(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TEARDOWN_START_ONE);
    }
    after(): tearDownOnce(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TEARDOWN_FINISH_ONE);
    }


    before(): tearDownAll(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TEARDOWN_START_ALL);
    }
    after(): tearDownAll(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method, LogPattern.TEARDOWN_FINISH_ALL);
        logDebug(method, LogPattern.TEST_FINISH);
    }

    before(): ruleSetup(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method,LogPattern.RULE_SETUP_START);
    }
    after(): ruleSetup(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method,LogPattern.RULE_SETUP_FINISH);
    }

    before(): ruleTearDown(){
        stackTrace++;
        considerUnFlushImmediate();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method,LogPattern.RULE_TEARDOWN_START);
    }
    after(): ruleTearDown(){
        stackTrace--;
        considerFlushBuffer();
        Method method = ((MethodSignature) thisJoinPointStaticPart.getSignature()).getMethod();
        logDebug(method,LogPattern.RULE_TEARDOWN_FINISH);
    }

    private void logExceptions(Throwable t, final JoinPoint point) {
        final Method method = ((MethodSignature) point.getSignature()).getMethod();
        String mName = method.getName();
        String cName = method.getDeclaringClass().getSimpleName();
        Object[] params = point.getArgs();
        StringBuilder sb = new StringBuilder();
        sb.append("Exception caught for [");
        sb.append(cName);
        sb.append(".");
        sb.append(mName);
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];

            sb.append("  [Arg=").append(i);
            if (param != null) {
                String type = param.getClass().getSimpleName();

                sb.append(", ").append(type);

                // Handle Object Array (Policy Override)
                if (param instanceof Object[]) {
                    sb.append("=").append(Arrays.toString((Object[]) param));
                } else {
                    sb.append("=").append(param.toString());
                }
            } else {
                sb.append(", null");
            }
            sb.append("]  ");
        }
        LOGGER.log(org.apache.log4j.Level.toLevel(LogPattern.EXCEPTION_LOG_MODE),sb.toString());
    }
    private void logDebug(Method method, String message){
        Thread t = Thread.currentThread();

        String mName = method.getName();
        Class clazz = method.getDeclaringClass();
        ArrayList<String> ar = new ArrayList<>();
        for (Class cl : method.getParameterTypes()){
            ar.add(cl.getTypeName());
        }
        String params = String.join(LogPattern.PARAMS_DELIMITER,ar);
        LOGGER.log(org.apache.log4j.Level.toLevel(LogPattern.COMMAND_LOG_MODE), String.join(LogPattern.DELIMITER, message, mName, clazz.getName(), clazz.getSimpleName(),clazz.getCanonicalName(), clazz.getTypeName(),params,Long.toString(t.getId())));
    }
    private void logDebug(String message){
        Thread t = Thread.currentThread();
        LOGGER.log(org.apache.log4j.Level.toLevel(LogPattern.COMMAND_LOG_MODE),String.join(LogPattern.DELIMITER, message,Long.toString(t.getId())));
    }
    private static ArrayList<WriterAppender> getAllWriterAppender(Logger logger){
        ArrayList<WriterAppender> was = new ArrayList<>();
        for (Enumeration aps = logger.getAllAppenders(); aps.hasMoreElements(); ) {
            Appender ap = (Appender) aps.nextElement();
            if (ap instanceof WriterAppender && !((WriterAppender)ap).getImmediateFlush()){
                was.add((WriterAppender)ap);
            }
        }
        return was;
    }

    private void considerFlushBuffer(){
        if (stackTrace == 0){
            if (!flushNow) {
                for (WriterAppender ap:writerAppenders)
                    ap.setImmediateFlush(true);
                flushNow = !flushNow;
            }
        }
    }

    private void considerUnFlushImmediate(){
        if (flushNow) {
            for (WriterAppender ap:writerAppenders)
                ap.setImmediateFlush(false);
            flushNow = !flushNow;
        }
    }
}
