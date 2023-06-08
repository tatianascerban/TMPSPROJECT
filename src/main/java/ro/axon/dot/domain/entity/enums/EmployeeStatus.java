package ro.axon.dot.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EmployeeStatus {
  ACTIVE(1),
  INACTIVE(2);

  private final int priority;
}

