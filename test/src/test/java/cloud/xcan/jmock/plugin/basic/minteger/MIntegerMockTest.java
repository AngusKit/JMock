package cloud.xcan.jmock.plugin.basic.minteger;

import cloud.xcan.jmock.api.FunctionToken;
import cloud.xcan.jmock.core.parser.SimpleMockFunctionTokenParser;
import cloud.xcan.jmock.plugin.MInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MIntegerMockTest {

  @Test
  public void MInteger1_defaultRange() throws Exception {
    FunctionToken token = new FunctionToken("Integer", new String[]{});
    SimpleMockFunctionTokenParser parser = new SimpleMockFunctionTokenParser();
    MInteger mock = (MInteger) parser.parse(token);
    for (int i = 0; i < 20; i++) {
      Integer val = mock.mock();
      Assertions.assertNotNull(val);
    }
  }

  @Test
  public void MInteger2_customRange() throws Exception {
    FunctionToken token = new FunctionToken("Integer", new String[]{"-100", "100"});
    SimpleMockFunctionTokenParser parser = new SimpleMockFunctionTokenParser();
    MInteger mock = (MInteger) parser.parse(token);
    for (int i = 0; i < 100; i++) {
      Integer val = mock.mock();
      Assertions.assertNotNull(val);
      Assertions.assertTrue(val >= -100 && val <= 100,
          "Value out of range: " + val);
    }
  }

  @Test
  public void MInteger5_maxIsInclusive() {
    MInteger mock = new MInteger(1, 2);
    boolean sawOne = false;
    boolean sawTwo = false;
    for (int i = 0; i < 40; i++) {
      int val = mock.mock();
      Assertions.assertTrue(val == 1 || val == 2, "out of range: " + val);
      sawOne |= val == 1;
      sawTwo |= val == 2;
    }
    Assertions.assertTrue(sawOne && sawTwo, "max=2 must be reachable");
  }

  @Test
  public void MInteger3_nullWeight() throws Exception {
    FunctionToken token = new FunctionToken("Integer", new String[]{"1:1"});
    SimpleMockFunctionTokenParser parser = new SimpleMockFunctionTokenParser();
    MInteger mock = (MInteger) parser.parse(token);
    boolean hasNull = false;
    boolean hasValue = false;
    for (int i = 0; i < 1000; i++) {
      Integer val = mock.mock();
      if (val == null) {
        hasNull = true;
      } else {
        hasValue = true;
      }
    }
    Assertions.assertTrue(hasNull, "Should produce null values with 1:1 nullWeight");
    Assertions.assertTrue(hasValue, "Should produce non-null values too");
  }

  @Test
  public void MInteger4_rangeAndNullWeight() throws Exception {
    FunctionToken token = new FunctionToken("Integer", new String[]{"-100", "100", "1:1"});
    SimpleMockFunctionTokenParser parser = new SimpleMockFunctionTokenParser();
    MInteger mock = (MInteger) parser.parse(token);
    boolean hasNull = false;
    for (int i = 0; i < 1000; i++) {
      Integer val = mock.mock();
      if (val == null) {
        hasNull = true;
      } else {
        Assertions.assertTrue(val >= -100 && val <= 100,
            "Value out of range: " + val);
      }
    }
    Assertions.assertTrue(hasNull, "Should produce null values with 1:1 nullWeight");
  }
}
