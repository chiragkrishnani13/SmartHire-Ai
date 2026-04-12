import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../shared/services/api.service';

@Component({
  selector: 'app-recruiter-applicants',
  template: `
    <app-navbar></app-navbar>
    <div class="page-layout">
      <div class="fade-in">
        <button class="btn btn-ghost btn-sm" (click)="back()">← Back to Dashboard</button>

        <div class="page-header">
          <div>
            <div class="section-label">Hiring Pipeline</div>
            <h2>Applicants for Job #{{ jobId }}</h2>
          </div>
          <div class="pipeline-actions">
            <button class="btn btn-outline" (click)="manageMcq()">🎯 Manage MCQ Test</button>
          </div>
        </div>

        <!-- Stats -->
        <div class="stats-row" *ngIf="!loading">
          <div class="stat-card">
            <div class="stat-value">{{ applicants.length }}</div>
            <div class="stat-label">Total Applicants</div>
          </div>
          <div class="stat-card">
            <div class="stat-value" style="color:var(--amber)">{{ selected }}</div>
            <div class="stat-label">Selected for R2</div>
          </div>
          <div class="stat-card">
            <div class="stat-value" style="color:var(--red)">{{ rejected }}</div>
            <div class="stat-label">Rejected</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ pending }}</div>
            <div class="stat-label">Pending Review</div>
          </div>
        </div>

        <!-- ATS Upload Section -->
        <div class="card ats-section" style="margin-bottom:24px">
          <h4 style="margin-bottom:6px">🤖 ATS Bulk Screening</h4>
          <p style="font-size:13px;color:var(--text-secondary);margin-bottom:16px">
            Upload a resume and job description to calculate AI-powered ATS match score
          </p>
          <div class="ats-inputs">
            <div class="form-group" style="flex:1">
              <label class="form-label">Job Description (for ATS scoring)</label>
              <textarea class="form-control" [(ngModel)]="jdText" rows="3"
                placeholder="Paste job description here for ATS matching..."></textarea>
            </div>
            <div class="ats-upload-side">
              <label class="form-label">Resume (PDF)</label>
              <div class="resume-drop" (click)="resumeInput.click()">
                <input #resumeInput type="file" accept=".pdf" style="display:none" (change)="onResumeSelect($event)">
                <span *ngIf="!atsResume">📂 Upload Resume</span>
                <span *ngIf="atsResume" style="color:var(--teal)">✅ {{ atsResume.name }}</span>
              </div>
              <div class="form-group" style="margin-top:10px">
                <label class="form-label">Applicant ID</label>
                <input class="form-control" type="number" [(ngModel)]="atsApplicantId" placeholder="Applicant ID" />
              </div>
              <button class="btn btn-primary btn-full" style="margin-top:10px" (click)="runAts()" [disabled]="atsLoading">
                <span *ngIf="!atsLoading">Calculate ATS Score</span>
                <div *ngIf="atsLoading" class="spinner" style="width:16px;height:16px;border-width:2px"></div>
              </button>
            </div>
          </div>
          <div class="ats-result card" *ngIf="atsResult" style="margin-top:16px;border-color:rgba(0,229,195,0.3)">
            <div class="ats-score-row">
              <div class="ats-score-val">{{ atsResult.score || atsResult.atsScore || 0 }}%</div>
              <div class="ats-score-info">
                <div class="ats-score-label">ATS Match Score</div>
                <div class="progress" style="width:200px;margin-top:6px">
                  <div class="progress-bar" [style.width]="(atsResult.score || atsResult.atsScore || 0) + '%'"></div>
                </div>
              </div>
            </div>
            <div class="ats-keywords" *ngIf="atsResult.matchedKeywords">
              <div style="font-size:12px;color:var(--text-muted);margin-bottom:8px">Matched Keywords</div>
              <div class="tags">
                <span class="tag" *ngFor="let k of atsResult.matchedKeywords">{{ k }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Applicants Table -->
        <div *ngIf="loading" style="padding:60px;text-align:center"><div class="spinner"></div></div>

        <div *ngIf="!loading && applicants.length === 0" class="empty-state card">
          <div class="empty-state-icon">👥</div>
          <h3>No applicants yet</h3>
          <p>Share the job posting to start receiving applications</p>
        </div>

        <div class="table-wrap" *ngIf="!loading && applicants.length > 0">
          <table>
            <thead>
              <tr>
                <th>Applicant</th>
                <th>Email</th>
                <th>Applied</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let app of applicants">
                <td>
                  <div style="display:flex;align-items:center;gap:10px">
                    <div class="mini-avatar">{{ (app.applicantName || 'A').charAt(0) }}</div>
                    <span>{{ app.applicantName || 'Applicant #' + app.applicantId }}</span>
                  </div>
                </td>
                <td style="color:var(--text-secondary)">{{ app.email || '—' }}</td>
                <td style="color:var(--text-muted);font-size:13px">{{ formatDate(app.appliedAt) }}</td>
                <td>
                  <span class="badge" [ngClass]="getBadgeClass(app.status)">{{ app.status || 'APPLIED' }}</span>
                </td>
                <td>
                  <div class="action-btns">
                    <button class="btn btn-outline btn-sm"
                      (click)="select(app.applicantId)"
                      [disabled]="app.status === 'SELECTED' || actionLoading[app.applicantId]">
                      ✅ Select
                    </button>
                    <button class="btn btn-danger btn-sm"
                      (click)="reject(app.applicantId)"
                      [disabled]="app.status === 'REJECTED' || actionLoading[app.applicantId]">
                      ✗ Reject
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="alert alert-success" *ngIf="actionMsg" style="margin-top:16px">{{ actionMsg }}</div>
      </div>
    </div>
  `,
  styles: [`
    .page-layout { padding: 32px 40px; max-width: 1100px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-end; margin: 20px 0 24px; }
    .page-header h2 { font-size: 2rem; }
    .stats-row { display: grid; grid-template-columns: repeat(4,1fr); gap: 16px; margin-bottom: 24px; }

    .ats-section { padding: 24px; }
    .ats-inputs { display: flex; gap: 20px; align-items: flex-start; }
    .ats-upload-side { width: 260px; flex-shrink: 0; }
    .resume-drop {
      border: 2px dashed var(--border); border-radius: var(--radius-sm);
      padding: 14px; text-align: center; cursor: pointer;
      font-size: 13px; color: var(--text-secondary);
      transition: var(--transition);
    }
    .resume-drop:hover { border-color: var(--teal); color: var(--teal); }

    .ats-result { padding: 20px; }
    .ats-score-row { display: flex; align-items: center; gap: 20px; margin-bottom: 16px; }
    .ats-score-val { font-family: var(--font-display); font-size: 3rem; font-weight: 800; color: var(--teal); }
    .ats-score-label { font-size: 13px; color: var(--text-secondary); }

    .mini-avatar {
      width: 28px; height: 28px; border-radius: 50%;
      background: var(--teal-glow); border: 1px solid rgba(0,229,195,0.2);
      display: flex; align-items: center; justify-content: center;
      font-size: 11px; font-weight: 700; color: var(--teal); flex-shrink: 0;
    }
    .action-btns { display: flex; gap: 8px; }

    @media(max-width:900px) {
      .stats-row { grid-template-columns: repeat(2,1fr); }
      .ats-inputs { flex-direction: column; }
      .ats-upload-side { width: 100%; }
    }
  `]
})
export class RecruiterApplicantsComponent implements OnInit {
  jobId!: number;
  applicants: any[] = [];
  loading = true;
  actionLoading: any = {};
  actionMsg = '';
  atsResume: File | null = null;
  jdText = '';
  atsApplicantId: number | null = null;
  atsLoading = false;
  atsResult: any = null;

  get selected() { return this.applicants.filter(a => a.status === 'SELECTED').length; }
  get rejected() { return this.applicants.filter(a => a.status === 'REJECTED').length; }
  get pending() { return this.applicants.filter(a => !a.status || a.status === 'APPLIED').length; }

  constructor(private route: ActivatedRoute, private api: ApiService, private router: Router) {}

  ngOnInit() {
    this.jobId = Number(this.route.snapshot.paramMap.get('jobId'));
    this.loadApplicants();
  }

  loadApplicants() {
    this.api.getApplicantsForJob(this.jobId).subscribe({
      next: (d) => { this.applicants = d || []; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  select(applicantId: number) {
    this.actionLoading[applicantId] = true;
    this.api.selectCandidate(this.jobId, applicantId).subscribe({
      next: () => {
        const a = this.applicants.find(x => x.applicantId === applicantId);
        if (a) a.status = 'SELECTED';
        this.actionMsg = 'Candidate selected for Round 2!';
        this.actionLoading[applicantId] = false;
        setTimeout(() => this.actionMsg = '', 3000);
      },
      error: () => { this.actionLoading[applicantId] = false; }
    });
  }

  reject(applicantId: number) {
    this.actionLoading[applicantId] = true;
    this.api.rejectCandidate(this.jobId, applicantId).subscribe({
      next: () => {
        const a = this.applicants.find(x => x.applicantId === applicantId);
        if (a) a.status = 'REJECTED';
        this.actionMsg = 'Candidate rejected.';
        this.actionLoading[applicantId] = false;
        setTimeout(() => this.actionMsg = '', 3000);
      },
      error: () => { this.actionLoading[applicantId] = false; }
    });
  }

  onResumeSelect(event: any) {
    this.atsResume = event.target.files[0] || null;
  }

  runAts() {
    if (!this.atsResume || !this.jdText || !this.atsApplicantId) return;
    this.atsLoading = true; this.atsResult = null;
    this.api.calculateAts(this.atsResume, this.jdText, this.atsApplicantId, this.jobId).subscribe({
      next: (res) => { this.atsResult = res; this.atsLoading = false; },
      error: () => { this.atsLoading = false; }
    });
  }

  manageMcq() { this.router.navigate(['/recruiter/mcq', this.jobId]); }
  back() { this.router.navigate(['/recruiter/dashboard']); }

  getBadgeClass(s: string): string {
    const m: any = { APPLIED: 'badge-teal', SELECTED: 'badge-amber', REJECTED: 'badge-red' };
    return m[s] || 'badge-teal';
  }

  formatDate(d: string): string {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' });
  }
}
