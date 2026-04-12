import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  readonly BASE = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // ── JOBS ──────────────────────────────────────
  getAllJobs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE}/job/all`);
  }
  getJobById(id: number): Observable<any> {
    return this.http.get<any>(`${this.BASE}/job/${id}`);
  }
  createJob(job: any): Observable<any> {
    return this.http.post(`${this.BASE}/job/savejobdetails`, job);
  }
  updateJob(id: number, job: any): Observable<any> {
    return this.http.put(`${this.BASE}/job/update/${id}`, job);
  }
  deleteJob(id: number): Observable<any> {
    return this.http.patch(`${this.BASE}/job/delete/${id}`, {});
  }
  searchJobs(keyword: string, location: string): Observable<any[]> {
    let params = new HttpParams();
    if (keyword) params = params.set('keyword', keyword);
    if (location) params = params.set('location', location);
    return this.http.get<any[]>(`${this.BASE}/job/search`, { params });
  }

  // ── APPLICATIONS ──────────────────────────────
  applyJob(jobId: number): Observable<any> {
    return this.http.post(`${this.BASE}/applications/apply/${jobId}`, {});
  }
  getMyApplications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE}/applications/my`);
  }
  getApplicantsForJob(jobId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.BASE}/applications/job/${jobId}`);
  }
  getApplicationStatus(jobId: number): Observable<any> {
    return this.http.get(`${this.BASE}/applications/status/${jobId}`);
  }

  // ── APPLICANT PROFILE ─────────────────────────
  getMyProfile(): Observable<any> {
    return this.http.get(`${this.BASE}/applicant/profile`);
  }
  saveProfile(profile: any): Observable<any> {
    return this.http.post(`${this.BASE}/applicant/profile`, profile);
  }
  updateProfile(profile: any): Observable<any> {
    return this.http.put(`${this.BASE}/applicant/profile`, profile);
  }
  uploadResume(file: File): Observable<any> {
    const fd = new FormData();
    fd.append('file', file);
    return this.http.post(`${this.BASE}/applicant/upload-resume`, fd);
  }

  // ── ATS ───────────────────────────────────────
  calculateAts(resume: File, jobDescription: string, applicantId: number, jobId: number): Observable<any> {
    const fd = new FormData();
    fd.append('resume', resume);
    fd.append('jobDescription', jobDescription);
    fd.append('applicantId', applicantId.toString());
    fd.append('jobId', jobId.toString());
    return this.http.post(`${this.BASE}/ats/calculate`, fd);
  }

  // ── ROUND 1 ───────────────────────────────────
  selectCandidate(jobId: number, applicantId: number): Observable<any> {
    return this.http.post(`${this.BASE}/round1/select`, { jobId, applicantId });
  }
  rejectCandidate(jobId: number, applicantId: number): Observable<any> {
    return this.http.post(`${this.BASE}/round1/reject`, { jobId, applicantId });
  }

  // ── MCQ ───────────────────────────────────────
  createMcqTest(request: any): Observable<any> {
    return this.http.post(`${this.BASE}/round2/mcq/create`, request);
  }
  releaseMcqTest(jobId: number): Observable<any> {
    return this.http.post(`${this.BASE}/round2/mcq/release/${jobId}`, {});
  }
  startMcqTest(jobId: number): Observable<any> {
    return this.http.get(`${this.BASE}/round2/mcq/start/${jobId}`);
  }
}
