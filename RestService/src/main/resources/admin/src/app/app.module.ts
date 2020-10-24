import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule,FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastrModule } from 'ngx-toastr';

import { AppRoutingModule } from './app.routing';
import { ComponentsModule } from './components/components.module';

import { AppComponent } from './app.component';

import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import {LoginGuard } from './login/login.guard';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AuthInterceptor } from './auth.interceptor';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { SupscriptionComponent } from './supscription/supscription.component';
import { UserprofileComponent } from './userprofile/userprofile.component';
import { DatastoreComponent } from './datastore/datastore.component';
import { TunnelingComponent } from './tunneling/tunneling.component';
import { ServicetimingComponent } from './servicetiming/servicetiming.component';
import { MetricschartComponent } from './metricschart/metricschart.component';
import { ServiceerrorComponent } from './serviceerror/serviceerror.component';
import { ServiceComponent } from './service/service.component';
import { ServiceattributeComponent } from './serviceattribute/serviceattribute.component';
import { MultiserviceComponent } from './multiservice/multiservice.component';
import { ValidationComponent } from './validation/validation.component';
import { ProcessorComponent } from './processor/processor.component';
@NgModule({
  imports: [
	ReactiveFormsModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    ComponentsModule,
    RouterModule,
    AppRoutingModule,
    NgbModule,
    ToastrModule.forRoot()
  ],
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    LoginComponent,
    RegisterComponent,
    SupscriptionComponent,
    UserprofileComponent,
    DatastoreComponent,
    TunnelingComponent,
    ServicetimingComponent,
    MetricschartComponent,
    ServiceerrorComponent,
    ServiceComponent,
    ServiceattributeComponent,
    MultiserviceComponent,
    ValidationComponent,
    ProcessorComponent

  ],
  providers: [
	{
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
	LoginGuard],
  bootstrap: [AppComponent]
})
export class AppModule { }
