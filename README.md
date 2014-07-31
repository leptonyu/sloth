Sloth
=====
**Current Version:** 0.3<br>
**Continuous Integration:** [![Build Status](https://travis-ci.org/leptonyu/sloth.svg?branch=master)](https://travis-ci.org/leptonyu/sloth)<br>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)<br>
**Author:** [Daniel Yu](http://icymint.me)<br>
**Java Version:** Java 8

### Use sloth with Maven
Add the following dependency to your pom.xml.

```xml
<dependency>
	<groupId>me.icymint</groupId>
	<artifactId>sloth-core</artifactId>
	<version>0.3</version>
</dependency>
```


### Deferred utility
The Deferred object is inspired by the Golang keyword **defer**. 
It collects operations need to execute and use Deferred#close() to execute all of them.

```java
	Deferred df=Deferred.create();
	try{
		// Here to add the DeferredOpration to df.
		// For example:
		ExecutorService pool=Executors.newSingleThreadPool();
		df.defer(pool::shutdown);
		// next, use the pool.
		pool.submit(()->System.out.println("Hello"));
	}finally{
		df.close();
	}
```
try-with usage:
```java
	try(Deferred df=Deffered.create()){
		// Here to add the DeferredOpration to df.
		// For example:
		ExecutorService pool=Executors.newSingleThreadPool();
		df.defer(pool::shutdown);
		// next, use the pool.
		pool.submit(()->System.out.println("Hello"));
	}
```