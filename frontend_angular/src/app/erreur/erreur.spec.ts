import { TestBed } from '@angular/core/testing';
import { AppModule } from '../app.module';
import { Erreur } from './erreur';

describe('Erreur', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Erreur);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
