package cloud.xcan.jmock.core.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cloud.xcan.jmock.api.AbstractMockFunction;
import cloud.xcan.jmock.api.i18n.ThreadLocaleHolder;
import cloud.xcan.jmock.core.fixtures.MEcho;
import cloud.xcan.jmock.core.fixtures.PlainMockFunction;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class DefaultFunctionRegistryUnitTest {

  @AfterEach
  void tearDown() {
    ThreadLocaleHolder.removeLocale();
  }

  @Test
  void register_lookup_withMprefix() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register(MEcho.class);
    assertTrue(reg.lookup("Echo").isPresent());
    assertEquals(MEcho.class, reg.lookup("Echo").orElseThrow());
    assertTrue(reg.registeredNames().contains("MEcho"));
  }

  @Test
  void register_skipsNullAbstractOrNonAbstractMock() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register(null);
    reg.register(PlainMockFunction.class);
    assertFalse(reg.lookup("PlainMockFunction").isPresent());
  }

  @Test
  void register_alias() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register("AliasEcho", MEcho.class);
    assertEquals(MEcho.class, reg.lookup("AliasEcho").orElseThrow());
  }

  @Test
  void unregister() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register(MEcho.class);
    assertEquals(MEcho.class, reg.unregister("MEcho"));
    assertTrue(reg.lookup("Echo").isEmpty());
  }

  @Test
  void constructorDoesNotSetCallerLocale() {
    ThreadLocaleHolder.removeLocale();
    new DefaultFunctionRegistry(Locale.FRANCE);
    assertNull(ThreadLocaleHolder.localeHolder.get());
  }

  @Test
  void reload_and_clear() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register(MEcho.class);
    assertNotNull(reg.lookup("Echo").orElse(null));
    reg.reload();
    reg.clear();
    assertNull(ThreadLocaleHolder.localeHolder.get());
  }

  abstract static class AbstractFn extends AbstractMockFunction {

    @Override
    public Object mock() {
      return null;
    }
  }

  @Test
  void register_skipsAbstractSubclass() {
    DefaultFunctionRegistry reg = new DefaultFunctionRegistry(Locale.ENGLISH);
    reg.clear();
    reg.register(AbstractFn.class);
    assertTrue(reg.lookup("AbstractFn").isEmpty());
  }
}
