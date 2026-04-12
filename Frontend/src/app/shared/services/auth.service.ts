import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface User {
  id: number;
  name: string;
  email: string;
  role: 'APPLICANT' | 'RECRUITER';
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private API = 'http://localhost:8080';
  private userSubject = new BehaviorSubject<User | null>(this.loadUser());
  user$ = this.userSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  private loadUser(): User | null {
    const u = localStorage.getItem('smarthire_user');
    return u ? JSON.parse(u) : null;
  }

  get currentUser(): User | null { return this.userSubject.value; }
  get isLoggedIn(): boolean { return !!this.currentUser; }
  get isRecruiter(): boolean { return this.currentUser?.role === 'RECRUITER'; }
  get isApplicant(): boolean { return this.currentUser?.role === 'APPLICANT'; }

  register(data: { name: string; email: string; password: string; role: string }): Observable<any> {
    return this.http.post(`${this.API}/auth/register`, data);
  }

  login(email: string, password: string): Observable<any> {
    // Basic Auth header sent by interceptor once credentials stored
    localStorage.setItem('smarthire_creds', btoa(`${email}:${password}`));
    return this.http.get<any[]>(`${this.API}/auth/users`).pipe(
      tap((users: any[]) => {
        const match = users.find((u: any) => u.email === email);
        if (match) {
          const user: User = { id: match.id, name: match.name, email: match.email, role: match.role };
          localStorage.setItem('smarthire_user', JSON.stringify(user));
          this.userSubject.next(user);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('smarthire_user');
    localStorage.removeItem('smarthire_creds');
    this.userSubject.next(null);
    this.router.navigate(['/login']);
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.API}/auth/forget-password`, { email });
  }

  getCredentials(): string | null {
    return localStorage.getItem('smarthire_creds');
  }
}
