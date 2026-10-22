package cloud.xcan.jmock.engine;

import cloud.xcan.jmock.core.engine.MockEngine;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 用真实插件验证整句模板：固定值必须逐字相等，随机值只替换调用、不改周围文本。
 */
public class MockEngineMixedTextTest {

  private MockEngine engine;

  @BeforeEach
  void setUp() {
    engine = MockEngine.defaultEngine();
  }

  @Test
  public void fixedValuesReplaceOnlyTheCalls() {
    String result = engine.render("共@Integer(2,2)人，标记@Enum(OPEN|CLOSED)完");
    Assertions.assertTrue(result.equals("共2人，标记OPEN完") || result.equals("共2人，标记CLOSED完"),
        result);
    Assertions.assertFalse(result.contains("@"));
  }

  @Test
  public void integerUpperBoundIsReachableInsideText() {
    Set<String> seen = new HashSet<>();
    for (int i = 0; i < 40; i++) {
      String result = engine.render("值=@Integer(8,9)。");
      Assertions.assertTrue(result.equals("值=8。") || result.equals("值=9。"), result);
      seen.add(result);
    }
    Assertions.assertEquals(Set.of("值=8。", "值=9。"), seen);
  }

  @Test
  public void severalCallsKeepTheirOwnPlaces() {
    String result = engine.render("a=@Integer(3,3),b=@Integer(4,4),c=@String(4)");
    Assertions.assertTrue(result.startsWith("a=3,b=4,c="), result);
    Assertions.assertEquals(4, result.substring("a=3,b=4,c=".length()).length(), result);
    Assertions.assertFalse(result.contains("@"));
  }

  @Test
  public void stringLengthStaysBetweenTheFixedWords() {
    for (int i = 0; i < 20; i++) {
      String result = engine.render("编码@String(4)完毕");
      Assertions.assertTrue(result.startsWith("编码") && result.endsWith("完毕"), result);
      Assertions.assertEquals(4, result.length() - "编码完毕".length(), result);
      Assertions.assertFalse(result.contains("@String"));
    }
  }

  @Test
  public void emailCallRendersInsideTheSentence() {
    String result = engine.render("邮箱=@Email()；备注ops@example.com");
    Assertions.assertTrue(result.startsWith("邮箱="), result);
    Assertions.assertTrue(result.endsWith("；备注ops@example.com"), result);
    String address = result.substring("邮箱=".length(), result.indexOf('；'));
    Assertions.assertTrue(address.contains("@") && !address.contains("@Email"), result);
    Assertions.assertFalse(result.contains("@Email()"), result);
  }
}
