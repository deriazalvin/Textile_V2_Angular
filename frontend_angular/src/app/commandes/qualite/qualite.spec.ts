import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Qualite } from './qualite';

describe('Qualite', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Qualite);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
