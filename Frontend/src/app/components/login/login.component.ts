/**************************************************************************

   File:        login.component.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
  23.12.2023  Pasa Larisa           Refactoring
  10.12.2023  Pasa Larisa           Refactor code
  02.12.2023  Pasa Larisa           Added users diferentiate logic
  01.12.2023  Pasa Larisa           Added login logic

 **************************************************************************/
import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  /* CONSTRUCTOR */
  constructor(private authservice: AuthService, private route: Router) {
    localStorage.clear();
  }

  /* VARIABLES */
  messageclass = ''
  message = ''
  Customerid: any;
  editdata: any;
  responsedata: any;
  public http_code_error_message: any
  public showErrorMessage: boolean = false;

  /* FORMS */
  Login = new FormGroup({
    email: new FormControl("", Validators.required),
    password: new FormControl("", Validators.required)
  });

  /* LOGS */
  launchAdminApp = "You are admin.Open the App!";

  /* FUNCTIONS */
  ngOnInit(): void { }


  isAdmin(): boolean { return this.responsedata && this.responsedata.userObject && this.responsedata.userObject.roleEnum === 'ADMIN'; }
  launchDesktopApp() { this.authservice.launchDesktopApp(); }

   /**
   * Function to initiate the login process.
   * 
   * Checks if the login form is valid and calls the authentication service's ProceedLogin method with the login form's value.
   * Handles successful and failed login scenarios, storing necessary data in local storage.
   */
  ProceedLogin() {
    console.log("ProceedLogin function()");

    /* Check if the login form is valid*/
    if (this.Login.valid) {

      /* Call the authentication service's ProceedLogin method with the login form's value */
      this.authservice.ProceedLogin(this.Login.value).subscribe(

        /* On successful login */
        result => {
          console.log("Login successful.");

          /* Check if the result is not null */
          if (result != null) {

            /* Extract email from the login form and log it */
            var body_login = this.Login.value
            console.log("Body form : " + body_login);

            /** Store the response data in the component's responsedata property */
            this.responsedata = result;

            /** Store access_token and refresh_token in local storage */
            localStorage.setItem("access_token", this.responsedata.access_token);
            localStorage.setItem("refresh_token", this.responsedata.refresh_token);

            /** For editing user, store the email in local storage */
            if (body_login && body_login.email) {
              localStorage.setItem("email", body_login.email);
            }

            /** Retrieve user role from the server */
            this.authservice.GetUserRole(body_login.email).subscribe(

              /** On successful retrieval of user role */
              (userObject: any) => {
                console.log("GetUserRole(){ " + userObject.email + " , " + userObject.roleEnum + " }");

                /** Check if the user has an ADMIN role */
                if (userObject.roleEnum === 'ADMIN') {
                  const shouldLaunchDesktopApp = window.confirm(this.launchAdminApp);
                  /** If the user agrees, launch the desktop app */
                  if (shouldLaunchDesktopApp) {
                    this.launchDesktopApp();
                  }
                } else {
                  this.route.navigate(['home']);
                }
              },

              /* On error while retrieving user role */
              (error: any) => {
                this.http_code_error_message = error.message;
                this.authservice.handleError(error);
                this.hideErrorMessage();
                this.showErrorMessage = true;
              }
            );
          }
        },
        /* On error during login */
        (error: any) => {
          this.http_code_error_message = error.message;
          console.log(error);
          this.authservice.handleError(error);
          this.hideErrorMessage();
          this.showErrorMessage = true;
        }
      );

    }
  }

  /* COMMON FUNCTIONS  */
  /**
   * Function to hide error messages after a specific timeout.
   * 
   * Displays error messages for a brief period and then clears them.
   */
  hideErrorMessage() {
    this.showErrorMessage = true;
    setTimeout(() => {
      this.http_code_error_message = '';
      this.showErrorMessage = false;
    }, 5000);
  }


}

