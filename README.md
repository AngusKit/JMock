# JMock

[English](README.md) | [中文](README_zh.md)

## Introduction

JMock is a high-performance data generation and simulation library implemented in Java. It generates
data that more closely resembles real business data characteristics compared to random data
generation.

## Core Features

- **High-performance data generation**:  
  Generates 2M+ user records per second (single thread). Each record contains 10+ attributes
  totaling 200+ bytes.
- **Business-realistic data with i18n support**:  
  Produces data that accurately simulates real-world business scenarios with internationalization
  capabilities.
- **Dual definition approaches**:
    - *Annotation-based*: Applied to class fields
    - *Function-based*: Used in script files (txt, json, yml, etc.)
- **Multiple storage options**:  
  Supports JDBC, in-memory, and local filesystem persistence for batch data.
- **Extensible architecture**:  
  Plugin-based system for custom mock data functions.

## Website

The `website/` directory is a Next.js static site (function reference, getting started, playground).

- **English**: site root, e.g. `/`, `/docs/functions`
- **中文**: `/zh`, e.g. `/zh/docs/functions`
- Function descriptions are loaded from `docs/JMockFunction-en.json` and `docs/JMockFunction-zh_CN.json`.

To build locally:

```bash
cd website && npm ci && npm run build
```

Output is written to `website/out`. For GitHub Pages project sites, set `NEXT_PUBLIC_BASE_PATH=/YourRepoName` when building (see `website/next.config.js` and `.github/workflows/deploy-github-pages.yml`).

## Function Expressions

### Expression Formats

| Type           | Parameterless Form | Parameterized Form                                         |
|----------------|--------------------|------------------------------------------------------------|
| **Annotation** | `@Annotation`      | `@Annotation(param1=value1, param2=value2)`                |
| **Function**   | `@Function()`      | `@Function(param1, param2)` or `@Function(param1,,param3)` |

### Syntax Rules

1. **Core Symbols**
    - `@` - Marks expression start
    - `()` - Encloses parameters
    - `,` - Separates parameters
    - `|` - Delimits array elements (array parameters only)
    - `\` - Escapes special characters (`@`, `,`, `|`)

2. **Naming Conventions**
    - **Annotation/Function Names**: UpperCamelCase  
      *Example: `EmailValidator`, `RandomNumber`*
    - **Parameter Names**: lowerCamelCase  
      *Example: `minValue`, `maxLength`*

3. **Parameter Rules**
    - Parameterless calls: Omit parentheses (annotations) or use empty `()` (functions)
    - Parameterized calls:
        - Annotation: `param=value` key-value pairs
        - Function: Positional values
    - Omitted parameters: Preserve commas  
      *Example: `@Generate(1,,3)` indicates empty second parameter*

4. **Special Character Handling**  
   Escape `@`, `,`, `|` in parameter values:  
   *Example: `@String("tom\@domain.com\|nick\@domain.com")`*

## Usage Example

1. **Add Maven dependencies** (version `2.0.1` matches the parent POM)

```xml
<dependency>
  <groupId>cloud.xcan.jmock</groupId>
  <artifactId>xcan-jmock.core</artifactId>
  <version>2.0.1</version>
</dependency>

<!-- Optional: all built-in plugins -->
<dependency>
  <groupId>cloud.xcan.jmock</groupId>
  <artifactId>xcan-jmock.all-plugin</artifactId>
  <version>2.0.1</version>
  <scope>runtime</scope>
</dependency>
```

2. **Render a template** with `MockEngine` (recommended entry point)

```java
import cloud.xcan.jmock.core.engine.MockEngine;

String content = """
    {
      "name": "@Name()",
      "email": "@Email()",
      "phone": "@Mobile()",
      "address": "@Address()",
      "hobbies": [ "reading", "hiking",  "cooking" ]
    }""";

MockEngine engine = MockEngine.defaultEngine();
String result = engine.render(content);
System.out.println(result);
```

For simple text replacement without the full engine pipeline, you can use `DefaultMockTextReplacer` from the core module.

***Sample output:***

```json
{
  "name": "Durfee Jacob",
  "email": "9alJWYsUGJuJZtGuXT@yahoo.com.cn",
  "phone": "15292153757",
  "address": "ul. Akademika Pavlova, 12к3, Moskva",
  "hobbies": [
    "reading",
    "hiking",
    "cooking"
  ]
}
```

## Repository

- **GitHub**: [https://github.com/xcancloud/JMock](https://github.com/xcancloud/JMock)
