/**************************************************************************

   File:        contact.component.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
   01.12.2023  Pasa Larisa          Contact logic functionalities implemented.
   18.01.2023  Matei Rares          Made all the functions
 **************************************************************************/
import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ContactUs} from 'src/app/models/welcome-contact';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, throwError} from "rxjs";

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss']
})

export class ContactComponent {
  /* CONSTRUCTOR */

  constructor(private http: HttpClient) { }

  /* VARIABLES */
  github_path = "https://github.com/HDR-2023-2024/proiectpaw-nazimpostor";
  contactUs: ContactUs = {
    address: "Iasi, Romania",
    phone: "0740 000 000",
    email: "ubigbox@email.com",
  };

  /* FORMS */
  contactForm = new FormGroup({
    name: new FormControl("", Validators.required),
    email: new FormControl("",[Validators.required, Validators.email]),
    description: new FormControl("", Validators.required)
  });

  /* FUNCTIONS */
  redirectToGitHub() {
    console.log('Redirecting to GitHub:', this.github_path);
    window.open(this.github_path, '_blank');
  }
  private apiUrl = 'http://localhost:8080/api/auth/contact';

  /* TO BE IMPLEMENTED */
  SendClientMessageContactSection() {
    const message=this.contactForm.get("name")?.value+"\n"+this.contactForm.get("email")?.value+"\n"+this.contactForm.get("description")?.value

     this.http.post(this.apiUrl, message).subscribe((data)=>{

     },catchError);

  }
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
