/**************************************************************************

 File:        SpecificUserAccessManager.java
 Copyright:   (c) 2023 NazImposter
 Description: Custom authorization logic for user access.
 Designed by: Toporas Tudor

 Module-History:
 Date        Author                Reason
 25.12.2023  Tudor Toporas          Users can now only access their own files

 **************************************************************************/

package com.paw.project.Utils.Others.Classes;

import com.paw.project.Components.FileManager.FileManagerService;
import com.paw.project.Utils.Models.User;
import com.paw.project.Utils.Repositories.UsersRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Log4j2
public class SpecificUserAccessManager implements AuthorizationManager<RequestAuthorizationContext> {
    private final FileManagerService entryService;
    private final UsersRepository userRepo;
    @Autowired
    public SpecificUserAccessManager(FileManagerService entryService, UsersRepository userRepo){
        this.entryService = entryService;
        this.userRepo = userRepo;
    }

    public AuthorizationDecision hasAccessToResources(Authentication auth, Integer ... entryId){
        UserDetails details = (UserDetails)auth.getPrincipal();
        Integer userId = userRepo.findByEmail(details.getUsername())
                .map(User::getUserId)
                .orElse(-1);
        log.info("Auth userId: " + userId + ", id of entries: " + Arrays.stream(entryId).toList());
        return new AuthorizationDecision(
                Arrays.stream(entryId)
                .map(it-> entryService.getUserIdOf(it).equals(userId))
                .reduce(true, Boolean::logicalAnd)
        );
    }

    public AuthorizationDecision isSameUser(Authentication auth, Integer userId){
        UserDetails details = (UserDetails)auth.getPrincipal();
        Integer authUserId = userRepo.findByEmail(details.getUsername())
                .map(User::getUserId)
                .orElse(-1);
        log.info("Checking auth id: " + authUserId + " with the one in path: " + userId);
        return new AuthorizationDecision(authUserId.equals(userId));
    }


    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
        Authentication auth = authentication.get();
        String URI = object.getRequest().getRequestURI();
        Matcher detailsM = Pattern
                        .compile("^/api/file_manager/[a-zA-Z/]+/userId/([0-9]+)$")
                        .matcher(URI),
                copyMoveM = Pattern
                        .compile("^/api/file_manager/entries/(copy|move)/([0-9]+)/parentId/([0-9]+)$")
                        .matcher(URI),
                othersM = Pattern
                        .compile("^/api/file_manager/entries/[a-zI/]*([0-9]+)[a-zI/]*$")
                        .matcher(URI);
        log.info("On URI: " + URI);
        if(detailsM.matches()){
            log.info("Checking is same user");
            return isSameUser(auth, Integer.parseInt(detailsM.group(1)));
        }
        if(copyMoveM.matches()){
            log.info("Checking 2 entries");
            return hasAccessToResources(
                    auth,
                    Integer.parseInt(copyMoveM.group(2)),
                    Integer.parseInt(copyMoveM.group(3))
            );
        }
        if(othersM.matches()){
            log.info("Checking one entry");
            return hasAccessToResources(
                    auth,
                    Integer.parseInt(othersM.group(1))
            );
        }
        log.info("Default no auth for file system access");
        return new AuthorizationDecision(false);
    }
}
