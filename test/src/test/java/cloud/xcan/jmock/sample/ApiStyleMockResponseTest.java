package cloud.xcan.jmock.sample;

import cloud.xcan.jmock.core.engine.MockEngine;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 模拟接口返回体：顶层 JSON + {@code users} 用 {@code @Repeat} 生成数组。
 * <p>
 * 你提供的示意里用了分号 {@code ;}，标准 JSON 对象属性之间应使用逗号 {@code ,}，下面模板均按合法 JSON 书写。
 * <p>
 * {@code @Repeat} 语法：{@code @Repeat(字面量, 次数)}；仅 {@code @Repeat(用户信息)} 时等价于重复 1 次。
 */
public class ApiStyleMockResponseTest {

  private MockEngine engine;
  private final ObjectMapper mapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    engine = MockEngine.defaultEngine();
  }

  /**
   * 对应形态（逻辑上等价）：
   * <pre>
   * {
   *   "name": "xxx函数",
   *   "description": "xxx函数",
   *   "users": @Repeat(用户信息, 5)
   * }
   * </pre>
   * 一次 {@link MockEngine#render(String)} 拼出整段 JSON；{@code users} 为字符串数组。
   */
  @Test
  void apiResponse_repeatLiteralUserInfo_keepsSurroundingJson() throws Exception {
    final int userRepeat = 5;
    String template = "{\"name\":\"xxx函数\",\"description\":\"xxx函数\",\"users\":@Repeat(用户信息,"
        + userRepeat + ")}";

    String raw = engine.render(template);

    JsonNode tree = mapper.readTree(raw);

    Assertions.assertEquals("xxx函数", tree.get("name").asText());
    Assertions.assertEquals("xxx函数", tree.get("description").asText());
    Assertions.assertTrue(tree.get("users").isArray());
    Assertions.assertEquals(userRepeat, tree.get("users").size());
    for (JsonNode u : tree.get("users")) {
      Assertions.assertEquals("用户信息", u.asText());
    }
  }

  /**
   * 同一结构，{@code users} 每项为随机姓氏（仍是一条模板 render）。
   */
  @Test
  void apiResponse_repeatLastname_keepsSurroundingJson() throws Exception {
    final int n = 4;
    String template =
        "{\"name\":\"xxx函数\",\"description\":\"xxx函数\",\"users\":@Repeat(@Lastname()," + n + ")}";

    String raw = engine.render(template);

    JsonNode tree = mapper.readTree(raw);
    Assertions.assertEquals("xxx函数", tree.get("name").asText());
    Assertions.assertEquals("xxx函数", tree.get("description").asText());
    Assertions.assertEquals(n, tree.get("users").size());
    for (JsonNode user : tree.get("users")) {
      Assertions.assertFalse(user.asText().isBlank());
      Assertions.assertFalse(user.asText().contains("@"));
    }
    Assertions.assertFalse(raw.contains("@Lastname"));
    Assertions.assertFalse(raw.contains("@Repeat"));
  }
}
