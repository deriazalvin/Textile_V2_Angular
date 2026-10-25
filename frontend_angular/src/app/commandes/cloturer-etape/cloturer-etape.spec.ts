import { TestBed } from '@angular/core/testing';
import { AppModule } from '../../app.module';
import { CloturerEtape } from './cloturer-etape';

describe('CloturerEtape', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppModule],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(CloturerEtape);
    expect(fixture.componentInstance).toBeTruthy();
  });
});
