/**************************************************************************

 File:        UsersController.java
 Copyright:   (c) 2023 NazImposter
 Description: Controller responsible for managing User resources via URL mapping.
 Designed by: Pasa Larisa

 Module-History:
 Date        Author                Reason
 22.12.2023  Larisa Pasa           Just refactor code
 10.12.2023  Larisa Pasa           Added change update logic for username and password.
 11.12.2023  Larisa Pasa           Added change password logic.
 02.12.2023  Pasa Larisa          Added [ getRoleByUserEmail ] for Angular Role Management.
 11.11.2023  Pasa Larisa          Initial creation of the User resource controller.
 12.12.2023  Tudor Toporas        Added optional filter for all users query
 10.1.2023   Tudor Toporas        Plan id update

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.IDMComponent;

import com.paw.project.Components.AccessManagerComponent.Authentication.ChangePasswordRequest;
import com.paw.project.Utils.Exceptions.ConflictException;
import com.paw.project.Utils.Exceptions.JwtSignatureException;
import com.paw.project.Utils.Exceptions.NotAcceptableException;
import com.paw.project.Utils.Models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.*;
import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.NOT_ACCEPTABLE_EXCEPTION;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class UsersController {

    /*------------------------------------------ VARIABLES -----------------------------------------------------------*/
    private final UsersServiceImplementation usersService;

    /*------------------------------------------ GET -----------------------------------------------------------*/
    @GetMapping
    public ResponseEntity<List<User>> getUsers(@RequestParam(value = "name", required = false)String name,
                                               @RequestParam(value = "isBlocked", required = false)Boolean isBlocked)
    {
        try{
            log.info("getUsers");
            return new ResponseEntity<>(usersService.getAllUsers().stream()
                    .filter(it -> name == null ||
                            it.getUsername().toLowerCase()
                                    .contains(name.toLowerCase()))
                    .filter(it -> isBlocked == null ||
                            it.getIsBlocked().equals(isBlocked))
                    .toList(),
                    HttpStatus.OK);
        }catch (JwtSignatureException e){
            throw new JwtSignatureException("REFRESH");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Integer userId) {
        Optional<User> userOptional = Optional.ofNullable(usersService.getUser(userId));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String userEmail) {
        Optional<User> userOptional = usersService.getUserByEmail(userEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info("RETURNEZ USERUL CATRE MYACCOUNT => {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/role/{email}")
    public ResponseEntity<User> getRoleByUserEmail(@PathVariable String email) {
        Optional<User> userOptional = Optional.ofNullable(usersService.getUserRoleByEmail(email));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*------------------------------------------ POST -----------------------------------------------------------*/
    @PostMapping("/username/{userEmail}")
    public ResponseEntity<User> update(
            @PathVariable String userEmail,
            @RequestBody String  newUsername
    )
    {
        log.info("UPDATE USER CONTROLLER ");
        if(null == newUsername) {
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }
        User newUser = usersService.updateUserByEmail(userEmail, newUsername);
        return new ResponseEntity(newUser, HttpStatus.NO_CONTENT);
    }
    @PostMapping("/password/{userEmail}")
    public ResponseEntity<User> updatePassword(
            @PathVariable String userEmail,
            @RequestBody ChangePasswordRequest  changePasswordRequest,
            Principal connectedUser
    )
    {
        log.info("UPDATE USER CONTROLLER ");
        if(null == changePasswordRequest) {
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }
        usersService.changePassword(changePasswordRequest, connectedUser);
        Optional<User> newUser = usersService.getUserByEmail(userEmail);
        return new ResponseEntity(newUser, HttpStatus.NO_CONTENT);
    }

    /*------------------------------------------ PUT -----------------------------------------------------------*/
    @PutMapping("/{userId}")
    public ResponseEntity<User> update(@PathVariable Integer userId,@RequestBody User user)
    {
        if(null == user.getUserId()) {
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }
        /* IF NOT*/
        if (!user.getUserId().equals(userId)) {
            return new ResponseEntity(CONFLIC_EXCEPTION_NOT_SAME_ID, HttpStatus.CONFLICT);
        }

        log.info("Patient existing, updating ... ");
        User newUser = usersService.updateUserById(userId,user);
        return new ResponseEntity(newUser, HttpStatus.NO_CONTENT);
    }

    /*------------------------------------------ PATCH -----------------------------------------------------------*/

    @PostMapping("/{userId}/plan")
    public ResponseEntity<User> updatePlan(@PathVariable Integer userId, @RequestBody Integer planId){
        return new ResponseEntity<>(usersService.updatePlanId(userId, planId), HttpStatus.OK);
    }

    /*------------------------------------------  DELETE -----------------------------------------------------------*/
    @DeleteMapping("/deactivate/{userEmail}")
    public ResponseEntity<?> deleteUser(
            @PathVariable String userEmail
    )
    {
        log.info("[{}] -> DELETE, deleteUser: userEmail:{},", this.getClass().getSimpleName(), userEmail);

        try {
            usersService.deleteUser(userEmail);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotAcceptableException notAcceptableException) {
            return new ResponseEntity<>(notAcceptableException.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        } catch (ConflictException conflictException) {
            return new ResponseEntity<>(conflictException.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
