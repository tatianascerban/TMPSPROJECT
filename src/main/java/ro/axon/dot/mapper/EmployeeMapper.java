package ro.axon.dot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ro.axon.dot.domain.entity.EmployeeEty;
import ro.axon.dot.model.request.EmployeeRequestDTO;
import ro.axon.dot.model.response.EmployeeDetailListItem;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface EmployeeMapper {

  EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "username", source = "username")
  @Mapping(target = "firstName", source = "firstname")
  @Mapping(target = "password", ignore = true)
  @Mapping(target = "lastName", source = "lastname")
  @Mapping(target = "crtUsr", ignore = true)
  @Mapping(target = "crtTms", ignore = true)
  @Mapping(target = "mdfUsr", ignore = true)
  @Mapping(target = "mdfTms", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "contractEndDate", ignore = true)
  @Mapping(target = "team", ignore = true)
  @Mapping(target = "daysOffEties", ignore = true)
  @Mapping(target = "leaveRequestEties", ignore = true)
  EmployeeEty mapEmployeeDtoToEmployeeEntity(EmployeeRequestDTO employeeRequestDTO);

  @Mapping(target = "totalVacationDays", ignore = true)
  @Mapping(target = "teamDetails",source = "employeeEty.team")
  EmployeeDetailListItem mapEtyToEmployeeDetailListItem(EmployeeEty employeeEty);

}
