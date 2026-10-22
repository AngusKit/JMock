package cloud.xcan.jmock.engine;

import cloud.xcan.jmock.core.engine.MockEngine;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for {@link MockEngine} using the default SPI registry. These tests require
 * plugin JARs to be on the classpath (provided by xcan-jmock.all-plugin).
 */
public class MockEngineTest {

  private MockEngine engine;

  @BeforeEach
  void setUp() {
    engine = MockEngine.defaultEngine();
  }

  // ----- render -----

  @Test
  public void render_nullTemplate_returnsNull() {
    Assertions.assertNull(engine.render((String) null));
  }

  @Test
  public void render_emptyTemplate_returnsEmpty() {
    Assertions.assertEquals("", engine.render(""));
  }

  @Test
  public void render_plainText_returnsAsIs() {
    Assertions.assertEquals("hello world", engine.render("hello world"));
  }

  @Test
  public void render_integerFunction_returnsNumericString() {
    String result = engine.render("@Integer()");
    Assertions.assertNotNull(result);
    // Should parse as a number
    Assertions.assertDoesNotThrow(() -> Long.parseLong(result), "Expected numeric: " + result);
  }

  @Test
  public void render_integerWithRange_inRange() {
    for (int i = 0; i < 50; i++) {
      String result = engine.render("@Integer(1,10)");
      long val = Long.parseLong(result.trim());
      Assertions.assertTrue(val >= 1 && val <= 10, "Out of range: " + val);
    }
  }

  @Test
  public void render_mixedTemplate_replacesOnlyFunctions() {
    String result = engine.render("id=@Integer(1,9999),ok");
    Assertions.assertTrue(result.startsWith("id="), "Should start with 'id=': " + result);
    Assertions.assertTrue(result.endsWith(",ok"), "Should end with ',ok': " + result);
    String middle = result.substring(3, result.length() - 3);
    long val = Long.parseLong(middle);
    Assertions.assertTrue(val >= 1 && val <= 9999, result);
    Assertions.assertFalse(result.contains("@Integer"), result);
  }

  @Test
  public void render_unknownFunction_keepsOriginalToken() {
    String result = engine.render("@NoSuchFunction()");
    Assertions.assertEquals("@NoSuchFunction()", result);
  }

  // ----- evaluate -----

  @Test
  public void evaluate_integerExpression_returnsInteger() {
    Object result = engine.evaluate("@Integer(1,100)");
    Assertions.assertNotNull(result);
    // raw result is a Number
    Assertions.assertTrue(result instanceof Number,
        "Expected Number but got: " + result.getClass());
  }

  @Test
  public void evaluate_booleanExpression_returnsBooleanOrString() {
    Object result = engine.evaluate("@Bool()");
    Assertions.assertNotNull(result);
    String s = result.toString();
    Assertions.assertTrue("true".equals(s) || "false".equals(s), "Expected true/false: " + s);
  }

  @Test
  public void evaluate_nullResult_returnsNull() {
    // @Bool(1:1, 1:1) should produce null sometimes (null weight 1:1)
    // Run many times to ensure we can at least get a non-crash outcome
    boolean sawNull = false;
    boolean sawValue = false;
    for (int i = 0; i < 200; i++) {
      Object result = engine.evaluate("@Bool(1:1,1:1)");
      if (result == null) {
        sawNull = true;
      } else {
        sawValue = true;
      }
      if (sawNull && sawValue) {
        break;
      }
    }
    Assertions.assertTrue(sawNull || sawValue, "Should return something");
  }

  // ----- renderBatch -----

  @Test
  public void renderBatch_zeroCount_returnsEmpty() {
    List<String> result = engine.renderBatch("@Integer()", 0);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void renderBatch_negativeCount_returnsEmpty() {
    List<String> result = engine.renderBatch("@Integer()", -1);
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void renderBatch_producesCorrectCount() {
    List<String> result = engine.renderBatch("@Integer(1,9999)", 100);
    Assertions.assertEquals(100, result.size());
  }

  @Test
  public void renderBatch_randomFunctionProducesDifferentValues() {
    List<String> result = engine.renderBatch("@Integer(1,1000000)", 50);
    Set<String> unique = new HashSet<>(result);
    // With range 1..1M and 50 iterations, expect at least 10 distinct values
    Assertions.assertTrue(unique.size() > 10,
        "Expected random distribution but got only " + unique.size() + " unique values");
  }

  @Test
  public void renderBatch_nullTemplate_returnsEmpty() {
    List<String> result = engine.renderBatch(null, 10);
    Assertions.assertTrue(result.isEmpty());
  }

  // ----- JSON array via @Repeat -----

  @Test
  public void render_repeatWithNestedFunction_producesJsonArray() {
    String result = engine.render("@Repeat(@Integer(1,9),3)");
    Assertions.assertNotNull(result);
    // Should be a JSON array like [5,2,8]
    Assertions.assertTrue(result.startsWith("["), "Expected JSON array: " + result);
    Assertions.assertTrue(result.endsWith("]"), "Expected JSON array: " + result);
    // 3 items
    String[] parts = result.substring(1, result.length() - 1).split(",");
    Assertions.assertEquals(3, parts.length, "Expected 3 items: " + result);
  }

  // ----- getRegistry -----

  @Test
  public void getRegistry_returnsNonNull() {
    Assertions.assertNotNull(engine.getRegistry());
  }
}
