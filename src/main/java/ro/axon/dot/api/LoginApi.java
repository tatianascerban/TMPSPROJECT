package ro.axon.dot.api;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ro.axon.dot.model.request.LoginRequestDTO;
import ro.axon.dot.model.request.TokenRequestDTO;
import ro.axon.dot.model.response.LoginResponseDTO;
import ro.axon.dot.model.response.UserDetailsResponse;
import ro.axon.dot.service.LoginService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LoginApi {

  private final LoginService loginService;

  @PostMapping("login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequest) {
    var response = loginService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("refresh")
  public ResponseEntity<LoginResponseDTO> refresh(@RequestBody @Valid TokenRequestDTO refreshRequest){
    var refreshResponse = loginService.refresh(refreshRequest);
    return ResponseEntity.ok(refreshResponse);
  }

  @PostMapping("logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(@RequestBody @Valid TokenRequestDTO logoutRequestDTO){
    loginService.logout(logoutRequestDTO);
  }

  @GetMapping("user")
  public ResponseEntity<UserDetailsResponse> getUserDetails() {
    var detailsResponse = loginService.getLoggedUserDetails();
    return ResponseEntity.ok(detailsResponse);
  }

}
