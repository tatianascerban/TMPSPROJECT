package ro.axon.dot.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.configuration.RolesProperties;
import ro.axon.dot.model.response.LegallyDaysOffList;
import ro.axon.dot.service.LegallyDaysOffService;
import ro.axon.dot.validation.PeriodFormat;
import ro.axon.dot.validation.YearFormat;
import ro.axon.dot.model.response.RolesList;
import ro.axon.dot.service.RoleService;

@RestController
@RequestMapping("/api/v1/misc")
@Validated
@Slf4j
public class MiscApi {

  private final LegallyDaysOffService legallyDaysOffService;
  private final RoleService roleService;

  public MiscApi(LegallyDaysOffService legallyDaysOffService, RolesProperties rolesProperties) {
    this.legallyDaysOffService = legallyDaysOffService;
    this.roleService = RoleService.getInstance(rolesProperties);
  }

  @GetMapping("/legally-days-off")
  public ResponseEntity<LegallyDaysOffList> getLegallyDaysOff(
      @RequestParam(required = false) @YearFormat String[] years,
      @RequestParam(required = false) @PeriodFormat String[] periods
      ) {
    log.trace("Get legally days off.");
    return ResponseEntity.ok(legallyDaysOffService.getLegallyDaysOff(years, periods));
  }

  @GetMapping("roles")
  public RolesList getRoles() {
    return roleService.getRolesList();
  }

}
