/**************************************************************************

 File:        Permission.java
 Copyright:   (c) 2023 NazImposter
 Description: Permission Enum for granulate access for resources
 Designed by:  Pasa Larisa

 Module-History:
 Date        Author                Reason
 21.11.2023  Pasa Larisa           Created permissions for token authentication logic.
 **************************************************************************/
package com.paw.project.Utils.Models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    // TODO De sters -> am incercat sa simplific codul si sa ma axez pe lucrul cu roluri, nu cu permisiuni specifice
    ADMIN_CREATE("admin:create"),

    USER_READ_OWN_RESOURCE("user_own:read_own"),
    USER_CREATE_OWN_RESOURCE("user_own:create_own"),
    USER_UPDATE_OWN_RESOURCE("user_own:update_own"),
    USER_DELETE_OWN_RESOURCE("user_own:delete_own"),

    USER_READ_FILE_RESOURCE("user_file:read_file"),
    USER_CREATE_FILE_RESOURCE("user_file:create_file"),
    USER_UPDATE_FILE_RESOURCE("user_file:update_file"),
    USER_DELETE_FILE_RESOURCE("user_file:delete_file"),
    ADMIN_CREATE_OWN_RESOURCE("admin:open_desktop_app")

    ;

    @Getter
    private final String permission;
}
