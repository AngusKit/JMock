package cloud.xcan.jmock.plugin;

import static cloud.xcan.jmock.api.i18n.JMockFuncDocMessage.DOC_PARAMETER_LOCALE;
import static cloud.xcan.jmock.plugin.DateDocMessage.DOC_CATEGORY_DATE;
import static cloud.xcan.jmock.plugin.DateDocMessage.DOC_MONTH_C1;
import static cloud.xcan.jmock.plugin.DateDocMessage.DOC_MONTH_C2;
import static cloud.xcan.jmock.plugin.DateDocMessage.DOC_MONTH_DESC;

import cloud.xcan.jmock.api.AbstractMockFunction;
import cloud.xcan.jmock.api.docs.annotation.JMockConstructor;
import cloud.xcan.jmock.api.docs.annotation.JMockFunctionRegister;
import cloud.xcan.jmock.api.docs.annotation.JMockParameter;
import cloud.xcan.jmock.api.i18n.MessageResources;
import cloud.xcan.jmock.api.support.utils.RandomUtils;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author XiaoLong Liu
 */
@Setter
@Getter
@JMockFunctionRegister(descI18nKey = DOC_MONTH_DESC,
    categoryI18nKey = {DOC_CATEGORY_DATE}, order = 272)
public class MMonth extends AbstractMockFunction {

  private String[] dict;

  @JMockParameter(descI18nKey = DOC_PARAMETER_LOCALE, type = "String", defaultValue = "zh_CN")
  private Locale locale;

  final static Locale DEFAULT_LOCALE_VALUE = Locale.CHINA;

  @JMockConstructor(descI18nKey = DOC_MONTH_C1,
      example = "@Month()",
      exampleValues = {"一月"})
  public MMonth() {
    this(DEFAULT_LOCALE_VALUE);
  }

  @JMockConstructor(descI18nKey = DOC_MONTH_C2,
      example = "@Month()",
      exampleValues = {"January"})
  public MMonth(Locale locale) {
    this.locale = ObjectUtils.isEmpty(locale) ? DEFAULT_LOCALE_VALUE : locale;
    String months = MessageResources.getString(DateDocMessage.DATA_MONTH, this.locale);
    this.dict = months.split("\\|");
  }

  @Override
  public String mock() {
    Integer dictIndex = RandomUtils.nextInt(0, dict.length);
    return dict[dictIndex];
  }
}
