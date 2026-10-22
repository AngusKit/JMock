package cloud.xcan.jmock.plugin;

import static cloud.xcan.jmock.api.i18n.JMockFuncDocMessage.DOC_PARAMETER_NULL_WEIGHT;
import static cloud.xcan.jmock.api.i18n.JMockMessage.PARAM_MAX_T;
import static cloud.xcan.jmock.api.i18n.JMockMessage.PARAM_WEIGHT_T;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_CATEGORY_BASIC;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_C1;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_C2;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_C3;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_C4;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_DESC;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_PARAMETER_MAX;
import static cloud.xcan.jmock.plugin.BasicDocMessage.DOC_LONG_PARAMETER_MIN;

import cloud.xcan.jmock.api.AbstractMockFunction;
import cloud.xcan.jmock.api.WeightedSampler;
import cloud.xcan.jmock.api.docs.annotation.JMockConstructor;
import cloud.xcan.jmock.api.docs.annotation.JMockFunctionRegister;
import cloud.xcan.jmock.api.docs.annotation.JMockParameter;
import cloud.xcan.jmock.api.exception.ParamParseException;
import cloud.xcan.jmock.api.support.utils.RandomUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Bao Zhang
 * @author XiaoLong Liu
 */
@Setter
@Getter
@JMockFunctionRegister(descI18nKey = DOC_LONG_DESC,
    categoryI18nKey = {DOC_CATEGORY_BASIC}, order = 102)
public class MLong extends AbstractMockFunction {

  @JMockParameter(descI18nKey = DOC_LONG_PARAMETER_MIN, type = "Long", defaultValue = "0")
  private long min;

  @JMockParameter(descI18nKey = DOC_LONG_PARAMETER_MAX, type = "Long", defaultValue = "9223372036854775807")
  private long max;

  @JMockParameter(descI18nKey = DOC_PARAMETER_NULL_WEIGHT, type = "String")
  private WeightedSampler nullSampler;

  /**
   * Max value of Long allowed
   */
  final static long DEFAULT_MAX_VALUE = Long.MAX_VALUE;

  /**
   * Min value of Long allowed
   */
  final static long LONG_MIN_VALUE = Long.MIN_VALUE;

  /**
   * default Min value of Long allowed
   */
  final static long DEFAULT_MIN_VALUE = 0L;

  @JMockConstructor(descI18nKey = DOC_LONG_C1,
      example = "@Long()", exampleValues = {"2567071027"}
  )
  public MLong() {
    this(null);
  }

  @JMockConstructor(descI18nKey = DOC_LONG_C2,
      example = "@Long(\"1:2\")",
      exampleValues = {"98089", "null", "28907625479"}
  )
  public MLong(String nullWeight) {
    this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, nullWeight);
  }

  @JMockConstructor(descI18nKey = DOC_LONG_C3,
      example = "@Long(1L,10000000000L)",
      exampleValues = {"1109", "34008978", "87256252199901"}
  )
  public MLong(Long min, Long max) {
    this(min, max, null);
  }

  /**
   * Generate any integer (4 bytes) value, and the value range is -9223372036854775808 ～
   * 9223372036854775807 when it is signed.
   *
   * @param min        the minimum value, the default minimum value is not specified 0
   * @param max        the maximum value, if not specified, the default maximum is
   *                   9223372036854775807
   * @param nullWeight a ratio of null, such as value: "1:9", which means that a random mstring is
   *                   generated 10 times and an average of 1 is null
   */
  @JMockConstructor(descI18nKey = DOC_LONG_C4,
      example = "@Long(-10000L,100000L,)",
      exampleValues = {"198", "594", "-17865", "9876", "37092"})
  public MLong(Long min, Long max, String nullWeight) {
    this.max = max;
    this.min = min;
    if (this.min > max) {
      ParamParseException.throw0(PARAM_MAX_T, new Object[]{"min", max});
    }
    if (null != nullWeight && !nullWeight.isEmpty()) {
      try {
        this.nullSampler = WeightedSampler.of(nullWeight);
      } catch (IllegalArgumentException e) {
        ParamParseException.throw0(PARAM_WEIGHT_T, new Object[]{"nullWeight"});
      }
    }
  }

  @Override
  public Long mock() {
    if (nullSampler != null && nullSampler.shouldBeNull()) {
      return null;
    }
    long upper = this.max == Long.MAX_VALUE ? this.max : this.max + 1;
    return RandomUtils.nextLong(this.min, upper);
  }

}
