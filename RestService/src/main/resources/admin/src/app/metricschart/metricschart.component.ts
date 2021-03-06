import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js';
import { AuthService } from "../auth/auth-service.service";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DatePipe } from '@angular/common'
import {MatDatepickerInputEvent} from '@angular/material/datepicker';
@Component({
  selector: 'app-metricschart',
  templateUrl: './metricschart.component.html',
  styleUrls: ['./metricschart.component.css']
})
export class MetricschartComponent implements OnInit {
  data: any;
  Time = [];
  ConsumeTime = [];
  Timepost = [];
  ConsumeTimepost = [];
  Timeput = [];
  ConsumeTimeput = [];
  Timedelete = [];
  ConsumeTimedelete = [];
  Linechart : Chart ;
  isLoadingget = true;
  LinechartPost:Chart ;
  isLoadingpost = true;
  LinechartPUT: Chart ;
  isLoadingput = true;
  LinechartDelete: Chart ;
  isLoadingdelete = true;
  serviceConsumptionName ='service consumption';
chart1Time:number;
chart2Time:number;
chart3Time:number;
chart4Time:number;
currentDate:string;
isGetLoading:boolean=false;
isPostLoading:boolean=false;
isPutLoading:boolean=false;
isDeleteLoading:boolean=false;
  today: any ;
  constructor(public formBuilder: FormBuilder, public authService: AuthService,public datepipe: DatePipe) {


   }

  ngOnInit(): void {
 let map = new Map<string, string>();
 let uid = localStorage.getItem('uid');
 map.set('processorClassName', 'DateProvider');
 	this.authService.getproperties('auth', map).subscribe((res) => {
let sqldate=res['processedValue'];
this.currentDate=sqldate;
    this.initGETChart(sqldate);
   this.initPostChart(sqldate);
    this.initPutChart(sqldate);
    this.initDeleteChart(sqldate);

   });
  }
refreshChart(chartName:string)
{
if(chartName=='GET')        { this.initGETChart(this.currentDate);}
 else if(chartName=='POST') {  this.initPostChart(this.currentDate);}
 else if(chartName=='PUT'){    this.initPutChart(this.currentDate);}
 else if(chartName=='DELETE')   {this.initDeleteChart(this.currentDate);}
}
initGETChart(sqldate : string)
{
 this.isGetLoading=true;
 this.Time = [];
  this.ConsumeTime = [];
   let map = new Map<string, string>();
		let uid = localStorage.getItem('uid');
		map.set('uid', uid);
    map.set('method', 'GET');
    this.today  =new Date();

    map.set('date', sqldate);
   	this.authService.getdata(this.serviceConsumptionName, map).subscribe((res) => {
      this.data = res[this.serviceConsumptionName];
      for (let item of  this.data) {
        console.log(item); // Will display contents of the object inside the array
       let timepush: boolean =false;
       let durationpush: boolean =false;
        for (var key in item) {
          if(key=='time')
          {
            this.Time.push(item[key]);
            timepush=true;
          }
          if(key=='duration'){
          this.ConsumeTime.push(item[key]);
          durationpush=true;
          }

        }
        if(!timepush){
          if(durationpush){
            this.ConsumeTime.pop()
          }
        }
        if(!durationpush){
          if(timepush){
            this.Time.pop()
          }
        }

    }
    if(this.Linechart){
this.Linechart.destroy();
}
    this.Linechart = new Chart('canvas', {
      type: 'line',
      data: {
        labels: this.Time,

        datasets: [
          {
            label: 'Time Taken in ms For '+this.datepipe.transform(this.today, 'yyyy-MM-dd'),
            data: this.ConsumeTime,
            borderColor: '#3cb371',
            backgroundColor: "#0000FF",
          }
        ]
      },
      options: {
        legend: {
          display: false
        },
        scales: {
          xAxes: [{
            display: true,
            title: {
              display: true,
              text: 'Time',
              color: '#911',
              font: {
                family: 'Comic Sans MS',
                size: 20,
                style: 'bold',
                lineHeight: 1.2,
              },
              padding: {top: 20, left: 0, right: 0, bottom: 0}
            }
          }],
          yAxes: [{
            display: true,
            title: {
              display: true,
              text: 'Execution Time in Micro seconds',
              color: '#191',
              font: {
                family: 'Times',
                size: 20,
                style: 'normal',
                lineHeight: 1.2,
              },
              padding: {top: 30, left: 0, right: 0, bottom: 0}
            }
          }],
        }
      }
    });
    this.isLoadingget=false;
this.chart1Time= Date.now();
 this.isGetLoading=false;
    });
}
initPostChart(sqldate : string)
{
 this.isPostLoading=true;
this.Timepost = [];
 this. ConsumeTimepost = [];
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'POST');
  this.today  =new Date();

  map.set('date', sqldate);
   this.authService.getdata(this.serviceConsumptionName, map).subscribe((res) => {
    this.data = res[this.serviceConsumptionName];
    for (let item of  this.data) {
      console.log(item); // Will display contents of the object inside the array
     let timepush: boolean =false;
     let durationpush: boolean =false;
      for (var key in item) {
        if(key=='time')
        {
          this.Timepost.push(item[key]);
          timepush=true;
        }
        if(key=='duration'){
        this.ConsumeTimepost.push(item[key]);
        durationpush=true;
        }

      }
      if(!timepush){
        if(durationpush){
          this.ConsumeTimepost.pop()
        }
      }
      if(!durationpush){
        if(timepush){
          this.Timepost.pop()
        }
      }

  }
  if(this.LinechartPost){
  this.LinechartPost.destroy();
  }
  this.LinechartPost = new Chart('canvas1', {
    type: 'line',
    data: {
      labels: this.Timepost,

      datasets: [
        {
          label: 'Time Taken in ms For Post '+this.datepipe.transform(this.today, 'yyyy-MM-dd'),
          data: this.ConsumeTimepost,
          borderColor: '#3cb371',
          backgroundColor: "#0000FF",
        }
      ]
    },
    options: {
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Time',
            color: '#911',
            font: {
              family: 'Comic Sans MS',
              size: 20,
              style: 'bold',
              lineHeight: 1.2,
            },
            padding: {top: 20, left: 0, right: 0, bottom: 0}
          }
        }],
        yAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Execution Time in Micro seconds',
            color: '#191',
            font: {
              family: 'Times',
              size: 20,
              style: 'normal',
              lineHeight: 1.2,
            },
            padding: {top: 30, left: 0, right: 0, bottom: 0}
          }
        }],
      }
    }
  });
  this.isLoadingpost=false;
this.chart2Time= Date.now();
this.isPostLoading=false;
  });


}

initPutChart(sqldate : string)
{
this.Timeput = [];
 this.ConsumeTimeput = [];
this.isPutLoading=true;
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'PUT');
  this.today  =new Date();
  map.set('date', sqldate);
   this.authService.getdata(this.serviceConsumptionName, map).subscribe((res) => {
    this.data = res[this.serviceConsumptionName];
    for (let item of  this.data) {
      console.log(item); // Will display contents of the object inside the array
     let timepush: boolean =false;
     let durationpush: boolean =false;
      for (var key in item) {
        if(key=='time')
        {
          this.Timeput.push(item[key]);
          timepush=true;
        }
        if(key=='duration'){
        this.ConsumeTimeput.push(item[key]);
        durationpush=true;
        }

      }
      if(!timepush){
        if(durationpush){
          this.ConsumeTimeput.pop()
        }
      }
      if(!durationpush){
        if(timepush){
          this.Timeput.pop()
        }
      }

  }
  if(this.LinechartPUT){
  this.LinechartPUT.destroy();
  }
  this.LinechartPUT = new Chart('canvas2', {
    type: 'line',
    data: {
      labels: this.Timeput,

      datasets: [
        {
          label: 'Time Taken in ms For PUT '+this.datepipe.transform(this.today, 'yyyy-MM-dd'),
          data: this.ConsumeTimeput,
          borderColor: '#3cb371',
          backgroundColor: "#0000FF",
        }
      ]
    },
    options: {
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Time',
            color: '#911',
            font: {
              family: 'Comic Sans MS',
              size: 20,
              style: 'bold',
              lineHeight: 1.2,
            },
            padding: {top: 20, left: 0, right: 0, bottom: 0}
          }
        }],
        yAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Execution Time in Micro seconds',
            color: '#191',
            font: {
              family: 'Times',
              size: 20,
              style: 'normal',
              lineHeight: 1.2,
            },
            padding: {top: 30, left: 0, right: 0, bottom: 0}
          }
        }],
      }
    }
  });
  this.isLoadingput=false;
this.chart3Time= Date.now();
this.isPutLoading=false;
  });


}


initDeleteChart(sqldate : string)
{
this.Timedelete = [];
  this.ConsumeTimedelete = [];
this.isDeleteLoading=true;
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'DELETE');
  this.today  =new Date();

  map.set('date', sqldate);
   this.authService.getdata(this.serviceConsumptionName, map).subscribe((res) => {
    this.data = res[this.serviceConsumptionName];
    for (let item of  this.data) {
      console.log(item); // Will display contents of the object inside the array
     let timepush: boolean =false;
     let durationpush: boolean =false;
      for (var key in item) {
        if(key=='time')
        {
          this.Timedelete.push(item[key]);
          timepush=true;
        }
        if(key=='duration'){
        this.ConsumeTimedelete.push(item[key]);
        durationpush=true;
        }

      }
      if(!timepush){
        if(durationpush){
          this.ConsumeTimedelete.pop()
        }
      }
      if(!durationpush){
        if(timepush){
          this.Timedelete.pop()
        }
      }

  }
if(this.LinechartDelete){
  this.LinechartDelete.destroy();
  }
  this.LinechartDelete = new Chart('canvas3', {
    type: 'line',
    data: {
      labels: this.Timedelete,

      datasets: [
        {
          label: 'Time Taken in ms For Delete '+this.datepipe.transform(this.today, 'yyyy-MM-dd'),
          data: this.ConsumeTimedelete,
          borderColor: '#3cb371',
          backgroundColor: "#0000FF",
        }
      ]
    },
    options: {
      legend: {
        display: false
      },
      scales: {
        xAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Time',
            color: '#911',
            font: {
              family: 'Comic Sans MS',
              size: 20,
              style: 'bold',
              lineHeight: 1.2,
            },
            padding: {top: 20, left: 0, right: 0, bottom: 0}
          }
        }],
        yAxes: [{
          display: true,
          title: {
            display: true,
            text: 'Execution Time in Micro seconds',
            color: '#191',
            font: {
              family: 'Times',
              size: 20,
              style: 'normal',
              lineHeight: 1.2,
            },
            padding: {top: 30, left: 0, right: 0, bottom: 0}
          }
        }],
      }
    }
  });
  this.isLoadingdelete=false;
this.chart4Time= Date.now();
this.isDeleteLoading=false;
  });


}
 addEvent(type: string, event: MatDatepickerInputEvent<Date>) {
let sqldate: Date=event.value;
let picker_date =this.datepipe.transform(sqldate, 'yyyy-MM-dd');
if(type=='GET')        { this.initGETChart(picker_date);}
 else if(type=='POST') {  this.initPostChart(picker_date);}
 else if(type=='PUT'){    this.initPutChart(picker_date);}
 else if(type=='DELETE')   {this.initDeleteChart(picker_date);}
  }

}
