import { Component, OnInit } from '@angular/core';

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: '/dashboard', title: 'Dashboard',  icon: 'design_app', class: '' },
  //  { path: '/icons', title: 'Icons',  icon:'education_atom', class: '' },
  //  { path: '/maps', title: 'Maps',  icon:'location_map-big', class: '' },
  //  { path: '/notifications', title: 'Notifications',  icon:'ui-1_bell-53', class: '' },

  //  { path: '/user-profile', title: 'User Profile',  icon:'users_single-02', class: '' },
 //   { path: '/table-list', title: 'Table List',  icon:'design_bullet-list-67', class: '' },
 //   { path: '/typography', title: 'Typography',  icon:'text_caps-small', class: '' },
 //   { path: '/upgrade', title: 'Upgrade to PRO',  icon:'objects_spaceship', class: '' },
    { path: '/datastore', title: 'DataStore',  icon:'business_money-coins', class: '' },
	//{ path: '/tunneling', title: 'Tunneling Service',  icon:'shopping_delivery-fast', class: '' },
	//{ path: '/servicetiming', title: 'Service Timing',  icon:'tech_watch-time', class: '' },
	{ path: '/metricschart', title: 'Metrics ',  icon:'business_chart-bar-32', class: '' },
	{ path: '/serviceerror', title: 'Rest Service Error ',  icon:'ui-1_simple-remove', class: '' },
	{ path: '/service', title: 'Rest Service ',  icon:'objects_planet', class: '' },
	{ path: '/serviceattribute', title: 'Rest Service Attribute',  icon:'objects_support-17', class: '' },
	{ path: '/multiservice', title: 'Multi Rest Service ',  icon:'files_paper', class: '' },
	{ path: '/validation', title: 'Validation Config ',  icon:'ui-1_check', class: '' },
  { path: '/processor', title: 'Processor Config ',  icon:'education_atom', class: '' },
{ path: '/servicedataprocess', title: 'Data Manupulation ',  icon:'education_atom', class: '' }

];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: any[];

  constructor() { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  }
  isMobileMenu() {
      if ( window.innerWidth > 991) {
          return false;
      }
      return true;
  };
}
