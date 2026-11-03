package cloud.xcan.jmock.plugin;

import static cloud.xcan.jmock.api.i18n.JMockFuncDocMessage.DOC_PARAMETER_LOCALE;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_CATEGORY_USER;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_EDUCATION_C1;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_EDUCATION_C2;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_EDUCATION_C3;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_EDUCATION_DESC;
import static cloud.xcan.jmock.plugin.UserDocMessage.DOC_EDUCATION_PARAMETER_DICT;

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
 * @author Hao Guo
 * @author XiaoLong Liu
 */
@Setter
@Getter
@JMockFunctionRegister(descI18nKey = DOC_EDUCATION_DESC,
    categoryI18nKey = {DOC_CATEGORY_USER}, order = 808)
public class MEducation extends AbstractMockFunction {

  @JMockParameter(descI18nKey = DOC_EDUCATION_PARAMETER_DICT, type = "String")
  private String[] dict;

  @JMockParameter(descI18nKey = DOC_PARAMETER_LOCALE, type = "String", defaultValue = "zh_CN")
  private Locale locale;

  final static Locale DEFAULT_LOCALE_VALUE = Locale.CHINA;

  @JMockConstructor(descI18nKey = DOC_EDUCATION_C1,
      example = "@Education()",
      exampleValues = {"本科"})
  public MEducation() {
    this(DEFAULT_LOCALE_VALUE);
  }

  @JMockConstructor(descI18nKey = DOC_EDUCATION_C2,
      example = "@Education(zh_CN)",
      exampleValues = {"本科"})
  public MEducation(Locale locale) {
    this(locale, null);
  }

  @JMockConstructor(descI18nKey = DOC_EDUCATION_C3,
      example = "@Education(本科,硕士)",
      exampleValues = {"本科"})
  public MEducation(String dict) {
    this(null, dict);
  }

  public MEducation(Locale locale, String dict) {
    this.locale = ObjectUtils.isEmpty(locale) ? DEFAULT_LOCALE_VALUE : locale;
    if (ObjectUtils.isEmpty(dict)) {
      String education = MessageResources.getString(UserDocMessage.DATA_EDUCATION, this.locale);
      this.dict = education.split("\\|");
    } else {
      this.dict = dict.split("\\|");
    }
  }

  @Override
  public String mock() {
    Integer dictIndex = RandomUtils.nextInt(0, dict.length);
    return dict[dictIndex];
  }

}
