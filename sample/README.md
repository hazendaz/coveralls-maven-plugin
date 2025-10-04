Coverage samples
================

### Cobertura (Tested Compliant to java 8 at coveralls plugin 4.5.0)

note: We have flagged deprecated as it will not run with java 11 and minimum coveralls is java 11 past 4.5.0.

```
mvn -Pcobertura clean cobertura:cobertura
```


### JaCoCo (Tested Compliant to java 25)

```
mvn -Pjacoco clean verify jacoco:report
```

### Saga with Jasmine (Tested Compliant to java 25)

note: While it builds, it is not getting coverage as it shows no tests ran.

```
mvn -Psaga clean test saga:coverage
```

### Clover (Tested Compliant to java 25)

```
mvn -Pclover clean test clover:aggregate clover:clover
```
