import { Injectable } from '@angular/core';

import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse,HttpParams } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { User } from '../../model/user';


@Injectable({
  providedIn: 'root'
})

export class AuthService {
  API_URL: string = 'http://localhost:4000';
  headers = new HttpHeaders().set('Content-Type', 'application/json');
  currentUser = {};

  constructor(private httpClient: HttpClient,public router: Router){}

  register(user: User): Observable<any> {

    return this.httpClient.post(`${window.location.origin}/myApps/user/addData`, user).pipe(
        catchError(this.handleError)
    )
  }

  login(user: User) {
	 const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy'
    });
	const params = new HttpParams()
      .set('email', user.email+'')
      .set('password', user.password+'');
	const options = {
      headers,
      params,
      withCredentials: true
    };
    return this.httpClient.get<any>(`${window.location.origin}/myApps/user/getdata`,  { params: params })
      .subscribe((res: any) => {
        localStorage.setItem('access_token', res[0].id)
        this.getUserProfile(res[0].id).subscribe((res) => {
          this.currentUser = res;
          this.router.navigate(['dashboard' , res.id]);
        })
      })
  }

  getAccessToken() {
    return localStorage.getItem('access_token');
  }

  get isLoggedIn(): boolean {
    let authToken = localStorage.getItem('access_token');
    return (authToken !== null) ? true : false;
  }

  logout() {
    if (localStorage.removeItem('access_token') == null) {
      this.router.navigate(['login']);
    }
  }

  getUserProfile(id): Observable<any> {
    return this.httpClient.get(`${window.location.origin}/myApps/user/getdataForKey/${id}`, { headers: this.headers }).pipe(
      map((res: Response) => {
        return res || {}
      }),
      catchError(this.handleError)
    )
  }

  handleError(error: HttpErrorResponse) {
    let msg = '';
    if (error.error instanceof ErrorEvent) {
      // client-side error
      msg = error.error.message;
    } else {
      // server-side error
      msg = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(msg);
  }
}