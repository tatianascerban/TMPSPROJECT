package ro.axon.dot.api;

import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.model.response.LeaveRequestDetailsList;
import ro.axon.dot.model.response.VacationResponse;
import ro.axon.dot.service.LeaveRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/requests")
public class LeaveRequestApi {

  private final LeaveRequestService leaveRequestService;

  @GetMapping
  public ResponseEntity<LeaveRequestDetailsList> getLeaveRequestDetailsList(
      @RequestParam(value = "status", required = false) String status,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "type", required = false) String type,
      @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(
        leaveRequestService.getLeaveRequestsDetails(status, search, type, startDate, endDate));
  }

  @GetMapping("by-period")
  public ResponseEntity<VacationResponse> getVacationRequestsByPeriod(
      @RequestParam(required = false) String team,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotEmpty LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NotEmpty LocalDate endDate) {

    var response = leaveRequestService.getVacationRequestsByPeriod(team, startDate, endDate);
    return ResponseEntity.ok(response);
  }
}