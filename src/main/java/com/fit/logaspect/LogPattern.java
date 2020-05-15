package com.fit.logaspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LogPattern {
    public static final String METHOD_START = "METHOD-START";
    public static final String METHOD_FINISH = "METHOD-FINISH";

    // not separate Rule and ClassRule, combine to Rule only.
    // a test class in log start

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
    public static final String PARAMS_DELIMITER = "-";
    public static final String MANUAL_TEST_MODE = "TRACE";

}