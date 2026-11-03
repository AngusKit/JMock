package cloud.xcan.jmock.api.i18n;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Get locale messages from Resource Bundle.
 *
 * @author XiaoLong Liu
 */
public class MessageResources {

  public static final Set<String> RESOURCE_BUNDLE = new HashSet<>();

  /**
   * Plugin bundles keep Chinese in the root file and English in {@code *_en}.
   * The default {@link ResourceBundle} search falls back to the JVM locale before
   * the root, so an English JVM turns {@code zh_CN} into the English file.
   */
  private static final ResourceBundle.Control LOCALE_CONTROL = new ResourceBundle.Control() {
    @Override
    public Locale getFallbackLocale(String baseName, Locale locale) {
      return null;
    }
  };

  static {
    RESOURCE_BUNDLE.add("i18n/jmock-messages");
  }

  /**
   * You cannot instantiate this class.
   */
  private MessageResources() {
  }

  /**
   * Looks up a string resource identified by {@code key} in {@code resources}.
   */
  public static String getString(String key) {
    for (String res : RESOURCE_BUNDLE) {
      ResourceBundle bundle = ResourceBundle.getBundle(res);
      if (bundle.containsKey(key)) {
        return bundle.getString(key);
      }
    }
    return "";
  }

  /**
   * Looks up a string resource identified by {@code key} in {@code resources}.
   */
  public static String getString(String key, Locale locale) {
    if (locale == null) {
      return getString(key);
    }
    // Chinese lives in the root bundle and English in *_en. Asking the JDK for
    // zh_CN lets the default search pick the JVM locale before the root, so an
    // English JVM turns @Month(zh_CN) into "July". Select the bundle directly.
    Locale bundleLocale = bundleLocale(locale);
    for (String res : RESOURCE_BUNDLE) {
      ResourceBundle bundle = ResourceBundle.getBundle(res, bundleLocale, LOCALE_CONTROL);
      if (bundle.containsKey(key)) {
        return bundle.getString(key);
      }
    }
    return "";
  }

  /**
   * English locales read {@code *_en}. Every other locale, including {@code zh_CN},
   * reads the root bundle.
   */
  static Locale bundleLocale(Locale locale) {
    String language = locale.getLanguage();
    if (language != null && language.toLowerCase(Locale.ROOT).startsWith("en")) {
      return Locale.ENGLISH;
    }
    return Locale.ROOT;
  }

  /**
   * Looks up a string resource identified by {@code key} in {@code resources} and formats it as a
   * message using {@code MessageFormat.format} with the given {@code arguments}.
   */
  public static String getString(String key, Object[] arguments) {
    return MessageFormat.format(getString(key), arguments);
  }

  /**
   * Looks up a string resource identified by {@code key} in {@code resources} and formats it as a
   * message using {@code MessageFormat.format} with the given {@code arguments}.
   */
  public static String getString(String key, Object[] arguments, Locale locale) {
    return MessageFormat.format(getString(key, locale), arguments);
  }

}
