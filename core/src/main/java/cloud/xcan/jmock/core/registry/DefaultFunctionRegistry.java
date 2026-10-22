package cloud.xcan.jmock.core.registry;

import cloud.xcan.jmock.api.AbstractMockFunction;
import cloud.xcan.jmock.api.MockFunction;
import cloud.xcan.jmock.api.i18n.RegisterDocMessage;
import cloud.xcan.jmock.api.i18n.ThreadLocaleHolder;
import cloud.xcan.jmock.core.spi.MockFunctionSpiClasses;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default SPI-based function registry.
 * <p>
 * Discovers {@link MockFunction} classes from {@link MockFunctionSpiClasses#RESOURCE_PATH} (no
 * {@link ServiceLoader} instantiation). Backed by {@link ConcurrentHashMap} for lock-free
 * concurrent reads.
 * <p>
 * Note: This class has NO compile-time dependency on any plugin module. Plugins are discovered
 * purely at runtime via SPI.
 *
 * @since 2.0.0
 */
public class DefaultFunctionRegistry implements FunctionRegistry {

  private final ConcurrentHashMap<String, Class<? extends MockFunction>> registry =
      new ConcurrentHashMap<>();

  public DefaultFunctionRegistry() {
    this(Locale.getDefault());
  }

  public DefaultFunctionRegistry(Locale locale) {
    if (locale == null) {
      throw new IllegalArgumentException("locale");
    }
    loadServiceMockRegister();
    loadServiceMessageRegister();
    // Locale stays on the caller around render. The constructor must not leave a
    // thread-local that later work on the same pooled thread would inherit.
  }

  @Override
  public void register(Class<? extends MockFunction> clazz) {
    if (clazz == null || isAbstractOrInterface(clazz)) {
      return;
    }
    if (!AbstractMockFunction.class.isAssignableFrom(clazz)) {
      return;
    }
    registry.put(clazz.getSimpleName(), clazz);
  }

  @Override
  public void register(String alias, Class<? extends MockFunction> clazz) {
    if (alias == null || clazz == null || isAbstractOrInterface(clazz)) {
      return;
    }
    if (!AbstractMockFunction.class.isAssignableFrom(clazz)) {
      return;
    }
    registry.put(alias, clazz);
  }

  @Override
  public Class<? extends MockFunction> unregister(String name) {
    return registry.remove(name);
  }

  @Override
  public Optional<Class<? extends MockFunction>> lookup(String name) {
    // Try with 'M' prefix first (standard convention), then plain name
    Class<? extends MockFunction> clz = registry.get("M" + name);
    if (clz != null) {
      return Optional.of(clz);
    }
    clz = registry.get(name);
    return Optional.ofNullable(clz);
  }

  @Override
  public Collection<String> registeredNames() {
    return Collections.unmodifiableCollection(registry.keySet());
  }

  @Override
  public Collection<Class<? extends MockFunction>> registeredClasses() {
    return Collections.unmodifiableCollection(registry.values());
  }

  @Override
  public void reload() {
    registry.clear();
    loadServiceMockRegister();
    loadServiceMessageRegister();
  }

  @Override
  public void clear() {
    registry.clear();
    ThreadLocaleHolder.removeLocale();
  }

  private void loadServiceMockRegister() {
    MockFunctionSpiClasses.forEachClass(MockFunctionSpiClasses.resolveClassLoader(),
        this::register);
  }

  private void loadServiceMessageRegister() {
    ServiceLoader<RegisterDocMessage> loader = ServiceLoader.load(RegisterDocMessage.class);
    loader.forEach(RegisterDocMessage::register);
  }

  private static boolean isAbstractOrInterface(Class<?> clazz) {
    return Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface();
  }
}
