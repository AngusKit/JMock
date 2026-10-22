package cloud.xcan.jmock.engine;

import cloud.xcan.jmock.core.engine.MockExpr;
import cloud.xcan.jmock.core.engine.MockParser;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MockParser}.
 */
public class MockParserTest {

  private final MockParser parser = new MockParser();

  // ----- plain text -----

  @Test
  public void plainText_producesOneTextSegment() {
    List<MockExpr> result = parser.parse("hello world");
    Assertions.assertEquals(1, result.size());
    MockExpr.TextSegment ts = (MockExpr.TextSegment) result.get(0);
    Assertions.assertEquals("hello world", ts.text());
  }

  @Test
  public void emptyString_returnsEmptyList() {
    List<MockExpr> result = parser.parse("");
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void nullInput_returnsEmptyList() {
    List<MockExpr> result = parser.parse(null);
    Assertions.assertTrue(result.isEmpty());
  }

  // ----- function calls -----

  @Test
  public void noArgFunction_parsesFunctionCall() {
    List<MockExpr> result = parser.parse("@Integer()");
    Assertions.assertEquals(1, result.size());
    MockExpr.FunctionCall fc = (MockExpr.FunctionCall) result.get(0);
    Assertions.assertEquals("Integer", fc.name());
    Assertions.assertTrue(fc.args().isEmpty());
    Assertions.assertTrue(fc.explicitEmptyParentheses());
  }

  @Test
  public void functionWithLiteralArgs_parsesArgs() {
    List<MockExpr> result = parser.parse("@Integer(1,100)");
    Assertions.assertEquals(1, result.size());
    MockExpr.FunctionCall fc = (MockExpr.FunctionCall) result.get(0);
    Assertions.assertEquals("Integer", fc.name());
    Assertions.assertEquals(2, fc.args().size());
    Assertions.assertEquals("1", fc.args().get(0).literalValue());
    Assertions.assertEquals("100", fc.args().get(1).literalValue());
    Assertions.assertFalse(fc.args().get(0).isExpression());
  }

  @Test
  public void multipleNodesInTemplate_producesCorrectList() {
    List<MockExpr> result = parser.parse("id:@Integer(),name:@Name()");
    Assertions.assertEquals(4, result.size());
    Assertions.assertTrue(result.get(0) instanceof MockExpr.TextSegment);
    Assertions.assertTrue(result.get(1) instanceof MockExpr.FunctionCall);
    Assertions.assertTrue(result.get(2) instanceof MockExpr.TextSegment);
    Assertions.assertTrue(result.get(3) instanceof MockExpr.FunctionCall);
    Assertions.assertEquals("id:", ((MockExpr.TextSegment) result.get(0)).text());
    Assertions.assertEquals("Integer", ((MockExpr.FunctionCall) result.get(1)).name());
    Assertions.assertEquals(",name:", ((MockExpr.TextSegment) result.get(2)).text());
    Assertions.assertEquals("Name", ((MockExpr.FunctionCall) result.get(3)).name());
  }

  // ----- function without parens (bare token) -----

  @Test
  public void functionWithoutParens_parsesFunctionCall() {
    List<MockExpr> result = parser.parse("@Integer");
    Assertions.assertEquals(1, result.size());
    MockExpr.FunctionCall fc = (MockExpr.FunctionCall) result.get(0);
    Assertions.assertEquals("Integer", fc.name());
    Assertions.assertTrue(fc.args().isEmpty());
    Assertions.assertFalse(fc.explicitEmptyParentheses());
  }

  // ----- escape sequences -----

  @Test
  public void escapedAt_producesTextSegment() {
    List<MockExpr> result = parser.parse("\\@Integer()");
    // After escape: '@Integer()' treated as text, not a function
    Assertions.assertEquals(1, result.size());
    Assertions.assertTrue(result.get(0) instanceof MockExpr.TextSegment);
    String text = ((MockExpr.TextSegment) result.get(0)).text();
    Assertions.assertEquals("@Integer()", text);
  }

  // ----- nested function call (expression argument) -----

  @Test
  public void nestedFunctionArg_parsesAsExpressionArgument() {
    List<MockExpr> result = parser.parse("@Repeat(@Integer(),3)");
    Assertions.assertEquals(1, result.size());
    // Parser converts @Repeat(@Expr, N) to ArrayExpr
    MockExpr.ArrayExpr ae = (MockExpr.ArrayExpr) result.get(0);
    Assertions.assertEquals(3, ae.count());
    MockExpr.FunctionCall inner = (MockExpr.FunctionCall) ae.itemExpr();
    Assertions.assertEquals("Integer", inner.name());
  }

  // ----- ArrayExpr from @Repeat -----

  @Test
  public void repeatWithCount_producesArrayExpr() {
    List<MockExpr> result = parser.parse("@Repeat(@Name(),5)");
    Assertions.assertEquals(1, result.size());
    MockExpr.ArrayExpr ae = (MockExpr.ArrayExpr) result.get(0);
    Assertions.assertEquals(5, ae.count());
    Assertions.assertTrue(ae.itemExpr() instanceof MockExpr.FunctionCall);
    Assertions.assertEquals("Name", ((MockExpr.FunctionCall) ae.itemExpr()).name());
  }

  @Test
  public void repeatWithLiteralArg_doesNotProduceArrayExpr() {
    // @Repeat(hello,3) — first arg is literal, not expression → stays as FunctionCall
    List<MockExpr> result = parser.parse("@Repeat(hello,3)");
    Assertions.assertEquals(1, result.size());
    Assertions.assertTrue(result.get(0) instanceof MockExpr.FunctionCall);
  }

  // ----- parseExpression strict mode -----

  @Test
  public void parseExpression_withValidFunction_returnsNode() {
    MockExpr expr = parser.parseExpression("@Integer(1,10)");
    Assertions.assertNotNull(expr);
    Assertions.assertTrue(expr instanceof MockExpr.FunctionCall);
  }

  @Test
  public void parseExpression_withPlainText_throwsException() {
    Assertions.assertThrows(IllegalArgumentException.class,
        () -> parser.parseExpression("hello"));
  }

  // ----- low-case / invalid function name → treated as text -----

  @Test
  public void lowercaseFunctionName_treatedAsText() {
    // '@integer()' starts with lowercase — not a valid function
    List<MockExpr> result = parser.parse("@integer()");
    // The @ is the function identifier but the name validation fails, so it falls back to text
    // At least one element should exist and it should be a TextSegment
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals("@integer()", ((MockExpr.TextSegment) result.get(0)).text());
  }
}
