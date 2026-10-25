import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Liste } from './liste';

describe('Liste', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Liste);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
