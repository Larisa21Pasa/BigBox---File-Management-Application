/**************************************************************************

 File:        AdminController.java
 Copyright:   (c) 2023 NazImposter
 Description: Configuration class for security settings in the Access Manager component.
 Designed by: Larisa Pasa

 Module-History:
 Date        Author                Reason
 21.11.2023  Larisa Pasa           Create logic for ADMIN path => just launch desktop app.
 12.12.2023  Tudor Toporas         Block/Unblock user

 **************************************************************************/
package com.paw.project.Components.AccessManagerComponent.Controllers;

import com.paw.project.Components.AccessManagerComponent.IDMComponent.UsersServiceImplementation;
import com.paw.project.Utils.Exceptions.NotFoundException;
import com.paw.project.Utils.Models.User;
import com.paw.project.Utils.Repositories.UsersRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import static com.paw.project.Utils.Others.Classes.AccessManagerMacros.NOT_FOUND_MESSAGE;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UsersServiceImplementation usersService;
    @Autowired
    private UsersRepository usersRepository;

  
    @GetMapping("/launchDesktopApp")
    public ResponseEntity<Map<String,String>> launchDesktopApp() {
        log.info("Admin call launchDesktopApp");
        Map<String, String> response = new HashMap<>();
        log.info("alooooooo");
        response.put("message", "Se deschide aplicatia desktop ... ");

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<?> updateIsBlocked(@PathVariable Integer userId,@RequestBody Boolean isBlocked){

        User usr=usersRepository.findByUserId(userId);
        usr.setIsBlocked(isBlocked);
        return  new ResponseEntity<>(usersRepository.save(usr),HttpStatus.OK);

      }

}
