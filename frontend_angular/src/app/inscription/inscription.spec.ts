import { TestBed } from '@angular/core/testing';
import { AppModule } from '../app.module';
import { Inscription } from './inscription';

describe('Inscription', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Inscription);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
