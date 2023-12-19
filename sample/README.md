Coverage samples
================

### Cobertura

```
mvn -Pcobertura clean cobertura:cobertura
```


### JaCoCo (Tested Compliant to java 22)

```
mvn -Pjacoco clean verify jacoco:report
```

### Saga with Jasmine (Tested Compliant to java 22)

```
mvn -Psaga clean test saga:coverage
```

### Clover (Tested Compliant to java 22)

```
mvn -Pclover clean test clover:aggregate clover:clover
```
