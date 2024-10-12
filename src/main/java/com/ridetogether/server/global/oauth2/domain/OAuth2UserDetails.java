package com.ridetogether.server.global.oauth2.domain;

import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.global.oauth2.model.SocialType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Builder
public class OAuth2UserDetails implements UserDetails {

    @Getter
    private SocialType socialType;
    @Getter
    private String memberId;
    @Getter
    private String email;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateAuthorities(Member member) {
        this.authorities = AuthorityUtils.createAuthorityList(member.getRole().toString());
    }
}
