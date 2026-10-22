package cloud.xcan.jmock.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import cloud.xcan.jmock.core.fixtures.MEcho;
import cloud.xcan.jmock.core.testsupport.MutableFunctionRegistry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 文本里夹着 {@code @函数(...)} 时，只替换函数调用，周围字符原样保留。
 * 函数返回值不再扫描，避免结果里的 {@code @} 被二次渲染。
 */
class MockEngineMixedTextUnitTest {

  private MockEngine engine;

  @BeforeEach
  void setUp() {
    MutableFunctionRegistry registry = new MutableFunctionRegistry();
    registry.register(MEcho.class);
    engine = new MockEngine(registry);
  }

  @Test
  void surroundingTextStaysAndFunctionsAreReplaced() {
    assertEquals("用户张三，编号7。", engine.render("用户@Echo(张三)，编号@Echo(7)。"));
  }

  @Test
  void adjacentFunctionsHaveNoInsertedSeparator() {
    assertEquals("甲乙", engine.render("@Echo(甲)@Echo(乙)"));
  }

  @Test
  void emailWithoutParenthesesStaysLiteral() {
    assertEquals("ops@example.com/7", engine.render("ops@example.com/@Echo(7)"));
  }

  @Test
  void escapedAtIsNotAFunction() {
    assertEquals("见@Echo(隐藏)处", engine.render("见\\@Echo(隐藏)处"));
  }

  @Test
  void lowercaseAtIsText() {
    assertEquals("keep @echo(1) and 1", engine.render("keep @echo(1) and @Echo(1)"));
  }

  @Test
  void unknownFunctionKeepsItsTokenInsideTheSentence() {
    assertEquals("id=@Nope(1);ok", engine.render("id=@Nope(1);ok"));
  }

  @Test
  void quotedArgumentKeepsInnerComma() {
    assertEquals("前a,b后", engine.render("前@Echo(\"a,b\")后"));
  }

  @Test
  void functionResultIsNotRenderedAgain() {
    assertEquals("值@Echo(no)", engine.render("值@Echo(\"@Echo(no)\")"));
  }

  @Test
  void repeatInsideASentenceBecomesAJsonArray() {
    assertEquals("列表[\"项\",\"项\"]完", engine.render("列表@Repeat(@Echo(项),2)完"));
  }

  @Test
  void whitespaceAroundTheCallIsKept() {
    assertEquals(" a\n", engine.render(" @Echo(a)\n"));
  }

  @Test
  void parsedAstRendersTheSameSentence() {
    String template = "你好@Echo(世界)";
    List<MockExpr> ast = engine.parse(template);
    assertEquals("你好世界", engine.render(template));
    assertEquals(engine.render(template), engine.render(ast));
    assertFalse(engine.render(ast).contains("@Echo"));
  }
}
