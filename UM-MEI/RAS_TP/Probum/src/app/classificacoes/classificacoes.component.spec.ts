import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClassificacoesComponent } from './classificacoes.component';

describe('ClassificacoesComponent', () => {
  let component: ClassificacoesComponent;
  let fixture: ComponentFixture<ClassificacoesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassificacoesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ClassificacoesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
