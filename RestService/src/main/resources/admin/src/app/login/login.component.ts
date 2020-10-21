import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup,Validators  } from "@angular/forms";
import * as Survey from "survey-angular";
import { AuthService } from '../auth/auth-service.service';

@Component({
  selector: 'app-login',
templateUrl:'./login.component.html',
  styleUrls: ['./login.component.css']
})

export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
 surveyJSON = { title: "Tell us, what technologies do you use?", pages: [
  { name:"page1", questions: [ 
      { type: "radiogroup", choices: [ "Yes", "No" ], isRequired: true, name: "frameworkUsing",title: "Do you use any front-end framework like Bootstrap?" },
      { type: "checkbox", choices: ["Bootstrap","Foundation"], hasOther: true, isRequired: true, name: "framework", title: "What front-end framework do you use?", visibleIf: "{frameworkUsing} = 'Yes'" }
   ]},
  { name: "page2", questions: [
    { type: "radiogroup", choices: ["Yes","No"],isRequired: true, name: "mvvmUsing", title: "Do you use any MVVM framework?" },
    { type: "checkbox", choices: [ "AngularJS", "KnockoutJS", "React" ], hasOther: true, isRequired: true, name: "mvvm", title: "What MVVM framework do you use?", visibleIf: "{mvvmUsing} = 'Yes'" } ] },
  { name: "page3",questions: [
    { type: "comment", name: "about", title: "Please tell us about your main requirements for Survey library" } ] }
 ]
}


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
	console.log(this.surveyJSON);
	var survey = new Survey.Model(this.surveyJSON);
   survey.onComplete.add(sendDataToServer);
    Survey.SurveyWindowNG.render("surveyElement", {model:survey});
	 this.loginForm= this.formBuilder.group({
     email: ['', [Validators.required, Validators.email,Validators.pattern('^[A-Za-z0-9._%+-]+@[a-z0-9.-]+\\.[A-Za-z]{2,4}$')]],
      password: ['', Validators.required]
    });
 

 }

get f() { return this.loginForm.controls; }

  loginUser() {
	 this.submitted = true;
this.loginForm.markAllAsTouched();
this.loginForm.get("email").updateValueAndValidity();
 
	console.log(this.loginForm.controls.email.errors);
	//console.log(this.loginForm.controls.password.errors['required']);
	 //localStorage.setItem('access_token', 'trail')
if (!this.loginForm.invalid) {
	this.authService.login(this.loginForm.value,this.loginForm);
            return;
        }
   //this.authService.login(this.loginForm.value)
   // this.router.navigate(['dashboard']);
  
  }

 
}
function sendDataToServer(survey) {
  var resultAsString = JSON.stringify(survey.data);
  alert(resultAsString); //send Ajax request to your web server.
}