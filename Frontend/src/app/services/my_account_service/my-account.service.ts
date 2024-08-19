/**************************************************************************

   File:        my-account.service.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
   23.12.2023  Pasa Larisa           Refactoring
   05.12.2023  Pasa Larisa           Added functionalities for my-account page

 **************************************************************************/

   import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
   import { Injectable } from '@angular/core';
   import { Router } from '@angular/router';
   import { Observable, catchError, throwError } from 'rxjs';
   import { AuthService } from '../auth_service/auth.service';
   import { ChangePasswordRequest } from 'src/app/models/change-password';

   @Injectable({
     providedIn: 'root'
   })
   export class MyAccountService {
     /* CONSTRUCTOR */
     constructor(private authservice: AuthService, private http: HttpClient, private router: Router) {
     }

     /* VARIABLES */
     get_user_by_email_path = 'http://localhost:8080/api/users/user';
     update_user_path = 'http://localhost:8080/api/users/username';
     change_password_path = 'http://localhost:8080/api/users/password';
     deactivate_account_path = 'http://localhost:8080/api/users/deactivate';

     /* FUNCTIONS */

     /**
       * Function to handle the edit username process.
       *
       * This function initiates the process of editing the username by sending a POST request to the server.
       * It takes the user's email and the new username as parameters.
       *
       * @param email - The email of the user whose username is to be edited.
       * @param newUsername - The new username to be set.
       * @returns An Observable with the server response.
     */
     ProceedEditUser(email: any, newUsername: any): Observable<any> {
       console.log("ProceedEditUser()");
       return this.http
         .post(`${this.update_user_path}/${email}`, newUsername)
         .pipe(
           catchError(this.handleError)
         );
     }

     /**
     * Function to handle the change password process.
     *
     * This function initiates the process of changing the user's password by sending a POST request to the server.
     * It takes the user's email, current password, new password, and confirmation password as parameters.
     *
     * @param email - The email of the user whose password is to be changed.
     * @param currentPasswordValue - The current password.
     * @param newPasswordValue - The new password to be set.
     * @param confirmationPasswordValue - The confirmation of the new password.
     * @returns An Observable with the server response.
     */
     ProceedChangePassword(email: any, currentPasswordValue: any, newPasswordValue: any, confirmationPasswordValue: any): Observable<any> {
       console.log("ProceedChangePassword()");

       const fields: ChangePasswordRequest = {
         currentPassword: currentPasswordValue,
         newPassword: newPasswordValue,
         confirmationPassword: confirmationPasswordValue
       };

       return this.http
         .post(`${this.change_password_path}/${email}`, fields)
         .pipe(
           catchError(this.handleError)
         );
     }

     /**
      * Function to handle the deactivate account process.
      *
      * Initiates the process of deactivating a user account by sending a DELETE request to the server.
      *
      * @param email - The email of the user account to be deactivated.
      * @returns An Observable with the server response.
      */
     ProceedDeactivateAccount(email: any): Observable<any> {
       console.log("ProceedDeactivateAccount()");
       return this.http
         .delete(`${this.deactivate_account_path}/${email}`)
         .pipe(
           catchError(this.handleError)
         );
     }

     /**
      * Function to retrieve user information by email.
      *
      * @param email - The email of the user.
      * @returns An Observable with the server response containing user information.
      */
     GetUser(email: any) {
       return this.http.get(`${this.get_user_by_email_path}/${email}`)
     }

     /**
      * Private function to get HttpHeaders with the Authorization header containing the Bearer token.
      *
      * @returns HttpHeaders object with Authorization header.
      */
     private getHeaders(): HttpHeaders {
       return new HttpHeaders().set('Authorization', 'Bearer ' + this.authservice.GetToken());
     }

     /**
      * Error handling function for HTTP requests.
      *
      * @param error - The HttpErrorResponse object.
      * @returns An Observable with a custom error message and HTTP status code.
      */
     handleError(error: HttpErrorResponse) {

       let http_code_error_message = '';
       if (error.status === 0) {
         console.error('An error occurred:', error.error);
       } else {
         http_code_error_message = `${error.error}`;
       }
       return throwError(() => ({
         message: http_code_error_message,  // Include the server error message
         status: error.status  // Include the HTTP status code
       }));
     }
   }

