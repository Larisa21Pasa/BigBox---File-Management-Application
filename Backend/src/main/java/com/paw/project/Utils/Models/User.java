/**************************************************************************

 File:        User.java
 Copyright:   (c) 2023 NazImposter
 Description: Model class for User entity related operations.
 Designed by: Sebastian Pitica

 Module-History:
 Date        Author                Reason
 07.11.2023  Sebastian Pitica      Basic structure
 08.11.2023  Sebastian Pitica      Added extra fields and constraints
 16.11.2023  Sebastian Pitica      Remove @min annotation from id field
 19.11.2023  Sebastian Pitica      Remove @toString annotation and add @builder annotation
 19.11.2023  Toporas Tudor         Added option toBuilder = true to @Builder
 11.11.2023  Pasa Larisa           Added UserDetails interface to get a Spring User from table Users
 21.11.2023  Pasa Larisa           Refactor for roles and token logic

 **************************************************************************/

package com.paw.project.Utils.Models;

import com.paw.project.Utils.Models.Enums.RoleEnum;
import com.paw.project.Utils.Validators.ValidString;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
//@ToString
@ToString(exclude = {"tokens", "subscription"})

@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "User")
@Table(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic
    @Column(name = "user_id")
    private Integer userId;

    @ValidString
    @Basic
    @Column(name = "name")
    private String name;

    @NotNull
    @Basic
    @Column(name = "hashed_password")
    private String hashedPassword;

    @Email
    @Basic
    @Column(name = "email",
            unique = true)
    private String email;

    @NotNull
    @Basic
    @Column(name = "blocked_0_1")
    private Boolean isBlocked ;


    @OneToOne
    @JoinColumn(name = "subscription_id", referencedColumnName = "subscription_id")
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    private RoleEnum roleEnum;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    /* Methods from UserDetails */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleEnum.getAuthorities();
    }
    /* Necessary for authentication part */
    @Override
    public String getUsername() { return email; }
    public String getUsernameReal() { return name; }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /* Method that indicates whether the user account is locked or not.
       This method is used by the authentication system to check if the user account is locked. */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /* Method that indicates whether the user is enabled or disabled.
       This method is used by the authentication system to check if the user is enabled. */
    @Override
    public boolean isEnabled() {
        return true;
    }
}

