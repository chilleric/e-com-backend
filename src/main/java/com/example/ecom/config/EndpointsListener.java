package com.example.ecom.config;

import static java.util.Map.entry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.example.ecom.repository.feature.Feature;
import com.example.ecom.repository.feature.FeatureRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.utils.DateFormat;

@Component
public class EndpointsListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username}")
    protected String email;

    @Value("${default.password}")
    protected String defaultPassword;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        List<String> paths = new ArrayList<>();
        applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods()
                .forEach((key, value) -> {
                    Set<String> pathList = key.getDirectPaths();
                    for (String path : pathList) {
                        paths.add(path);
                        List<Feature> features = featureRepository
                                .getFeatures(Map.ofEntries(entry("path", path)), "", 0, 0, "").get();
                        if (features.size() == 0) {
                            Feature feature = new Feature(null, value.toString(), path, 0);
                            featureRepository.insertAndUpdate(feature);
                        }
                    }
                });
        List<User> users = userRepository.getUsers(Map.ofEntries(entry("username", "super_admin")), "", 0, 0, "").get();
        User user = new User();
        if (users.size() == 0) {
            user = new User(new ObjectId(), "super_admin",
                    bCryptPasswordEncoder.encode(Base64.getEncoder().encodeToString(defaultPassword.getBytes())), 0,
                    "", "", "Super", "Admin", email, "", new HashMap<>(), DateFormat.getCurrentTime(), null, true,
                    false,
                    0);
            userRepository.insertAndUpdate(user);
        } else {
            user = users.get(0);
        }
        List<Permission> permissions = permissionRepository
                .getPermissions(Map.ofEntries(entry("name", "super_admin_permission")), "", 0, 0, "").get();
        if (permissions.size() == 0) {
            List<ObjectId> features = featureRepository.getFeatures(new HashMap<>(), "", 0, 0, "").get().stream()
                    .map(feature -> feature.get_id()).collect(Collectors.toList());
            List<ObjectId> userIds = Arrays.asList(user.get_id());
            Permission permission = new Permission(null, "super_admin_permission", userIds, features,
                    DateFormat.getCurrentTime(), null, false, 0);
            permissionRepository.insertAndUpdate(permission);
        } else {
            List<ObjectId> features = featureRepository.getFeatures(new HashMap<>(), "", 0, 0, "").get().stream()
                    .map(feature -> feature.get_id()).collect(Collectors.toList());
            List<ObjectId> userIds = Arrays.asList(user.get_id());
            Permission permission = permissions.get(0);
            permission.setFeatureId(features);
            permission.setUserId(userIds);
            permissionRepository.insertAndUpdate(permission);
        }
        List<Permission> permissionsDefault = permissionRepository
                .getPermissions(Map.ofEntries(entry("name", "default_permission")), "", 0, 0, "").get();
        if (permissionsDefault.size() == 0) {
            Permission defaultPermission = new Permission(null, "default_permission", new ArrayList<>(),
                    new ArrayList<>(),
                    DateFormat.getCurrentTime(), null, false, 0);
            permissionRepository.insertAndUpdate(defaultPermission);
        } else {
            permissionRepository.insertAndUpdate(permissionsDefault.get(0));
        }
        featureRepository.getFeatures(new HashMap<>(), "", 0, 0,
                "").get().forEach(featureCheck -> {
                    boolean needDelete = true;
                    for (String path : paths) {
                        if (featureCheck.getPath().compareTo(path) == 0) {
                            needDelete = false;
                            break;
                        }
                    }
                    if (needDelete) {
                        featureRepository.deleteFeature(featureCheck.get_id().toString());
                        permissionRepository.getPermissions(new HashMap<>(), "", 0, 0, "").get()
                                .forEach(perm -> {
                                    List<ObjectId> updateFeature = perm.getFeatureId().stream()
                                            .filter(fea -> fea != featureCheck.get_id()).collect(Collectors.toList());
                                    perm.setFeatureId(updateFeature);
                                    permissionRepository.insertAndUpdate(perm);
                                });
                    }
                });
    }
}
