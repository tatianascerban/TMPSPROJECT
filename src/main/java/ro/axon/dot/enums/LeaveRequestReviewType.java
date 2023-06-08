package ro.axon.dot.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LeaveRequestReviewType {
  APPROVAL,
  REJECTION;
}
