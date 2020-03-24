package com.fit.logaspect;

public class LogPattern {
    public static final String LOG4J_PATTERN = "%-5p||%m%n";

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

    public static final String TESTMETHOD_START = "TESTMETHOD-START";

    public static final String TESTMETHOD_FINISH = "TESTMETHOD-FINISH";

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

}
