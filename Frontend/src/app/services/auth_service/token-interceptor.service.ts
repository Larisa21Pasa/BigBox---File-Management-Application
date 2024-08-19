/**************************************************************************

   File:        auth.service.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date        Author                Reason
   01.12.2023  Pasa Larisa        Enhanced token interceptor for better error handling
 **************************************************************************/
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable, Injector} from '@angular/core';
import {catchError, Observable, switchMap, throwError} from 'rxjs';
import {AuthService} from './auth.service';

/*
  @Injectable decorator marks this class as injectable by Angular's Dependency Injection system.
  It is responsible for intercepting outgoing HTTP requests globally before they are sent and
  adding an Authorization header with a Bearer token obtained from the AuthService.

  The intercept method takes the original HttpRequest<any> object.

  The AddTokenHeader function is then called to create a modified copy of the request (authRequest)
  by adding the Authorization header with the Bearer token obtained from authservice.GetToken().

  The modified request (authRequest) is then passed to next.handle(authRequest).
*/

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor {
  /* CONSTRUCTOR */
  constructor(private inject: Injector) { }

  /* FUNCTIONS */

  /**
   * Intercepting outgoing HTTP requests globally before they are sent.
   *
   * @param request - The original HttpRequest<any> object.
   * @param next - The HttpHandler to handle the modified request.
   * @returns An Observable of the HttpEvent<any>.
   */
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Injecting the `AuthService` to obtain token information and manage user authentication.
    let authservice = this.inject.get(AuthService);

    // Creating a copy of the original request with an Authorization header containing the Bearer token.
    let authRequest = this.AddTokenHeader(request, authservice.GetToken());
    //console.log("TokenInterceptorService=>authRequest" + authRequest.headers.get)
    //console.log("interceptor localstorage: " + localStorage.getItem("access_token"));

    // Handling the modified request and catching any potential errors
    return next.handle(authRequest).pipe(
      catchError(errordata => {
        var check = authservice.IsValidToken();

        if ((errordata.status === 403)) {
          authservice.LogoutRefreshToken();

        }
        return throwError(errordata);
      })


    );
  }

  AddTokenHeader(request: HttpRequest<any>, access_token: any) {
    return request.clone(
      {
        headers: request.headers.set('Authorization', 'Bearer ' + access_token)
      }
    )
  }

}
