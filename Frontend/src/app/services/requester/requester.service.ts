/**************************************************************************

   File:       requester.service.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   01.12.2023   Sebastian Pitica      Added patch method

 **************************************************************************/


import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class RequesterService {

  constructor(private http: HttpClient) {
  }
  get<T>(url: string, options?: any): Observable<T> {
    return this.http.get<T>(url, options).pipe(
      map((response: HttpEvent<T>) => response as T)
    );
  }

  post<T>(url: string, data: any, options?: any): Observable<T> {
    return this.http.post<T>(url, data, options).pipe(
      map((response: HttpEvent<T>) => response as T)
    );
  }

  put<T>(url: string, data: any, options?: any): Observable<T> {
    return this.http.put<T>(url, data, options).pipe(
      map((response: HttpEvent<T>) => response as T)
    );
  }

  delete<T>(url: string, options?: any): Observable<T> {
    return this.http.delete<T>(url, options).pipe(
      map((response: HttpEvent<T>) => response as T)
    );
  }

  patch<T>(url: string, options?: any,data?: any): Observable<T> {
    return this.http.patch<T>(url, data, options).pipe(
      map((response: HttpEvent<T>) => response as T)
    );
  }
}

