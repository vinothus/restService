import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {
	
	 constructor(private httpClient: HttpClient,public router: Router){}
	
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
	var access_token= localStorage.getItem('access_token');
	console.log(access_token);
	console.log('login gaurd');
	if(access_token !== null)
	{
	return true;	
		
	}else
	{
	this.router.navigate(['login']);	
	}
//this.router.navigate(['dashboard']);
	
    
  }
  
}
