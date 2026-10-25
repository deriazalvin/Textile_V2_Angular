import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Production } from './production';

describe('Production', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Production);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
