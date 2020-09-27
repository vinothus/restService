import { NgModule } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';

import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import {LoginGuard } from './login/login.guard';

import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
const routes: Routes =[
	
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
canActivate :[LoginGuard]
  }, {
    path: '',
    component: AdminLayoutComponent,
    children: [
        {
      path: '',
      loadChildren: './layouts/admin-layout/admin-layout.module#AdminLayoutModule',
canActivate :[LoginGuard]
  }]},
  {
    path: '**',
    redirectTo: 'dashboard',
canActivate :[LoginGuard]
  }
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes)
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
