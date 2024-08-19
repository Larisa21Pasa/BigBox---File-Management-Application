/**************************************************************************

 File:        UsersServiceImplementation.java
 Copyright:   (c) 2023 NazImposter
 Description: Service implementation responsible for managing User resources from database.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 11.12.2023  Larisa Pasa           Added change password logic.
 11.11.2023  Larisa Pasa           Define specific methods for user -> not final
 10.1.2023   Tudor Toporas        Plan id update

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.IDMComponent;

import com.paw.project.Components.AccessManagerComponent.Authentication.ChangePasswordRequest;
import com.paw.project.Utils.Exceptions.ChangePasswordException;
import com.paw.project.Utils.Exceptions.NotAcceptableException;
import com.paw.project.Utils.Exceptions.NotFoundException;
import com.paw.project.Utils.Exceptions.UnprocessableContentException;
import com.paw.project.Utils.Models.User;
import com.paw.project.Utils.Repositories.PlansRepository;
import com.paw.project.Utils.Repositories.SubscriptionsRepository;
import com.paw.project.Utils.Repositories.UsersRepository;
import com.paw.project.Utils.Validators.EmailValidator;
import com.paw.project.Utils.Validators.PasswordValidator;
import com.paw.project.Utils.Validators.StringValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.*;

@Service
@RequiredArgsConstructor

@Transactional
@Slf4j
public class UsersServiceImplementation implements UsersService {
    /*------------------------------------------ VARIABLES  -----------------------------------------------------------*/

    @Autowired
    private final UsersRepository usersRepository;
    @Autowired
    private final SubscriptionsRepository subscriptionsRepository;
    @Autowired
    private final PlansRepository plansRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator validPassword= new PasswordValidator();
    private final EmailValidator emailValidator=new EmailValidator();
    private final StringValidator stringValidator=new StringValidator();



    /*------------------------------------------ GET  -----------------------------------------------------------*/
    public User getUser(Integer UserId) {
        log.info("Fetching user {} .", UserId);
        return usersRepository.findByUserId(UserId);
    }

    public List<User> getAllUsers() {
        log.info("Fetching all users.");
        return usersRepository.findAll();
    }
    public Optional<User> getUserByEmail(String userEmail) {
        System.out.println("Fetching user by email: {}"+ userEmail);

        Optional<User> userOptional = usersRepository.findByEmail(userEmail);

        if (userOptional.isPresent()) {
            System.out.println("Found user: {}"+ userOptional.get().getUsername());
        } else {
            throw  new RuntimeException("User not found for email: {}"+ userEmail);
        }

        return userOptional;
    }
    public User getUserRoleByEmail(String email) {
        return usersRepository.findUserByEmail(email);
    }

    @Override
    public Optional<User> findById(Integer userId) {
        return usersRepository.findById(userId);
    }


    public User updateUserById(Integer idUser,User userEntity) {
        /* Check resource to be existent */
        if(null == userEntity.getUserId())
        {
            throw new NotFoundException();
        }

        /*
         * After confirming the existence of a patient to work with,
         *  further validation is performed on the rest of the fields.
         */
        if(
                null == userEntity.getEmail()
                        || null == userEntity.getUsername()
                        || null == userEntity.getPassword()
        )
        {
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }

        /*
         * After ensuring that all fields are present and a 406 error is not required,
         *  the validity of the field values is checked.
         * If any of the fields is not valid, an Unprocessable Content Exception is thrown.
         */
        if( !stringValidator.isValid(userEntity.getUsername(), null)
                        || !emailValidator.isValid(userEntity.getEmail(), null)
                        || !validPassword.isValid(userEntity.getPassword(), null)
        )
        {
            throw new UnprocessableContentException(UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS);
        }

        try{
            log.info("not delete doctor and create updated");
            usersRepository.save(userEntity);

        }catch (DataAccessException dataAccessException)
        {
            throw  new RuntimeException(UPDATE_ERROR + dataAccessException.getRootCause());
        }

        return usersRepository.findByUserId(userEntity.getUserId());
    }

    /*------------------------------------------ POST  -----------------------------------------------------------*/

    public User updateUserByEmail(String userEmail, String newUsername) {
        log.info("SERVICE  updateUserByEmail => {}", newUsername);
        /* Check resource to be existent */
        User userExistent = usersRepository.findUserByEmail(userEmail);
        if(null == userExistent)
        {
            log.info("not existent");
            throw new NotFoundException();
        }

        /*
         * After confirming the existence of a user to work with,
         *  further validation is performed on the username.
         */
        if( null == newUsername )
        {
            log.info("username null");
            throw new NotAcceptableException(NOT_ACCEPTABLE_EXCEPTION);
        }
        log.info("Value of newUsername: {}", newUsername);

        /*
         * After ensuring that all field newUsername are present and a 406 error is not throwed,
         *  the validity of the field values is checked.
         * If field is not valid, an Unprocessable Content Exception is thrown.
         */
        /* IF NOT */
        if( !stringValidator.isValid(newUsername, null) )
        {
            log.info("username invalid");
            throw new UnprocessableContentException(UNPROCESSABLE_CONTENT_EXCEPTION_FIELDS);
        }
        log.info("userExistent berofre {} ", userExistent);
        userExistent.setName(newUsername);
        log.info("userExistent after {} ", userExistent);

        try{
            usersRepository.save(userExistent);

        }catch (DataAccessException dataAccessException)
        {
            throw  new RuntimeException(UPDATE_ERROR + dataAccessException.getRootCause());
        }

        return usersRepository.findByUserId(userExistent.getUserId());
    }



    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
    log.info("SERVICE changePassword");
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ChangePasswordException(PASSWORD_CHANGE_EXCEPTION_NOT_SAME_CURRENT);
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new ChangePasswordException(PASSWORD_CHANGE_EXCEPTION_NOT_SAME_NEW);
        }

        // update the password
        user.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        usersRepository.save(user);
    }

    @Override
    public User updatePlanId(Integer userId, Integer planId) {
        return usersRepository.save(usersRepository.findById(userId).map(it ->
                it.toBuilder()
                        .subscription(
                                subscriptionsRepository.save(
                                        it.getSubscription().toBuilder()
                                                .plan(plansRepository.findById(planId)
                                                        .orElseThrow(()->new NotFoundException("Plan not found")))
                                                .build()
                                )
                        ).build()
        ).orElseThrow(()->new NotFoundException("User not found")));
    }

    /*------------------------------------------ DELETE  -----------------------------------------------------------*/
    @Override
    public void deleteUser(String userEmail) {
        User user = usersRepository.findUserByEmail(userEmail);
        user.setIsBlocked(true);
        usersRepository.save(user);

    }
}
