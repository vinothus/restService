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
export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent,canActivate :[LoginGuard] },
    { path: 'user-profile',   component: UserProfileComponent ,canActivate :[LoginGuard]},
    { path: 'table-list',     component: TableListComponent,canActivate :[LoginGuard] },
    { path: 'typography',     component: TypographyComponent ,canActivate :[LoginGuard]},
    { path: 'icons',          component: IconsComponent ,canActivate :[LoginGuard]},
    { path: 'maps',           component: MapsComponent,canActivate :[LoginGuard] },
    { path: 'notifications',  component: NotificationsComponent,canActivate :[LoginGuard] },
    { path: 'upgrade',        component: UpgradeComponent,canActivate :[LoginGuard] }
];
