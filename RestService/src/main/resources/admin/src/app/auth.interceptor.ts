import { Injectable } from "@angular/core";
import { HttpInterceptor, HttpRequest, HttpHandler } from "@angular/common/http";
import { AuthService } from "./auth/auth-service.service";
import { DOCUMENT } from '@angular/common';
import {  Inject } from '@angular/core';
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
	
    constructor(@Inject(DOCUMENT)  private  document: Document,private authService: AuthService) {
	this.orgin=document.location.protocol +'//'+ document.location.hostname + ':8080';
 }
orgin : string='';
    intercept(req: HttpRequest<any>, next: HttpHandler) {
	console.log('interceptor');

        const accessToken = this.authService.getAccessToken();
 	console.log('accessToken '+accessToken);
        req = req.clone({
            setHeaders: {
                Authorization: `JWT $[accessToken}` ,
                'Access-Control-Allow-Credentials': 'true',
                'Access-Control-Allow-Methods' : 'GET, POST, OPTIONS, PUT , DELETE ' ,   
                'Access-Control-Allow-Headers':  'Origin, X-Requested-With, Content-Type, Accept'
                 
            }
        });
        return next.handle(req);
    }
}