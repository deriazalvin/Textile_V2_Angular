import { TestBed } from '@angular/core/testing';
import { AppModule } from '../app.module';
import { Statistisques } from './statistisques';

describe('Statistisques', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Statistisques);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
