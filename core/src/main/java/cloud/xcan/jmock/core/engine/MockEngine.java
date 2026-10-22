package cloud.xcan.jmock.core.engine;

import cloud.xcan.jmock.core.registry.DefaultFunctionRegistry;
import cloud.xcan.jmock.core.registry.FunctionRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified entry point for the JMock 2.0 engine.
 * <p>
 * Replaces direct use of Replacer/Extractor/Environment with a single composable, thread-safe API.
 *
 * <pre>{@code
 * // Simple template rendering
 * String result = MockEngine.defaultEngine().render("Hello @Name()!");
 *
 * // Batch generation
 * List<String> records = MockEngine.defaultEngine().renderBatch(template, 1000);
 *
 * // Single expression evaluation
 * Object value = MockEngine.defaultEngine().evaluate("@Integer(1,100)");
 * }</pre>
 *
 * @since 2.0.0
 */
public final class MockEngine {

  private final FunctionRegistry registry;
  private final MockParser parser;
  private final MockRenderer renderer;

  public MockEngine(FunctionRegistry registry) {
    this.registry = registry;
    this.parser = new MockParser();
    this.renderer = new MockRenderer();
  }

  public MockEngine(FunctionRegistry registry, MockParser parser, MockRenderer renderer) {
    this.registry = registry;
    this.parser = parser;
    this.renderer = renderer;
  }

  /**
   * Create a default engine with SPI-loaded function registry.
   */
  public static MockEngine defaultEngine() {
    return new MockEngine(new DefaultFunctionRegistry());
  }

  /**
   * Render a template string, replacing all @Function tokens with mock values.
   *
   * @param template the template text containing @Function tokens
   * @return the rendered text with all tokens replaced
   */
  public String render(String template) {
    if (template == null || template.isEmpty()) {
      return template;
    }
    return render(parse(template));
  }

  /**
   * Parse once so callers can reuse the AST. Nodes are immutable records.
   */
  public List<MockExpr> parse(String template) {
    if (template == null || template.isEmpty()) {
      return List.of();
    }
    return parser.parse(template);
  }

  /**
   * Render an already parsed template with this engine's registry.
   */
  public String render(List<MockExpr> ast) {
    if (ast == null || ast.isEmpty()) {
      return "";
    }
    return renderer.render(ast, registry);
  }

  /**
   * Evaluate a single mock expression and return the raw result.
   *
   * @param expression a single mock expression, e.g. "@Integer(1,100)"
   * @return the evaluated result
   */
  public Object evaluate(String expression) {
    if (expression == null || expression.isEmpty()) {
      return expression;
    }
    List<MockExpr> ast = parser.parse(expression);
    List<Object> results = renderer.renderRaw(ast, registry);
    if (results.size() == 1) {
      return results.get(0);
    }
    // Multiple nodes (e.g. text + function): return concatenated string
    return render(expression);
  }

  /**
   * Render the same template multiple times to produce a batch of results.
   *
   * @param template the template text
   * @param count    number of records to generate
   * @return list of rendered strings
   */
  public List<String> renderBatch(String template, int count) {
    if (template == null || template.isEmpty() || count <= 0) {
      return Collections.emptyList();
    }
    // Parse once — AST nodes are immutable records; MockFunction instances are created
    // fresh per render() call via reflection, so the same AST is safe to reuse.
    List<MockExpr> ast = parser.parse(template);
    List<String> results = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      results.add(renderer.render(ast, registry));
    }
    return results;
  }

  /**
   * Get the underlying function registry.
   */
  public FunctionRegistry getRegistry() {
    return registry;
  }
}
