# Java Upgrade Plan: 17 → 21

**Project**: Smart Campus Operations Hub  
**Session ID**: 20260306233234  
**Date**: March 7, 2026  
**Current Java**: 17  
**Target Java**: 21  
**Current Spring Boot**: 3.2.4

---

## Technology Stack

| Technology | Current Version | Min Compatible Version | Incompatible Reason / EOL Warning |
|------------|----------------|------------------------|-----------------------------------|
| Java | 17 | 21 | Target upgrade |
| Spring Boot | 3.2.4 | 3.2.0 | - |
| Spring Framework | 6.1.x (from Spring Boot) | 6.1.0 | - |
| Maven | 3.x | 3.6.3 | - |
| spring-boot-starter-web | 3.2.4 | 3.2.0 | - |
| spring-boot-starter-data-mongodb | 3.2.4 | 3.2.0 | - |
| spring-boot-starter-security | 3.2.4 | 3.2.0 | - |
| spring-boot-starter-oauth2-client | 3.2.4 | 3.2.0 | - |
| spring-boot-starter-validation | 3.2.4 | 3.2.0 | - |
| JJWT (io.jsonwebtoken) | 0.12.5 | 0.11.5 | - |
| Lombok | 1.18.38 | 1.18.30 | - |
| spring-boot-devtools | 3.2.4 | 3.2.0 | - |
| spring-boot-starter-test | 3.2.4 | 3.2.0 | - |
| spring-security-test | 6.2.x (from Spring Boot) | 6.2.0 | - |

---

## Derived Upgrades

No derived upgrades required. All dependencies are compatible with Java 21 and Spring Boot 3.2.4.

**Analysis Summary**:
- Spring Boot 3.2.4 fully supports Java 21 (supports Java 17-21)
- All Spring Boot managed dependencies are compatible through the parent POM
- JJWT 0.12.5 supports Java 8+ and is compatible with Java 21
- Lombok 1.18.38 includes Java 21 support (required minimum: 1.18.30)
- All dependencies are current and maintained

**Required Changes**:
- Update `<java.version>` property from 17 to 21 in pom.xml
- Update maven-compiler-plugin `<source>` and `<target>` from 17 to 21

---

## Build Configuration Updates

### pom.xml
```xml
<!-- Update properties section -->
<properties>
    <java.version>21</java.version>  <!-- Changed from 17 -->
    <jjwt.version>0.12.5</jjwt.version>
</properties>

<!-- Update maven-compiler-plugin configuration -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>21</source>  <!-- Changed from 17 -->
        <target>21</target>  <!-- Changed from 17 -->
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.38</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

---

## Risk Assessment

**Overall Risk: LOW**

- ✅ All dependencies are compatible with Java 21
- ✅ Spring Boot 3.2.4 officially supports Java 21
- ✅ No deprecated APIs detected in dependency versions
- ✅ Build tools and plugins are current
- ⚠️ Recommendation: Full regression testing after upgrade

---

## Next Steps

1. ✅ Dependency analysis completed
2. ⏳ Update pom.xml with Java 21 configuration
3. ⏳ Run build and tests
4. ⏳ Verify application startup
5. ⏳ Run integration tests

---

## Available Tools

- **JDK 17** **<TO_BE_INSTALLED>** - Current Java version, required for baseline testing
- **JDK 21** **<TO_BE_INSTALLED>** - Target Java version for upgrade
- **Maven 3.x** - Build tool (already available)

---

## Upgrade Steps

### Step 1: Setup Environment
**Rationale**: Ensure both current (JDK 17) and target (JDK 21) Java versions are available for baseline comparison and upgrade testing.

**Changes**:
- Verify JDK 17 availability; install if not present
- Verify JDK 21 availability; install if not present
- Confirm Maven 3.x is available and properly configured

**Verification**:
- Command: `java -version` (with JAVA_HOME set to each JDK)
- JDK: 17 and 21
- Expected: Both JDKs report correct versions

---

### Step 2: Setup Baseline
**Rationale**: Establish a baseline by compiling and testing the project with the current Java 17 to ensure the starting point is stable and all tests pass.

**Changes**:
- Set JAVA_HOME to JDK 17
- Run full Maven build with tests
- Document compilation and test results as baseline

**Verification**:
- Command: `mvn clean test`
- JDK: 17
- Expected: Build SUCCESS, all tests pass (100%)

---

### Step 3: Update Java Version Configuration
**Rationale**: Update project configuration to target Java 21, enabling use of Java 21 features and runtime.

**Changes**:
- Update `<java.version>` property from 17 to 21 in pom.xml
- Update maven-compiler-plugin `<source>` from 17 to 21
- Update maven-compiler-plugin `<target>` from 17 to 21

**Verification**:
- Command: `mvn clean compile`
- JDK: 21
- Expected: Compilation SUCCESS with Java 21

---

### Step 4: Final Validation
**Rationale**: Verify that all upgrade goals are met, the application builds successfully, and all tests pass with Java 21.

**Changes**:
- Run complete Maven build with all tests
- Verify application can start successfully
- Confirm no deprecation warnings or errors

**Verification**:
- Command: `mvn clean verify`
- JDK: 21
- Expected: Build SUCCESS, 100% tests pass, no errors

---

## Key Challenges

No significant challenges identified. This is a straightforward upgrade.

**Reasoning**:
- Spring Boot 3.2.4 officially supports Java 21
- All dependencies are compatible with Java 21
- No deprecated APIs or breaking changes detected
- Project uses standard Spring Boot patterns with no exotic dependencies
