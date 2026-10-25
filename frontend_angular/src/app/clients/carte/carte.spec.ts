import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { Carte } from './carte';

describe('Carte', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Carte);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
