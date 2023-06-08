package ro.axon.dot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.axon.dot.domain.entity.LeaveRequestEty;
import ro.axon.dot.model.request.LeaveRequestCreationRequest;
import ro.axon.dot.model.response.LeaveRequestDetailsListItem;
import ro.axon.dot.model.response.LeaveRequestResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface LeaveRequestMapper {

  LeaveRequestMapper INSTANCE = Mappers.getMapper(LeaveRequestMapper.class);

  @Mapping(source = "employee", target = "employeeDetails")
  @Mapping(source = "employee.id", target = "employeeDetails.employeeId")
  LeaveRequestDetailsListItem mapEtyToLeaveRequestDetailsListItem(LeaveRequestEty leaveRequestEty);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "crtUsr", ignore = true)
  @Mapping(target = "crtTms", ignore = true)
  @Mapping(target = "mdfUsr", ignore = true)
  @Mapping(target = "mdfTms", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "rejectReason", ignore = true)
  @Mapping(target = "noOfDays", ignore = true)
  @Mapping(target = "v", ignore = true)
  @Mapping(target = "employee", ignore = true)
  LeaveRequestEty mapLeaveRequestCreationRequestToEty(LeaveRequestCreationRequest leaveRequestCreationRequest);

  LeaveRequestResponse mapEtyToResponseDto(LeaveRequestEty leaveRequestEty);
}
