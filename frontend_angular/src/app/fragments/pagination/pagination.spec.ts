import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Pagination } from './pagination';

describe('Pagination', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Pagination);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
