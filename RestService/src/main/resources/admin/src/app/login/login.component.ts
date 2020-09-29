import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup,Validators  } from "@angular/forms";

import { AuthService } from '../auth/auth-service.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  constructor(
    public formBuilder: FormBuilder,
    public authService: AuthService,
    public router: Router
  ) {
    this.loginForm= this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    })
  }

  ngOnInit() {
	
	 this.loginForm= this.formBuilder.group({
     email: ['', [Validators.required, Validators.email,Validators.pattern('^[A-Za-z0-9._%+-]+@[a-z0-9.-]+\\.[A-Za-z]{2,4}$')]],
      password: ['', Validators.required]
    })
 }

get f() { return this.loginForm.controls; }

  loginUser() {
	 this.submitted = true;
	console.log(this.loginForm.controls.email.errors);
	//console.log(this.loginForm.controls.password.errors['required']);
	 localStorage.setItem('access_token', 'trail')
if (this.loginForm.invalid) {
            return;
        }
   //this.authService.login(this.loginForm.value)
    this.router.navigate(['dashboard']);
  
  }
}