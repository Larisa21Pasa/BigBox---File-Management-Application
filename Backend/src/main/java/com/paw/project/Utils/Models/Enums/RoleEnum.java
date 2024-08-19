/**************************************************************************

 File:        RoleEnum.java
 Copyright:   (c) 2023 NazImposter
 Description: RoleEnum is an enum for roles in app.
 Designed by:  Pasa Larisa

 Module-History:
 Date        Author                Reason
 2.12.2023  Pasa Larisa            Simplified role handling for clarity and conciseness.
 21.11.2023  Pasa Larisa           Created for intern managing roles.
 **************************************************************************/
package com.paw.project.Utils.Models.Enums;


import com.paw.project.Utils.Models.Permission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public enum RoleEnum {
    USER,
    ADMIN;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new SimpleGrantedAuthority( "ROLE_" + this.name() );

        log.info("getAuthorities=> {}", authorities);
        return Collections.singletonList(authorities);
    }
}
