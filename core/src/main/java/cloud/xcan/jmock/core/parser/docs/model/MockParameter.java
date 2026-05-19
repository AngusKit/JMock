package cloud.xcan.jmock.core.parser.docs.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
public class MockParameter {

  private String name;

  private String description;

  private String type;

  private String defaultValue;

  private boolean required;

}
