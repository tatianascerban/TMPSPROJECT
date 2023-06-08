package ro.axon.dot.api;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.model.request.EmployeeRequestDTO;
import ro.axon.dot.model.request.EmployeeUpdateRequest;
import ro.axon.dot.model.request.EmployeesDayOffUpdateRequest;
import ro.axon.dot.model.request.LeaveRequestCreationRequest;
import ro.axon.dot.model.request.LeaveRequestUpdate;
import ro.axon.dot.model.response.EmployeeDetailList;
import ro.axon.dot.model.request.LeaveRequestReview;
import ro.axon.dot.model.response.EmployeeRemainingDaysOfCurrentYear;
import ro.axon.dot.model.response.LeaveRequestDetailsList;
import ro.axon.dot.service.EmployeeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/employees")
public class EmployeeApi {

  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<EmployeeDetailList> getEmployeeDetails(
      @RequestParam(required = false) String name) {
    return ResponseEntity.ok(employeeService.getEmployeeDetails(name));
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public void createEmployee(@RequestBody @Valid EmployeeRequestDTO employeeRequest) {
    employeeService.createEmployee(employeeRequest);
  }

  @PatchMapping("/{employeeId}")
  public ResponseEntity<Void> updateEmployee(@Size(min = 1) @PathVariable String employeeId,
      @Valid @RequestBody EmployeeUpdateRequest employeeUpdateRequest) {
    employeeService.updateEmployee(employeeId, employeeUpdateRequest);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PatchMapping("/{employeeId}/inactivate")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void inactivateEmployee(@PathVariable String employeeId) {
    employeeService.inactivateEmployee(employeeId);
  }

  @GetMapping("/{employeeId}/requests")
  public ResponseEntity<LeaveRequestDetailsList> getEmployeeLeaveRequests(
      @Size(min = 1) @PathVariable String employeeId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(employeeService.getLeaveRequests(employeeId, startDate, endDate));
  }

  @PostMapping("/{employeeId}/requests")
  public ResponseEntity<Void> postLeaveRequest(
      @Size(min = 1) @PathVariable String employeeId,
      @Valid @RequestBody LeaveRequestCreationRequest leaveRequestCreationRequest) {
    employeeService.createLeaveRequest(employeeId, leaveRequestCreationRequest);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PutMapping("/{employeeId}/requests/{requestId}")
  public ResponseEntity<Void> updateLeaveRequest(
      @Size(min = 1) @PathVariable String employeeId,
      @Size(min = 1) @PathVariable Long requestId,
      @Valid @RequestBody LeaveRequestUpdate leaveRequestUpdate) {
    employeeService.updateEmployeeLeaveRequest(employeeId, requestId, leaveRequestUpdate);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("{employeeId}/requests/{requestId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteLeaveRequest(@PathVariable("employeeId") String emplployeeId, @PathVariable("requestId") Long requestId){
    employeeService.deleteLeaveRequest(emplployeeId, requestId);
  }

  @PutMapping("days-off")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateEmployeeLeaveDays(
      @RequestBody EmployeesDayOffUpdateRequest dayOffUpdateRequest) {
    employeeService.updateEmployeesDaysOff(dayOffUpdateRequest);
  }

  @PatchMapping("{employeeId}/requests/{requestId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void reviewLeaveRequest(@PathVariable String employeeId, @PathVariable Long requestId,
      @Valid @RequestBody LeaveRequestReview leaveRequestReview) {
    employeeService.reviewLeaveRequest(employeeId, requestId, leaveRequestReview);
  }

  @GetMapping("{employeeId}/remaining-days-off")
  public ResponseEntity<EmployeeRemainingDaysOfCurrentYear> getRemainingDaysOff(
      @PathVariable String employeeId) {
    var response = employeeService.getRemainingDaysOff(employeeId);
    return ResponseEntity.ok(response);
  }
}
