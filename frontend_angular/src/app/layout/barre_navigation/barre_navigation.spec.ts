import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Barre_navigation } from './barre_navigation';

describe('Barre_navigation', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Barre_navigation);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
