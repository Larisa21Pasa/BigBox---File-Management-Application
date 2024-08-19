/**************************************************************************

   File:        auth.service.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
    02.12.2023  Pasa Larisa        Added refresh token methods
    01.12.2023  Pasa Larisa        Added token based authentication methods

 **************************************************************************/
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, Observable, of, switchMap, throwError} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  /* CONSTRUCTOR */
  constructor(private http: HttpClient, private router: Router) {
  }
  /* VARIABLES */

  authenticate_path = 'http://localhost:8080/api/auth/authenticate';
  register_path = 'http://localhost:8080/api/auth/register';
  logout_path = 'http://localhost:8080/api/auth/logout';
  check_token_validity_path = 'http://localhost:8080/api/auth/token';

  refresh_token_path = 'http://localhost:8080/api/auth/refresh-token';

  launch_decktop_app_path = 'http://localhost:8080/api/admin/launchDesktopApp';
  get_user_role_by_email = 'http://localhost:8080/api/users/role';
  login_error_message = "";


  /**
    * Function to handle the login process
    */
  ProceedLogin(user_credentials: any): Observable<any> {
    console.log("AuthService=>ProceedLogin");
    return this.http
      .post(this.authenticate_path, user_credentials)
      .pipe(
        catchError(this.handleError),
      );
  }



  /**
    * Function to handle the signup process
    */
  ProceedSignup(user_credentials: any): Observable<any> {
    return this.http
      .post(this.register_path, user_credentials)
      .pipe(
        catchError(this.handleError)
      );
  }
  setHttpErrorMessage(message: any) {
    this.login_error_message = message;
  }
  getHttpErrorMessage() {
    return this.login_error_message;
  }

  handleError(error: HttpErrorResponse) {
    let http_code_error_message = '';
    if (error.status === 0) {
    } else {
      http_code_error_message = `${error.error}`;
    }

    return throwError(() => ({
      message: http_code_error_message,  // Include the server error message
      status: error.status  // Include the HTTP status code
    }));
  }

  IsLoggedIn() {
    return localStorage.getItem("access_token") != null;
  }

  GetToken() {
    return localStorage.getItem('access_token') || '';
  }

  GetTokenValidity() {
    const headers = this.getHeaders();
    const token = this.GetToken();
    return this.http.get(`${this.check_token_validity_path}/${token}`, { headers });
  }

  IsValidToken(): Observable<boolean> {
    return this.GetTokenValidity().pipe(
      switchMap(response => {
        console.log("switchmap()");
        // Verifică dacă răspunsul este primit și conține informații
        if (response != null) {
          console.log("token valid cu raspuns "+response.valueOf);
          // Tokenul este valid
          console.log("Token is valid");
          return of(true);
        } else {

          // Tokenul este invalid, întoarce false
          console.log("Token is invalid");
          return of(false);
        }
      }),
      catchError(error => {
        // Tratează eroarea, de exemplu, dacă este 403 (Forbidden), consideră tokenul invalid
        console.error("Error while checking token validity: ", error);
        if (error.status === 403) {
          return of(false);
        } else {
          // Dacă apare o altă eroare, propagă eroarea mai departe
          return throwError(error);
        }
      })
    );
  }

  getHeaders(): HttpHeaders {
    return new HttpHeaders().set('Authorization', 'Bearer ' + this.GetToken());
  }

  GetUserRole(email: any) {
    const headers = this.getHeaders();
    console.log("GetUserRole => Headers  " + headers.get)
    console.log("GetUserRole => Email  " + email)
    return this.http.get(`${this.get_user_role_by_email}/${email}`, { headers });
  }


  launchDesktopApp() {
    console.log("launching app method");
    const headers = this.getHeaders();
    console.log("headers " + headers.get);
    return this.http.get(this.launch_decktop_app_path, { headers })
      .subscribe(
        (response) => {
          console.log("Response from server: ", response);
        },
        (error) => {
          console.error("Error from server: ", error);
        }
      );
  }
  LogoutRefreshToken() {
    alert("Session expired at " + Date());
    this.RekoveValidTokens();
    this.ClearStorageAfterLogout();
  }
  Logout() {
    this.RekoveValidTokens();
    this.ClearStorageAfterLogout();
  }
  ClearStorageAfterLogout() {
    localStorage.clear();
    this.router.navigate(['welcome']);

  }

  RekoveValidTokens() {
    const headers = this.getHeaders();


    return this.http.post(this.logout_path, null, { headers, responseType: 'text' }).subscribe(
      (response) => {
        console.log( response);
      },
      (error) => {
        console.error("Error from server: ", error);
      }
    );
  }

  /* REFRESH METHODS */
  GenerateRefreshToken() {
    let input = {
      'access_token': this.GetToken(),
      'refresh_token': this.GetRefreshToken()
    }
    this.RekoveValidTokens();
    localStorage.clear();
    return this.http
      .post(this.refresh_token_path, input)
      .pipe(
        catchError(this.handleError),
      )


  }

  GetRefreshToken() {
    return localStorage.getItem('refresh_token') || '';
  }

  SaveTokens(tokens: any) {
    localStorage.clear();

    localStorage.setItem("access_token", tokens.access_token);
    localStorage.setItem("refresh_token", tokens.refresh_token);

  }


}
