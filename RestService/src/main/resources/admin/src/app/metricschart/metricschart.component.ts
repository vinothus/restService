import { Component, OnInit } from '@angular/core';
import { Chart } from 'chart.js'; 
import { AuthService } from "../auth/auth-service.service"; 
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { DatePipe } from '@angular/common'
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
  Linechart = []; 
  isLoadingget = true;
  LinechartPost = []; 
  isLoadingpost = true;
  LinechartPUT = []; 
  isLoadingput = true;
  LinechartDelete = []; 
  isLoadingdelete = true;
  serviceConsumptionName ='service consumption'; 
  today: any ;
  constructor(public formBuilder: FormBuilder, public authService: AuthService,public datepipe: DatePipe) {
    let map = new Map<string, string>();
		let uid = localStorage.getItem('uid');
		map.set('uid', uid);
    map.set('method', 'GET');
    this.today  =new Date();
    let latest_date =this.datepipe.transform(this.today, 'yyyy-MM-dd');
    map.set('date', latest_date);
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

    });

   }
 
  ngOnInit(): void {
   this.initPostChart();
    this.initPutChart();
    this.initDeleteChart();
  }

initPostChart()
{
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'POST');
  this.today  =new Date();
  let latest_date =this.datepipe.transform(this.today, 'yyyy-MM-dd');
  map.set('date', latest_date);
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

  });


}
 
initPutChart()
{
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'PUT');
  this.today  =new Date();
  let latest_date =this.datepipe.transform(this.today, 'yyyy-MM-dd');
  map.set('date', latest_date);
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

  });


} 


initDeleteChart()
{
  let map = new Map<string, string>();
  let uid = localStorage.getItem('uid');
  map.set('uid', uid);
  map.set('method', 'DELETE');
  this.today  =new Date();
  let latest_date =this.datepipe.transform(this.today, 'yyyy-MM-dd');
  map.set('date', latest_date);
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
  
  this.LinechartPost = new Chart('canvas3', {  
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

  });


}

}
