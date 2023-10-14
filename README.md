# CDI Interceptors

⚠️ **This repository is archived and not active any more** ⚠️

⚠️ **The author does not use CDI anymore** ⚠️

The goal of this project is to provide CDI interceptors that implement cross-cutting concerns.
Interceptors should be usable in different projects and scenarios and should not be available elsewhere (with the same features and quality).

The project is still *work in progress*.

## `@Logging`: A configurable `LoggingInterceptor`

[@Logging](src/main/java/com/github/sfleiter/cdi_interceptors/Logging.java) enables call based slf4j logging on any CDI bean.
For every call a single message is generated with the logger set to the name of the class. The message contains the method name, the parameters, the result object, the call duration and the exception if one got thrown.
For parameters and result objects null, primitive types, objects, collections and arrays of any kind are supported. Collections and arrays are transformed recursively. For objects their respective toString method is used.
Log level and maximum number of items per single array or collection can be configured.

## Maven
Releases can be found in the Maven central repository, for a list of the available versions see [here](http://search.maven.org/#search|ga|1|g%3A%22com.github.sfleiter.cdi-interceptors%22).
