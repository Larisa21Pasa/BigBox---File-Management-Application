/**************************************************************************

 File:        UsersService.java
 Copyright:   (c) 2023 NazImposter
 Description: Service interface responsible for managing User resources from database.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 11.11.2023  Larisa Pasa           Declare specific methods for user
 10.1.2023   Tudor Toporas        Plan id update

 **************************************************************************/

package com.paw.project.Components.AccessManagerComponent.IDMComponent;

import com.paw.project.Components.AccessManagerComponent.Authentication.ChangePasswordRequest;
import com.paw.project.Utils.Models.User;
import org.springframework.hateoas.EntityModel;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface UsersService {
    /*------------------------------------------ GET -----------------------------------------------------------*/
    User getUser(Integer UserId);
    List<User> getAllUsers();
    User getUserRoleByEmail(String email);

    Optional<User> findById(Integer userId);

    /*------------------------------------------  POST -----------------------------------------------------------*/
   User updateUserById(Integer UserId, User user);
   // User updateUserByEmail(String userEmail,User userEntity);
    User updateUserByEmail(String userEmail, String newUsername);
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    User updatePlanId(Integer userId, Integer planId);

    /*------------------------------------------  DELETE -----------------------------------------------------------*/
    void deleteUser(String userEmail);
}
