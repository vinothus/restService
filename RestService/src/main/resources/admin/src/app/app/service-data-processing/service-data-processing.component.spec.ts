import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServiceDataProcessingComponent } from './service-data-processing.component';

describe('ServiceDataProcessingComponent', () => {
  let component: ServiceDataProcessingComponent;
  let fixture: ComponentFixture<ServiceDataProcessingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServiceDataProcessingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServiceDataProcessingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
