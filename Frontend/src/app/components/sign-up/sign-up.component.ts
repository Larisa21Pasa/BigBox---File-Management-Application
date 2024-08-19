/**************************************************************************

   File:        sign-up.component.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
   23.12.2023  Pasa Larisa           Refactoring
   01.12.2023  Pasa Larisa           Added sign up functionality

 **************************************************************************/
import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {
  /* CONSTRUCTOR */
  constructor(private service: AuthService, private route: Router) {
    localStorage.clear();
  }

  /* VARIABLES */
  http_code_error_message: any;
  public showErrorMessage: boolean = false;

  /* FORMS */
  Signup = new FormGroup({
    name: new FormControl("", Validators.required),
    email: new FormControl("", Validators.required),
    password: new FormControl("", Validators.required)
  });


  /* LOGS */

  /* FUNCTIONS */
  ngOnInit(): void { }

  /**
   * Function to handle the signup process.
   * 
   * Checks if the signup form is valid and calls the signup service's ProceedSignup method with the signup form data.
   * Handles successful signup and errors.
   */
  ProceedSignup() {
    /** Check if the signup form is valid */
    if (this.Signup.valid) {
      /** Extract signup form data */
      const body_signup = this.Signup.value;

      /** Call the signup service's ProceedSignup method with the signup form data */
      this.service.ProceedSignup(body_signup).subscribe(
        /** On successful signup */
        (result: any) => {
          /** Check if the result is not null */
          if (result != null) {

            /** Extract login form data for further use */
            var body_login = this.Signup.value;

            /** Extract response data */
            const responsedata = result;

            /** Store access_token and refresh_token in local storage */
            localStorage.setItem('access_token', responsedata.access_token);
            localStorage.setItem('refresh_token', responsedata.refresh_token);
            /** For editing user, store the email in local storage */
            if (body_login && body_login.email) {
              localStorage.setItem("email", body_login.email);
            }
            /** Navigate to the home route */
            this.route.navigate(['home']);

          }
        },
        /** On error during signup */
        (error: any) => {
          this.http_code_error_message = error.message;
          console.log(error);
          this.showErrorMessage = true;
          this.hideErrorMessage();
        }
      );
    }
  }

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
