import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Formulaire } from './formulaire';

describe('Formulaire', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Formulaire);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
