import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../shared/services/api.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-applications',
  template: `
    <app-navbar></app-navbar>
    <div class="page-layout">
      <div class="fade-in">
        <div class="page-header">
          <div>
            <div class="section-label">Applicant</div>
            <h2>My Applications</h2>
          </div>
          <a routerLink="/jobs" class="btn btn-primary">Browse More Jobs</a>
        </div>

        <!-- Status Filter -->
        <div class="filter-row">
          <button class="filter-btn" [class.active]="filter === 'all'" (click)="filter='all'">All ({{ applications.length }})</button>
          <button class="filter-btn" [class.active]="filter === 'APPLIED'" (click)="filter='APPLIED'">Applied</button>
          <button class="filter-btn" [class.active]="filter === 'SELECTED'" (click)="filter='SELECTED'">Selected</button>
          <button class="filter-btn" [class.active]="filter === 'REJECTED'" (click)="filter='REJECTED'">Rejected</button>
        </div>

        <div *ngIf="loading" style="padding:80px;text-align:center"><div class="spinner"></div></div>

        <div *ngIf="!loading && filtered.length === 0" class="empty-state">
          <div class="empty-state-icon">📋</div>
          <h3>No applications found</h3>
          <p>Start applying to jobs to track your progress here</p>
          <a routerLink="/jobs" class="btn btn-primary" style="margin-top:16px">Browse Jobs</a>
        </div>

        <div class="apps-list" *ngIf="!loading">
          <div class="app-card card" *ngFor="let app of filtered">
            <div class="app-card-header">
              <div class="job-logo">{{ (app.jobTitle || 'J').charAt(0) }}</div>
              <div class="app-info">
                <h4>{{ app.jobTitle || 'Job #' + app.jobId }}</h4>
                <div class="app-meta">
                  <span>📍 {{ app.location || 'Location N/A' }}</span>
                  <span>Applied {{ formatDate(app.appliedAt) }}</span>
                </div>
              </div>
              <span class="badge" [ngClass]="getBadgeClass(app.status)">{{ app.status || 'APPLIED' }}</span>
            </div>

            <!-- Status Timeline -->
            <div class="status-timeline">
              <div class="timeline-step" [class.done]="true" [class.active]="app.status === 'APPLIED' || !app.status">
                <div class="t-dot"></div>
                <div class="t-label">Applied</div>
              </div>
              <div class="timeline-line"></div>
              <div class="timeline-step" [class.done]="app.status === 'SELECTED' || app.status === 'ROUND2'" [class.rejected]="app.status === 'REJECTED'">
                <div class="t-dot"></div>
                <div class="t-label">ATS Review</div>
              </div>
              <div class="timeline-line"></div>
              <div class="timeline-step" [class.done]="app.status === 'ROUND2'">
                <div class="t-dot"></div>
                <div class="t-label">Round 2</div>
              </div>
            </div>

            <div class="app-card-footer">
              <button class="btn btn-ghost btn-sm" (click)="viewJob(app.jobId)">View Job</button>
              <button class="btn btn-outline btn-sm" *ngIf="app.status === 'SELECTED'" (click)="takeTest(app.jobId)">
                🎯 Take MCQ Test
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-layout { padding: 32px 40px; max-width: 900px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 24px; }
    .page-header h2 { font-size: 2rem; margin-top: 6px; }

    .filter-row { display: flex; gap: 8px; margin-bottom: 24px; }
    .filter-btn {
      padding: 7px 18px; border-radius: 100px;
      background: var(--bg-card); border: 1px solid var(--border);
      color: var(--text-secondary); font-size: 13px; font-weight: 500; cursor: pointer;
      transition: var(--transition);
    }
    .filter-btn:hover { border-color: var(--border-light); }
    .filter-btn.active { background: var(--teal-glow); border-color: var(--teal); color: var(--teal); }

    .apps-list { display: flex; flex-direction: column; gap: 16px; }
    .app-card { padding: 24px; }
    .app-card-header { display: flex; gap: 14px; align-items: flex-start; margin-bottom: 20px; }
    .job-logo {
      width: 44px; height: 44px; border-radius: var(--radius-sm);
      background: linear-gradient(135deg, var(--teal-glow), var(--bg-elevated));
      border: 1px solid rgba(0,229,195,0.2);
      display: flex; align-items: center; justify-content: center;
      font-family: var(--font-display); font-size: 1.1rem; font-weight: 800; color: var(--teal);
      flex-shrink: 0;
    }
    .app-info { flex: 1; }
    .app-info h4 { font-size: 1rem; margin-bottom: 6px; }
    .app-meta { display: flex; gap: 16px; font-size: 12px; color: var(--text-muted); }

    .status-timeline {
      display: flex; align-items: center;
      background: var(--bg-elevated); border-radius: var(--radius-sm);
      padding: 14px 20px; margin-bottom: 16px;
    }
    .timeline-step { display: flex; flex-direction: column; align-items: center; gap: 6px; }
    .t-dot {
      width: 12px; height: 12px; border-radius: 50%;
      background: var(--border); border: 2px solid var(--border-light);
      transition: var(--transition);
    }
    .timeline-step.done .t-dot { background: var(--teal); border-color: var(--teal); box-shadow: 0 0 8px rgba(0,229,195,0.4); }
    .timeline-step.active .t-dot { background: var(--amber); border-color: var(--amber); }
    .timeline-step.rejected .t-dot { background: var(--red); border-color: var(--red); }
    .t-label { font-size: 11px; color: var(--text-muted); white-space: nowrap; }
    .timeline-line { flex: 1; height: 2px; background: var(--border); margin: 0 8px; margin-bottom: 18px; }

    .app-card-footer { display: flex; gap: 10px; justify-content: flex-end; }
  `]
})
export class MyApplicationsComponent implements OnInit {
  applications: any[] = [];
  loading = true;
  filter = 'all';

  get filtered(): any[] {
    if (this.filter === 'all') return this.applications;
    return this.applications.filter(a => (a.status || 'APPLIED') === this.filter);
  }

  constructor(private api: ApiService, private router: Router) {}

  ngOnInit() {
    this.api.getMyApplications().subscribe({
      next: (d) => { this.applications = d || []; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  viewJob(id: number) { this.router.navigate(['/jobs', id]); }
  takeTest(jobId: number) { this.router.navigate(['/mcq', jobId]); }

  getBadgeClass(s: string): string {
    const m: any = { APPLIED: 'badge-teal', SELECTED: 'badge-amber', REJECTED: 'badge-red', ROUND2: 'badge-purple' };
    return m[s] || 'badge-teal';
  }

  formatDate(d: string): string {
    if (!d) return 'recently';
    return new Date(d).toLocaleDateString('en-IN', { day: 'numeric', month: 'short' });
  }
}
