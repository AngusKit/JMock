# JMock

[English](README.md) | [中文](README_zh.md)

## 介绍

JMock 是基于 Java 语言实现的高性能数据生成和模拟的组件库，生成的数据相比随机数据更加接近业务数据特征。

## 特性

- 高性能数据生成，单线程每秒可以生成 200 万+ 用户信息（每个用户信息包括 10 个属性字段，总大小 200+ 字节）
- 灵活的数据规则以及国际化支持，数据更加接近业务数据特征。
- 支持注解和函数两种方式定义数据生成规则及控制数据生成。其中「注解方式」应用在类属性字段上，「函数方式」应用在脚本文件里（如：txt、json、yml
  等）。
- 对于批量数据支持 JDBC、内存、本地文件系统等持久化存储方式。
- 支持插件方式扩展 Mock 数据函数。

## 官网

`website/` 目录为 Next.js 静态站点（函数参考、快速开始、演练场等）。

- **英文**：站点根路径，如 `/`、`/docs/functions`
- **中文**：`/zh` 前缀，如 `/zh/docs/functions`
- 函数说明数据来自 `docs/JMockFunction-en.json` 与 `docs/JMockFunction-zh_CN.json`。

本地构建：

```bash
cd website && npm ci && npm run build
```

产物在 `website/out`。若部署到 GitHub Pages 的项目子路径，构建时需设置 `NEXT_PUBLIC_BASE_PATH=/仓库名`（见 `website/next.config.js` 与 `.github/workflows/deploy-github-pages.yml`）。

## 函数

### 表达式格式

| 类型       | 无参数形式      | 有参数形式                                   |
|----------|------------|-----------------------------------------|
| **注解方式** | `@注解类型名`   | `@注解类型名(参数1=值1, 参数2=值2)`                |
| **函数方式** | `@数据函数名()` | `@数据函数名(参数1, 参数2)` 或 `@数据函数名(参数1,,参数3)` |

### 语法规则

1. **核心符号**
    - `@` 标识表达式开始
    - `()` 包裹参数列表
    - `,` 分隔参数
    - `|` 分隔数组元素（仅用于数组型参数）
    - `\` 转义特殊字符（如 `@` `,` `|`）

2. **命名规范**
    - **注解类型名/数据函数名**：大驼峰命名法  
      *示例：`EmailValidator`, `RandomNumber`*
    - **参数名**：小驼峰命名法  
      *示例：`minValue`, `maxLength`*

3. **参数规则**
    - 无参调用：可省略括号（注解方式）或保留空括号（函数方式）
    - 含参调用：
        - 注解方式：`参数名=值` 键值对形式
        - 函数方式：直接按顺序传值
    - 省略参数：保留逗号分隔符  
      *示例：`@Generate(1,,3)` 表示第二个参数为空*

4. **特殊字符处理**  
   当参数值包含 `@` `,` `|` 时需转义：  
   *示例：`@String("tom\@domain.com\|nick\@domain.com")`*

## 使用示例

1. **引入 Maven 依赖**（版本号 `2.0.1` 与父工程一致）

```xml
<dependency>
  <groupId>cloud.xcan.jmock</groupId>
  <artifactId>xcan-jmock.core</artifactId>
  <version>2.0.1</version>
</dependency>

<!-- 可选：聚合全部内置插件 -->
<dependency>
  <groupId>cloud.xcan.jmock</groupId>
  <artifactId>xcan-jmock.all-plugin</artifactId>
  <version>2.0.1</version>
  <scope>runtime</scope>
</dependency>
```

2. **使用 `MockEngine` 渲染模板**（推荐入口）

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

若仅需对文本做简单占位替换，也可使用 core 模块中的 `DefaultMockTextReplacer`。

***输出结果示例：***

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

## 仓库

- **GitHub**：[https://github.com/xcancloud/JMock](https://github.com/xcancloud/JMock)
