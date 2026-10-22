package cloud.xcan.jmock.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cloud.xcan.jmock.core.fixtures.MEcho;
import cloud.xcan.jmock.core.fixtures.MListJson;
import cloud.xcan.jmock.core.fixtures.MNullFn;
import cloud.xcan.jmock.core.fixtures.MStrict;
import cloud.xcan.jmock.core.testsupport.MutableFunctionRegistry;
import cloud.xcan.jmock.plugin.MFloat;
import cloud.xcan.jmock.plugin.MString;
import java.util.List;
import org.junit.jupiter.api.Test;

class MockEngineUnitTest {

  @Test
  void render_and_evaluate_withRegistry() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    reg.register(MEcho.class);
    MockEngine engine = new MockEngine(reg);

    assertEquals("default", engine.render("@Echo()"));
    assertEquals("hi", engine.render("@Echo(hi)"));
    assertEquals("hi", engine.evaluate("@Echo(hi)"));

    assertNull(engine.render((String) null));
    assertEquals("", engine.render(""));
    assertSame(reg, engine.getRegistry());
  }

  @Test
  void evaluate_multipleNodes_fallsBackToConcatRender() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    reg.register(MEcho.class);
    MockEngine engine = new MockEngine(reg);
    Object out = engine.evaluate("pre@Echo(x)suf");
    assertEquals("prexsuf", out);
  }

  @Test
  void render_unknownFunction_passthroughToken() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    MockEngine engine = new MockEngine(reg);
    assertEquals("@Nope()", engine.render("@Nope()"));
  }

  @Test
  void render_nullResult_fromMock() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    reg.register(MNullFn.class);
    MockEngine engine = new MockEngine(reg);
    assertEquals("null", engine.render("@NullFn()"));
  }

  @Test
  void render_listJson_formatting() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    reg.register(MListJson.class);
    MockEngine engine = new MockEngine(reg);
    String s = engine.render("@ListJson()");
    assertTrue(s.startsWith("[") && s.endsWith("]"));
    assertTrue(s.contains("\\\""));
  }

  @Test
  void renderBatch_reusesAst() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    reg.register(MEcho.class);
    MockEngine engine = new MockEngine(reg);
    List<String> batch = engine.renderBatch("@Echo()", 3);
    assertEquals(3, batch.size());
    assertEquals("default", batch.get(0));

    assertTrue(engine.renderBatch(null, 5).isEmpty());
    assertTrue(engine.renderBatch("", 5).isEmpty());
    assertTrue(engine.renderBatch("@Echo()", 0).isEmpty());
  }

  @Test
  void constructor_withCustomComponents() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    MockParser p = new MockParser();
    MockRenderer r = new MockRenderer();
    MockEngine engine = new MockEngine(reg, p, r);
    reg.register(MEcho.class);
    assertEquals("z", engine.render("@Echo(z)"));
  }

  @Test
  void constructor_withFullArgs() {
    MutableFunctionRegistry reg = new MutableFunctionRegistry();
    MockParser p = new MockParser();
    MockRenderer r = new MockRenderer();
    MockEngine engine = new MockEngine(reg, p, r);
    reg.register(MFloat.class);
    for (int i = 0; i < 20; i++) {
      String rendered = engine.render("v=@Float(5,15)");
      assertTrue(rendered.startsWith("v=") && !rendered.contains("@Float"), rendered);
      float value = Float.parseFloat(rendered.substring(2));
      assertTrue(value >= 5f && value <= 15f, rendered);
    }
  }
}
