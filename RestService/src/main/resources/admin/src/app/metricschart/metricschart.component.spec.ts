import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricschartComponent } from './metricschart.component';

describe('MetricschartComponent', () => {
  let component: MetricschartComponent;
  let fixture: ComponentFixture<MetricschartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MetricschartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MetricschartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
