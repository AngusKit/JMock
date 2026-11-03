package cloud.xcan.jmock.core.support.i18n;


import cloud.xcan.jmock.api.i18n.MessageResources;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageResourcesTest {

  String messageKey = "jmock.func.param.size.t";
  String enMessageTemplate = "Size of parameter {0} must be between {1} and {2}";
  String enMessage = "Size of parameter length must be between 1 and 10";
  String chMessageTemplate = "参数{0}个数必须在{1}和{2}之间";
  String chMessage = "参数length个数必须在1和10之间";

  @Test
  public void changeLocaleTest() {
    ResourceBundle RESOURCES = ResourceBundle.getBundle("i18n/jmock-messages", Locale.US);
    Assertions.assertEquals("en", RESOURCES.getLocale().getLanguage());
    Assertions.assertEquals(enMessageTemplate, RESOURCES.getString(messageKey));

    Locale.setDefault(new Locale("zh", "CN"));
    RESOURCES = ResourceBundle.getBundle("i18n/jmock-messages");
    Assertions.assertEquals("", RESOURCES.getLocale().getLanguage());
    Assertions.assertEquals(chMessageTemplate, RESOURCES.getString(messageKey));

    Locale.setDefault(new Locale("en"));
  }

  @Test
  public void getString() {
    String message = MessageResources.getString(messageKey, Locale.US);
    Assertions.assertEquals(enMessageTemplate, message);
    message = MessageResources.getString(messageKey, new Locale("zh", "CN"));
    Assertions.assertEquals(chMessageTemplate, message);
  }

  @Test
  public void requestedLocaleDoesNotFallBackToJvmDefault() {
    Locale previous = Locale.getDefault();
    try {
      Locale.setDefault(Locale.US);
      String message = MessageResources.getString(messageKey, Locale.CHINA);
      Assertions.assertEquals(chMessageTemplate, message);
    } finally {
      Locale.setDefault(previous);
    }
  }

  @Test
  public void getStringWithParamTest() {
    String message = MessageResources.getString(messageKey, new Object[]{"length", 1, 10},
        Locale.US);
    Assertions.assertEquals(enMessage, message);
    message = MessageResources.getString(messageKey, new Object[]{"length", 1, 10},
        new Locale("zh", "CN"));
    Assertions.assertEquals(chMessage, message);
  }

}
