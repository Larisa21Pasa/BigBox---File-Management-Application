/**************************************************************************

   File:        my-account.component.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
  10.12.2023  Pasa Larisa           Added logig for editing option, render user plan, deactivate account option.

 **************************************************************************/
import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, ValidationErrors} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';
import {MyAccountService} from 'src/app/services/my_account_service/my-account.service';

@Component({
  selector: 'app-my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent {
  /* CONSTRUCTOR */
  constructor(private myAccountService: MyAccountService, private authService: AuthService, private router: Router) { }

  /* VARIABLES */
  currentUser: any;
  public http_code_error_message: any
  public showErrorMessage: boolean = false;
  emailLocalstorage = localStorage.getItem("email");
  username: any;
  newUsername: any;


  /* FORMS */
  Edit = new FormGroup({
    name: new FormControl(""),
    current_password: new FormControl(""),
    new_password: new FormControl(""),
    confirm_password: new FormControl("", [this.passwordMatchValidator.bind(this)])
  });
  /* LOGS */
  not_all_password_fiels_completed = "Please fill in all password fields";
  new_password_not_matching_with_confirm = "Passwords do not match";
  delete_account_confirmation = "Are you sure you want to deactivate your account forever?";
  /* FUNCTIONS */
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirm_password = control.get('confirm_password')?.value;
    return password === confirm_password ? null : { 'passwordMismatch': true };
  }

  loadUser() {
    this.myAccountService.GetUser(localStorage.getItem("email")).subscribe(
      (result) => {
        console.log("Rezultatul functiei este un element user: ", result);
        this.currentUser = result;
      },
      (error: any) => {
        this.http_code_error_message = error.message;
        console.log(error);
        this.authService.handleError(error);
        this.hideErrorMessage();
        this.showErrorMessage = true;
      }
    );
  }
  ngOnInit() {
    console.log("ngOnInit MyAccountComponent : " + localStorage.getItem("email"));
    this.loadUser();
  }

  ProceedEditUser() {
    console.log("ProceedEditUser()");

    if (this.Edit.valid) {
      const nameChanged = this.Edit.get('name')?.dirty && this.Edit.get('name')?.value !== this.currentUser.name;
      const passwordChanged = this.Edit.get('new_password')?.value || this.Edit.get('current_password')?.value || this.Edit.get('confirm_password')?.value;

      console.log("namechanged: " + nameChanged);
      console.log("passwordchanged " + passwordChanged);

      if (nameChanged) {
        this.newUsername = this.Edit.value.name;
        this.myAccountService.ProceedEditUser(this.currentUser.email, this.newUsername).subscribe(
          (result) => {
            this.Edit.get('name')?.setValue(this.newUsername);
            window.location.reload();
          },
          (error) => {
            this.http_code_error_message = error.message;
            this.authService.handleError(error);
            this.hideErrorMessage();
            this.showErrorMessage = true;
          }
        );
      }

      if (passwordChanged) {
        if (this.Edit.get('new_password')?.value || this.Edit.get('current_password')?.value || this.Edit.get('confirm_password')?.value) {
          if (!this.Edit.get('new_password')?.value || !this.Edit.get('current_password')?.value || !this.Edit.get('confirm_password')?.value) {
            console.log('Please fill in all password fields');
            this.http_code_error_message = this.not_all_password_fiels_completed;
            this.hideErrorMessage();
            this.showErrorMessage = true;
          }

          if (this.Edit.get('new_password')?.value !== this.Edit.get('confirm_password')?.value) {
            console.log('Passwords do not match');
            this.http_code_error_message = this.new_password_not_matching_with_confirm;
            this.hideErrorMessage();
            this.showErrorMessage = true;
          }

        }
        this.myAccountService.ProceedChangePassword(this.currentUser.email, this.Edit.value.current_password, this.Edit.value.new_password, this.Edit.value.confirm_password).subscribe(
          (result) => {
            this.router.navigate(['login']);
          },
          (error) => {
            console.log('error proceededituser');
            this.http_code_error_message = error.message;
            this.authService.handleError(error);
            this.hideErrorMessage();
            this.showErrorMessage = true;
          }
        );
      }

    }
  }


  ProceedDeactivateAccount() {
    console.log("ProceedDeactivateAccount()")
    this.myAccountService.ProceedDeactivateAccount(this.currentUser.email).subscribe(
      (result) => {
        this.router.navigate(['login']);
      },
      (error) => {
        console.log('error proceededituser');
        this.http_code_error_message = error.message;
        // this.authService.handleError(error);
        this.hideErrorMessage();
        this.showErrorMessage = true;
      }
    );

  }
  hideErrorMessage() {
    this.showErrorMessage = true;
    setTimeout(() => {
      this.http_code_error_message = '';
      this.showErrorMessage = false;
    }, 5000);
  }


  deactivateAccount() {
    const confirmation = window.confirm(this.delete_account_confirmation);

    if (confirmation) {
    }
  }

}
