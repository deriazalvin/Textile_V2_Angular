import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Detail } from './detail';

describe('Detail', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Detail);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
