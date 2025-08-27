import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.removeItem('baccaratio_token');
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should store the token after login', () => {
    const mockCredentials = { username: 'testuser', password: 'password' };
    const mockResponse = { token: 'test-jwt-token' };

    service.login(mockCredentials).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);

    expect(localStorage.getItem('baccaratio_token')).toBe(mockResponse.token);
  });

  it('should clear the token on logout', () => {
    localStorage.setItem('baccaratio_token', 'some-token');
    service.logout();
    expect(localStorage.getItem('baccaratio_token')).toBeNull();
  });

  it('isLoggedIn should return true when token exists', () => {
    localStorage.setItem('baccaratio_token', 'some-token');
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('isLoggedIn should return false when token does not exist', () => {
    expect(service.isLoggedIn()).toBeFalse();
  });
});
