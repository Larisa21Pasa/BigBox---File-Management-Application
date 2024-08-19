/**************************************************************************

   File:        auth.guard.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
   23.12.2023  Pasa Larisa           Refactoring
   01.12.2023  Pasa Larisa           Added access token based over resources with authentication guardian

 **************************************************************************/
import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';

/**
 * AuthGuard service to protect routes that require authentication.
 *
 * This service implements the CanActivate interface to control route activation based on the user's authentication status.
 * If the user is logged in, the route is allowed to activate; otherwise, the user is redirected to the login page.
 */
@Injectable({
   providedIn: 'root'
})
export class AuthGuard implements CanActivate {
   /* CONSTRUCTOR */

   constructor(private service: AuthService, private route: Router) { }
   /* FUNCTIONS */
   /**
    * Function to determine if a route can be activated.
    *
    * Checks if the user is logged in using the AuthService's IsLoggedIn method.
    * If logged in, allows activation; otherwise, redirects to the login page and denies activation.
    *
    * @returns True if the route can be activated, false otherwise.
    */
   canActivate() {
     // return true; //todo security overpass
      if (this.service.IsLoggedIn()) {
         return true;
      } else {
         this.route.navigate(['login'])
         return false;
      }
   }

}

