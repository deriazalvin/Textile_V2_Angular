import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Utilisateurs } from './utilisateurs';

describe('Utilisateurs', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Utilisateurs);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
