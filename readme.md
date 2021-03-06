# YuuKonfig

Configuration for idiots

# Introduction

**Objectives**
- Typesafe, threadsafe, timesafe config
- Reloadable
- Auto update
- Really expandable
- Never write a config parser again
- Lightweight (Compiled Jar is 50 KB, great for shading)

# Repos

```xml

<dependencies>
    <dependency>
        <groupId>com.superyuuki</groupId>
        <artifactId>yuukonfig</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>yuuki-releases</id>
        <name>releases</name>
        <url>https://repo.superyuuki.com/releases</url>
    </repository>
</repositories>

```

# Use

```java

public interface InternalConfig extends Section {

    @ConfKey("number")
    default Integer defaultInt() {
        return 5;
    }

    @ConfKey("bool")
    default Boolean defaultBool() {
        return true;
    }

    @ConfKey("nestedConfig")
    NestedConfig notDefaultConfig();


    interface NestedConfig extends Section {

        @ConfKey("someint")
        default Integer nestedInteger() {
            return 10;
        }

    }
}

```

Gives you

```yaml
nestedConfig:
  someint: 10
number: 5
bool: true
```

Configs are immutable (cannot change values) and threadsafe.

Configs support any object or interface you can think of, even user defined ones.

# Vs. DazzleConf

YuuKonfig is clearly inspired by DazzleConf. If you need a config library for a production environment, you are better off using DazzleConf.

| Feature | YuuKonfig | DazzleConf |
| --- | --- | --- |
| Production Philosophy | Intent on allowing for extensive configuration modification, allowing externally defined configs | Intent on fast, easy to write and heavily checked configs |
| Feature Set | Barebones built in features since extensibility is a priority | Contains many built in features such as nested sections, static serializers, range annotations, etc |
| Backing Libraries | YuuKonfig is tied to YML and uses EO-Yaml | DazzleConf is platform agnostic and uses a variety of libraries |
| Extensibility | Priority | Neutral |
| Maintainability | Neutral | Priority |
| Security and Safety | Neutral | Priority |
| Runtime-Defined configs | Priority | X |
| Serializers as a first class citizen | Priority | X |
| Simplicity | Incredible | Overcomplicated |
| Battle Tested | X | Very much so |

