package ro.axon.dot.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ro.axon.dot.model.request.EmployeesDayOffUpdateRequest;
import ro.axon.dot.service.EmployeeService;

@WebMvcTest(EmployeeApi.class)
@ContextConfiguration(classes = {EmployeeApi.class})
@AutoConfigureMockMvc(addFilters = false)
class EmployeeApiTest {

  @Inject
  MockMvc mockMvc;
  @MockBean
  private EmployeeService employeeService;
  @Inject
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void updateEmployeeLeaveDays_success() throws Exception {
    doNothing().when(employeeService)
        .updateEmployeesDaysOff(isA(EmployeesDayOffUpdateRequest.class));

    mockMvc.perform(put("/api/v1/employees/days-off")
            .contentType(MediaType.APPLICATION_JSON).content("{\n"
                + "  \"employeeIds\": [ \"emplId1\", \"emplId10\"],\n"
                + "  \"noDays\": 10,\n"
                + "  \"type\": \"INCREASE\",\n"
                + "  \"description\": \"Numar de zile default\"\n"
                + "}\n"))
        .andExpect(status().isNoContent());

    verify(employeeService).updateEmployeesDaysOff(any(EmployeesDayOffUpdateRequest.class));
  }
}