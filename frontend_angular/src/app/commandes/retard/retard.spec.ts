import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Retard } from './retard';

describe('Retard', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Retard);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
