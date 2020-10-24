import { Routes } from '@angular/router';

import { DashboardComponent } from '../../dashboard/dashboard.component';
import { UserProfileComponent } from '../../user-profile/user-profile.component';
import { TableListComponent } from '../../table-list/table-list.component';
import { TypographyComponent } from '../../typography/typography.component';
import { IconsComponent } from '../../icons/icons.component';
import { MapsComponent } from '../../maps/maps.component';
import { NotificationsComponent } from '../../notifications/notifications.component';
import { UpgradeComponent } from '../../upgrade/upgrade.component';
import {LoginGuard } from '../../login/login.guard';
import { UserprofileComponent } from '../../userprofile/userprofile.component';
import { DatastoreComponent } from '../../datastore/datastore.component';
import { TunnelingComponent } from '../../tunneling/tunneling.component';
import { ServicetimingComponent } from '../../servicetiming/servicetiming.component';
import { MetricschartComponent } from '../../metricschart/metricschart.component';
import { ServiceerrorComponent } from '../../serviceerror/serviceerror.component';
import { ServiceComponent } from '../../service/service.component';
import { ServiceattributeComponent } from '../../serviceattribute/serviceattribute.component';
import { MultiserviceComponent } from '../../multiservice/multiservice.component';
import { ValidationComponent } from '../../validation/validation.component';
import { ProcessorComponent } from '../../processor/processor.component';
export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent,canActivate :[LoginGuard] },
    { path: 'user-profile',   component: UserProfileComponent ,canActivate :[LoginGuard]},
    { path: 'table-list',     component: TableListComponent,canActivate :[LoginGuard] },
    { path: 'typography',     component: TypographyComponent ,canActivate :[LoginGuard]},
    { path: 'icons',          component: IconsComponent ,canActivate :[LoginGuard]},
    { path: 'maps',           component: MapsComponent,canActivate :[LoginGuard] },
    { path: 'notifications',  component: NotificationsComponent,canActivate :[LoginGuard] },
    { path: 'upgrade',        component: UpgradeComponent,canActivate :[LoginGuard] },
    { path: 'userprofile',        component: UserprofileComponent,canActivate :[LoginGuard] },
    { path: 'datastore',        component: DatastoreComponent,canActivate :[LoginGuard] },
    { path: 'tunneling',        component: TunnelingComponent,canActivate :[LoginGuard] },
	{ path: 'servicetiming',        component: ServicetimingComponent,canActivate :[LoginGuard] },
	{ path: 'metricschart',        component: MetricschartComponent,canActivate :[LoginGuard] },
	{ path: 'serviceerror',        component: ServiceerrorComponent,canActivate :[LoginGuard] },
	{ path: 'service',        component: ServiceComponent,canActivate :[LoginGuard] },
	{ path: 'serviceattribute',        component: ServiceattributeComponent,canActivate :[LoginGuard] },
	{ path: 'multiservice',        component: MultiserviceComponent,canActivate :[LoginGuard] },
	{ path: 'validation',        component: ValidationComponent,canActivate :[LoginGuard] } ,
	{ path: 'processor',        component: ProcessorComponent,canActivate :[LoginGuard] }
];
