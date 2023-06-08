package ro.axon.dot.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import ro.axon.dot.configuration.RolesProperties;
import ro.axon.dot.model.response.RolesList;


@EnableConfigurationProperties(RolesProperties.class)
public class RoleService {

  private final RolesProperties rolesProperties;

  public RoleService(RolesProperties rolesProperties) {
    this.rolesProperties = rolesProperties;
  }

  private static RoleService roleService;

  public static RoleService getInstance(RolesProperties rolesProperties) {
    if(Objects.isNull(roleService)) {
      roleService = new RoleService(rolesProperties);
      return roleService;
    }
    return roleService;
  }

  public RolesList getRolesList() {
    return new RolesList(rolesProperties.getRoles());
  }
}