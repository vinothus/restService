import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TunnelingComponent } from './tunneling.component';

describe('TunnelingComponent', () => {
  let component: TunnelingComponent;
  let fixture: ComponentFixture<TunnelingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TunnelingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TunnelingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
