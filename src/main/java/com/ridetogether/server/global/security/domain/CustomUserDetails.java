package com.ridetogether.server.global.security.domain;

import com.ridetogether.server.domain.member.domain.Member;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	public Member getMember() {
		return member;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles = new ArrayList<>();

//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//		return this.roles.stream()
//				.map(SimpleGrantedAuthority::new)
//				.collect(Collectors.toList());
//	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return AuthorityUtils.createAuthorityList(member.getRole().toString());
//		return null;
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getMemberId();
	}

	public String getMemberId() {return member.getMemberId();}

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
}
