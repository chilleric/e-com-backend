package com.example.ecom.config;

import static java.util.Map.entry;

import com.example.ecom.repository.language.Language;
import com.example.ecom.repository.language.LanguageRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.utils.DateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LanguageRepository languageRepository;

  @Value("${spring.mail.username}")
  protected String email;

  @Value("${default.password}")
  protected String defaultPassword;

  private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
//    ApplicationContext applicationContext = event.getApplicationContext();
//    List<String> paths = new ArrayList<>();
//    applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods()
//        .forEach((key, value) -> {
//
//        });

    List<User> users = userRepository.getUsers(Map.ofEntries(entry("username", "super_admin")), "",
        0, 0, "").get();
    User user = new User();
    if (users.size() == 0) {
      user = new User(new ObjectId(), "super_admin",
          bCryptPasswordEncoder.encode(
              Base64.getEncoder().encodeToString(defaultPassword.getBytes())), 0,
          "", "", "Super", "Admin", email, "", new HashMap<>(), DateFormat.getCurrentTime(), null,
          true,
          false,
          0);
      userRepository.insertAndUpdate(user);
    } else {
      user = users.get(0);
    }
    List<Permission> permissions = permissionRepository
        .getPermissions(Map.ofEntries(entry("name", "super_admin_permission")), "", 0, 0, "")
        .get();
    if (permissions.size() == 0) {
      List<ObjectId> userIds = Arrays.asList(user.get_id());
      Permission permission = new Permission(null, "super_admin_permission", userIds,
          DateFormat.getCurrentTime(), null, permissionRepository.getViewPointSelect(),
          permissionRepository.getViewPointSelect());
      permissionRepository.insertAndUpdate(permission);
    } else {
      Permission permission = permissions.get(0);
      permission.setViewPoints(permissionRepository.getViewPointSelect());
      permission.setEditable(permissionRepository.getViewPointSelect());
      permissionRepository.insertAndUpdate(permission);
    }
    List<Language> defLanguages = languageRepository.getLanguages(Map.ofEntries(entry("key", "en")),
            "", 0, 0, "")
        .get();
    if (defLanguages.size() == 0) {
      Language defLanguage = new Language(null, "English", "en", new HashMap<>());
      languageRepository.insertAndUpdate(defLanguage);
    }
  }
}
