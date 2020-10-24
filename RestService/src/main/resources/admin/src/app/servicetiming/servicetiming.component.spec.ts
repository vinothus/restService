import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicetimingComponent } from './servicetiming.component';

describe('ServicetimingComponent', () => {
  let component: ServicetimingComponent;
  let fixture: ComponentFixture<ServicetimingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ServicetimingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ServicetimingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
