import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators,FormControl,FormArray, AbstractControl, AsyncValidatorFn, ValidationErrors } from "@angular/forms";
import { Router } from '@angular/router';
import { AuthService } from "../auth/auth-service.service";
import { Observable, of } from 'rxjs';
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
      email: ['', [Validators.required,Validators.pattern('^[A-Za-z0-9._%+-]+@[a-z0-9.-]+\\.[A-Za-z]{2,10}$')]],
      password: ['', Validators.required],
	  phoneno:['', Validators.required],
      address:['', Validators.required],
      repassword:['',Validators.required]
    },
      
      { updateOn: 'blur',
        validators: [this.ConfirmPasswordValidator("password", "repassword")]
      }
);
 
this.addValidator();
 }

  ngOnInit() { }
get f() { return this.registerForm.controls; }
  registerUser() {
	 this.submitted = true;


var objToadd = {};
for (const field in this.registerForm.controls) 
	{ // 'field' is a string↵  
	console.log(this.registerForm.controls[field].value);
	objToadd[field]=this.registerForm.controls[field].value;
	}
	
	 // this.emailValidationasyn(objToadd['email']);
// this.isValidemail() ;
//this.isFieldValid('email');
if (this.registerForm.invalid) {
            return;
        }
    this.authService.register(this.registerForm.value).subscribe((res) => {
      if (res.id) {
	//this.authService.addData('user',objToadd);
	//objToadd['id']=res.id;
	//this.authService.updateData('user',this.registerForm.value);
	//this.authService.getUniqueData('user',res.id);
	//let map = new Map<string,string>();
	//map.set('id',res.id);
	//this.authService.getdata('user',map);
	//this.authService.deleteUniqueData('user',res.id);
        this.registerForm.reset()
        this.router.navigate(['login']);
      }
    })
  }

ConfirmPasswordValidator(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      let control = formGroup.controls[controlName];
      let matchingControl = formGroup.controls[matchingControlName]
      if (
        matchingControl.errors&&
        !matchingControl.errors.confirmPasswordValidator
      ) {
        return;
      }
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ confirmPasswordValidator: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

emailValidation(controlName: string)
{
 return (formGroup: FormGroup) => {
	  let control = formGroup.controls[controlName];
	 let map = new Map<string,string>();
	map.set(controlName,control.value);
	var res=this.authService.getdata('user',map);
	if (
        control.errors&&
        !control.errors.emailValidation
      ) {
        return;
      }
	if(!res[0].id)
	{
	 control.setErrors({ emailValidation: true });	
	}else {
		 control.setErrors(null);
	}
	
	}	
	
}
emailValidationasyn(email: string)
{
	let map = new Map<string,string>();
	map.set('email',email);
	this.authService.getdata('user',map).subscribe((res) => {
		if(res[0].id)
	{
		 let control = this.registerForm.controls['email'];
	 control.setErrors({ emailValidation: true });	
		return;
	}else 
	   {
		 
		}
		
      
    });
	
}

addValidator(){
    this.registerForm.controls['email'].setAsyncValidators([this.isValidemail()]);
  }

 isValidemail(): AsyncValidatorFn {
      return (control: AbstractControl): Observable<ValidationErrors> => {
          let bReturn: boolean = true;
let map = new Map<string,string>();
	map.set('email',this.registerForm.controls['email'].value);
this.authService.getdata('user',map).subscribe((res) => {
	   if(res[0]!=undefined)
	{	if(res[0].id!=undefined)
	{
		 let control = this.registerForm.controls['email'];
	 control.setErrors({ emailValidation: true });	
		 let err: ValidationErrors = { 'invalid': true };
          return bReturn ? of(null) : of(err);
	}else 
	   {
		 control.setErrors(null);
		 return false;
		}
	}else 
	   {
		 control.setErrors(null);
		 return false;
		}
		
      
    });
         let control1 = this.registerForm.controls['email'];
	 control1.setErrors({ emailValidation: true });	
          let err: ValidationErrors = { 'invalid': true };
         return bReturn ? of(null) : of(err);
      };
  }
isFieldValid(field: string) {
  return (!this.registerForm.get(field).valid && this.registerForm.get(field).touched) ||
    (this.registerForm.get(field).untouched && this.submitted);
}
reset() {
  this.registerForm.reset();
  this.submitted = false;
}
}
