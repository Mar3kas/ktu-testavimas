package com.projektas.itprojektas.configs;

import com.projektas.itprojektas.model.Consultant;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomConsultantDetails implements UserDetails {

    private final Consultant consultant;

    public CustomConsultantDetails(Consultant consultant) {
        super();
        this.consultant = consultant;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(consultant.getRole().toString());
        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        return consultant.getPassword();
    }

    @Override
    public String getUsername() {
        return consultant.getUsername();
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
}
