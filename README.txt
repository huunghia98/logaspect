A tool to trace execution of test case which written by Junit 4 annotations syntax and basic Junit 5 annotations syntax.

It bases on AspectJ and works with AspectJ weaver tools like aspectj-maven-plugin, aspectjweaver, ...

Notes: With compile or post-compile time weaving, it will not trace execution of external libraries' methods.

Config log4j.properties:

log4j.rootLogger=TRACE
log4j.logger.com.fit.logaspect.Interceptor = TRACE, batchLogInterceptor
log4j.appender.batchLogInterceptor=org.apache.log4j.RollingFileAppender
log4j.appender.batchLogInterceptor.Append=true
log4j.appender.batchLogInterceptor.immediateFlush= false
log4j.appender.interceptor.bufferedIO=true
log4j.appender.interceptor.bufferSize=200KB
log4j.appender.batchLogInterceptor.File=./test.log
log4j.appender.batchLogInterceptor.layout=org.apache.log4j.PatternLayout
log4j.appender.batchLogInterceptor.layout.ConversionPattern=%-5p||%m%n