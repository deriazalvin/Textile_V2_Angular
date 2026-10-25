import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Livraison } from './livraison';

describe('Livraison', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Livraison);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
