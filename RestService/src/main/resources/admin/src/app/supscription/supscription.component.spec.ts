import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SupscriptionComponent } from './supscription.component';

describe('SupscriptionComponent', () => {
  let component: SupscriptionComponent;
  let fixture: ComponentFixture<SupscriptionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SupscriptionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SupscriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
