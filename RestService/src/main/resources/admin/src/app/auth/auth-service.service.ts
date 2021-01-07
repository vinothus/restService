import { Injectable } from '@angular/core';

import { Router } from '@angular/router';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { User } from '../../model/user';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DOCUMENT } from '@angular/common';
import { Inject } from '@angular/core';
import { AppConstants } from "../app-constants";
@Injectable({
	providedIn: 'root'
})

export class AuthService {
	API_URL: string = 'http://localhost:4000';
	APP_NAME: string = 'myApps';
	APP_SYS_API_KEY:string;
	App_SYS_DS_KEY:string;
	
	headers = new HttpHeaders().set('Content-Type', 'application/json');
	currentUser = {};

	constructor(@Inject(DOCUMENT) private document: Document, private httpClient: HttpClient, public router: Router) {


		let url = document.location.protocol + '//' + document.location.hostname + ':8080'; // for angular development
		//let url =document.location.protocol +'//'+ document.location.hostname + ':'+document.location.port 
		console.log(url);
		console.log(document.location.protocol + '//' + document.location.hostname + ':' + document.location.port);
		this.API_URL = url;
	}

	register(user: User): Observable<any> {

		return this.httpClient.post(this.API_URL + `/${this.APP_NAME}/system/system/user/addData`, user).pipe(
			catchError(this.handleError)
		)
	}

	login(user: User, loginForm: FormGroup) {
		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy'
		});
		const params = new HttpParams()
			.set('email', user.email + '')
			.set('password', user.password + '');
		const options = {
			headers,
			params,
			withCredentials: true
		};
		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/system/system/user/getdata`, { params: params })
			.subscribe((res: any) => {
				loginForm.controls['email'].setErrors(null);
				if (res['user'][0] != undefined) {
					localStorage.setItem('uid', res['user'][0].id)
					if (res['user'][0].apikey != undefined) {
						localStorage.setItem('access_token', res['user'][0].apikey)
						this.getUserProfile(res['user'][0].id).subscribe((res) => {
							this.currentUser = res;
							this.router.navigate(['dashboard', res.id]);

						})
					} else {
						this.router.navigate(['supscription']);
					}
				} else {
					loginForm.controls['email'].setErrors({ emailValidation: true });
					this.router.navigate(['login']);
				}
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
		return this.httpClient.get(this.API_URL + `/${this.APP_NAME}/system/system/user/getdataForKey/${id}`, { headers: this.headers }).pipe(
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


	getdata(service: string, params: Map<string, string>) {

		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy',
			'Access-Control-Allow-Origin': '*',
		});

		const params1 = new HttpParams();
		let paramstr = '';
		params.forEach(function(value, key) {
			console.log(key + " = " + value);
			paramstr = paramstr + key + '=' + value + '&';
			params1.set(key, value);
		})

		const options = {
			headers,
			params1,
			withCredentials: true
		};
 
		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/system/system/${service}/getdata?` + paramstr, { params: params1, headers: headers }).pipe(
			map((res: Response) => {
				return res || {}
			}),
			catchError(this.handleError)
		);

		/* console.log(promise);  
		 promise.then((data)=>{
		   console.log("Promise resolved with: " + JSON.stringify(data));
		  return data;
		 }).catch((error)=>{
		   console.log("Promise rejected with " + JSON.stringify(error));
		   
		 });*/
	}

	getAllMultidata(apikey:string,datastorekey:string,service: string,multiservice:string,multiserviceMethod:string, params: Map<string, string>) {

		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy',
			'Access-Control-Allow-Origin': '*',
		});

		const params1 = new HttpParams();
		let paramstr = '';
		params.forEach(function(value, key) {
			console.log(key + " = " + value);
			paramstr = paramstr + key + '=' + value + '&';
			params1.set(key, value);
		})

		const options = {
			headers,
			params1,
			withCredentials: true
		};

		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/${apikey}/${datastorekey}/${service}/${multiservice}/${multiserviceMethod}?` + paramstr, { params: params1, headers: headers }).pipe(
			map((res: Response) => {
				return res || {}
			}),
			catchError(this.handleError)
		);

		/* console.log(promise);  
		 promise.then((data)=>{
		   console.log("Promise resolved with: " + JSON.stringify(data));
		  return data;
		 }).catch((error)=>{
		   console.log("Promise rejected with " + JSON.stringify(error));
		   
		 });*/
	}
	 getPropertyMap() {
		let map = new Map<string, string>();
		map.set('processorClassName', 'yekeulav');
		 
		if (AppConstants.propertyMap.size == 0) {
			this.getproperties('auth', map).subscribe((res) => {

				for (var key in res) {
					AppConstants.propertyMap.set(key, res[key]);
				}

			});
			return AppConstants.propertyMap;
		}else
		{
			return AppConstants.propertyMap;
		}

	}

	getproperties(service: string, params: Map<string, string>) {

		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy',
			'Access-Control-Allow-Origin': '*',
		});

		const params1 = new HttpParams();
		let paramstr = '';
		params.forEach(function(value, key) {
			console.log(key + " = " + value);
			paramstr = paramstr + key + '=' + value + '&';
			params1.set(key, value);
		})

		const options = {
			headers,
			params1,
			withCredentials: true
		};

		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/system/system/${service}/preProcessData?` + paramstr, { params: params1, headers: headers }).pipe(
			map((res: Response) => {
				return res || {}
			}),
			catchError(this.handleError)
		);

		/* console.log(promise);  
		 promise.then((data)=>{
		   console.log("Promise resolved with: " + JSON.stringify(data));
		  return data;
		 }).catch((error)=>{
		   console.log("Promise rejected with " + JSON.stringify(error));
		   
		 });*/
	}


	validateData(service: string, params: Map<string, string>) {

		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy',
			'Access-Control-Allow-Origin': '*',
		});

		const params1 = new HttpParams();
		let paramstr = '';
		params.forEach(function(value, key) {
			console.log(key + " = " + value);
			paramstr = paramstr + key + '=' + value + '&';
			params1.set(key, value);
		})

		const options = {
			headers,
			params1,
			withCredentials: true
		};

		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/system/system/${service}/validateData?` + paramstr, { params: params1, headers: headers }).pipe(
			map((res: Response) => {
				return res || {}
			}),
			catchError(this.handleError)
		);

		/* console.log(promise);  
		 promise.then((data)=>{
		   console.log("Promise resolved with: " + JSON.stringify(data));
		  return data;
		 }).catch((error)=>{
		   console.log("Promise rejected with " + JSON.stringify(error));
		   
		 });*/
	}

	validateDatabyApikey(apikey: string, datastore: string, service: string, params: Map<string, string>) {

		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy',
			'Access-Control-Allow-Origin': '*',
		});

		const params1 = new HttpParams();
		let paramstr = '';
		params.forEach(function(value, key) {
			console.log(key + " = " + value);
			paramstr = paramstr + key + '=' + value + '&';
			params1.set(key, value);
		})

		const options = {
			headers,
			params1,
			withCredentials: true
		};

		return this.httpClient.get<any>(this.API_URL + `/${this.APP_NAME}/` + apikey + `/` + datastore + `/${service}/validateData?` + paramstr, { params: params1, headers: headers }).pipe(
			map((res: Response) => {
				return res || {}
			}),
			catchError(this.handleError)
		);

		/* console.log(promise);  
		 promise.then((data)=>{
		   console.log("Promise resolved with: " + JSON.stringify(data));
		  return data;
		 }).catch((error)=>{
		   console.log("Promise rejected with " + JSON.stringify(error));
		   
		 });*/
	}

	getUniqueData(service: string, id: string) {
		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy'
		});
		const promise = this.httpClient.get(this.API_URL + `/${this.APP_NAME}/system/system/${service}/getdataForKey/${id}`, { headers: headers }).toPromise();
		console.log(promise);
		promise.then((data) => {
			console.log("Promise resolved with: " + JSON.stringify(data));
			return data;
		}).catch((error) => {
			console.log("Promise rejected with " + JSON.stringify(error));

		});
	}

	deleteUniqueData(service: string, id: string) {
		const headers = new HttpHeaders({
			'Content-Type': 'application/x-www-form-urlencoded',
			'Authorization': 'Basic YW5ndWxhcjphbmd1bGFy'
		});
		const promise = this.httpClient.delete(this.API_URL + `/${this.APP_NAME}/system/system/${service}/deleteData/${id}`, { headers: headers }).toPromise();
		console.log(promise);
		return promise;
	}
	addData(service: string, data: any) {

		const headers = new HttpHeaders()
			.set("Content-Type", "application/json")
			.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy");

		const promise = this.httpClient.post(this.API_URL + `/${this.APP_NAME}/system/system/${service}/addData`, data, { headers }).toPromise();
		console.log(promise);

		return promise;

	}
	updateData(service: string, data: any) {


		const headers = new HttpHeaders()
			.set("Content-Type", "application/json")
			.set("Authorization", "Basic YW5ndWxhcjphbmd1bGFy");

		const promise = this.httpClient.put(this.API_URL + `/${this.APP_NAME}/system/system/${service}/updateData`, data, { headers: headers }).toPromise();
		console.log(promise);
		return promise;
	}

}