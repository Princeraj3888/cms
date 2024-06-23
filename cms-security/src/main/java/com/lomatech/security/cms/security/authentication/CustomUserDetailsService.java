package com.lomatech.security.cms.security.authentication;

import com.lomatech.security.cms.security.user.SecurityUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, SecurityUser> usersMap = new HashMap<>();

    public CustomUserDetailsService(BCryptPasswordEncoder bCryptPasswordEncoder){
        usersMap.put("user", createUser("user", bCryptPasswordEncoder.encode("userPass"), false, "USER"));
        usersMap.put("admin", createUser("admin", bCryptPasswordEncoder.encode("adminPass"), true, "ADMIN", "USER"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(usersMap.get(username))
                .orElseThrow(() -> new UsernameNotFoundException("User "+ username+ " does not exists"));
    }

    private SecurityUser createUser(String userName, String password, boolean withRestrictedPolicy, String... roles) {
        return SecurityUser.builder().withUserName(userName)
                .withPassword(password)
                .withGrantedAuthorityList(Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
                .hasRestrictedPolicy(withRestrictedPolicy);

    }

}
