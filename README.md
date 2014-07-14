Sloth
=====
**Current Version:** 0.1-alpha<br>
**Continuous Integration:** [![Build Status](https://travis-ci.org/leptonyu/sloth.svg?branch=master)](https://travis-ci.org/leptonyu/sloth)<br>
**License:** [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)<br>
**Author:** [Daniel Yu](http://icymint.me)<br>
**Java Version:** Java 8

### Use sloth with Maven
Add the following configuration to your pom.xml:

```xml
<repositories>
	<repository>
		<id>sloth-release</id>
		<name>Sloth Release</name>
		<url>https://raw.githubusercontent.com/leptonyu/sloth/releases</url>
	</repository>
</repositories>
```

Then add the following dependency to your pom.xml.

```xml
<dependency>
	<groupId>me.icymint</groupId>
	<artifactId>sloth</artifactId>
	<version>0.1-alpha</version>
</dependency>
```

### Use sloth with Gradle
Insert in your root project's 'build.gradle' under repositories:

```gradle
repositories {
    maven {
        url 'https://raw.githubusercontent.com/leptonyu/sloth/releases'
    }
}
```
and in your project folder in the 'build.gradle' under dependencies:

```gradle
dependencies {
    compile 'me.icymint:sloth:0.1-alpha'
}
```

### Download
[sloth-0.1-alpha Jar](https://raw.githubusercontent.com/leptonyu/sloth/releases/me/icymint/sloth/0.1-alpha/sloth-0.1-alpha.jar)<br>
[sloth-0.1-alpha Source](https://raw.githubusercontent.com/leptonyu/sloth/releases/releases/me/icymint/sloth/0.1-alpha/sloth-0.1-alpha-source.jar)<br>
[sloth-0.1-alpha Javadoc](https://raw.githubusercontent.com/leptonyu/sloth/releases/me/icymint/sloth/0.1-alpha/sloth-0.1-alpha-javadoc.jar)