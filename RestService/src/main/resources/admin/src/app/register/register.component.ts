import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from '@angular/router';
import { AuthService } from "../auth/auth-service.service";
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
registerForm: FormGroup;
 loading = false;
  submitted = false; 	
constructor(
    public formBuilder: FormBuilder,
    public authService: AuthService,
    public router: Router
  ) {
    this.registerForm= this.formBuilder.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email,Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      password: ['', Validators.required],
	  phoneno:['', Validators.required],
      address:['', Validators.required],
      repassword:['',Validators.required]
    })
  }

  ngOnInit() { }
get f() { return this.registerForm.controls; }
  registerUser() {
	 this.submitted = true;
if (this.registerForm.invalid) {
            return;
        }
	
    this.authService.register(this.registerForm.value).subscribe((res) => {
      if (res.result) {
        this.registerForm.reset()
        this.router.navigate(['login']);
      }
    })
  }
}
