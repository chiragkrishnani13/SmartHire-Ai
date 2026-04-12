import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ApiService } from '../../shared/services/api.service';
import { AuthService } from '../../shared/services/auth.service';

@Component({
  selector: 'app-applicant-profile',
  template: `
    <app-navbar></app-navbar>
    <div class="page-layout">
      <div class="fade-in">
        <div class="page-header">
          <div>
            <div class="section-label">My Account</div>
            <h2>Applicant Profile</h2>
            <p style="color:var(--text-secondary);font-size:14px;margin-top:6px">Keep your profile updated to improve ATS scores</p>
          </div>
          <div class="profile-completeness">
            <div class="complete-label">Profile Strength</div>
            <div class="progress" style="width:160px">
              <div class="progress-bar" [style.width]="completeness + '%'"></div>
            </div>
            <div class="complete-pct">{{ completeness }}%</div>
          </div>
        </div>

        <div class="profile-layout">
          <!-- Profile Card -->
          <div class="profile-side">
            <div class="card profile-card">
              <div class="avatar-lg">{{ initials }}</div>
              <h4>{{ auth.currentUser?.name }}</h4>
              <div class="user-email">{{ auth.currentUser?.email }}</div>
              <span class="badge badge-teal" style="margin-top:10px">APPLICANT</span>
            </div>

            <!-- Resume Upload -->
            <div class="card" style="margin-top:16px">
              <h4 style="margin-bottom:16px">📄 Resume</h4>
              <div class="resume-upload" [class.has-file]="resumeUrl"
                (click)="fileInput.click()" (dragover)="$event.preventDefault()" (drop)="onDrop($event)">
                <input #fileInput type="file" accept=".pdf,.doc,.docx" style="display:none" (change)="onFileSelect($event)">
                <div *ngIf="!resumeUrl && !uploading">
                  <div style="font-size:32px;margin-bottom:8px">📂</div>
                  <div style="font-size:13px;color:var(--text-secondary)">Click or drag to upload</div>
                  <div style="font-size:11px;color:var(--text-muted);margin-top:4px">PDF, DOC, DOCX</div>
                </div>
                <div *ngIf="uploading" style="text-align:center"><div class="spinner"></div><div style="margin-top:8px;font-size:12px">Uploading...</div></div>
                <div *ngIf="resumeUrl && !uploading">
                  <div style="font-size:28px">✅</div>
                  <div style="font-size:13px;color:var(--teal);margin-top:8px">Resume uploaded</div>
                  <div style="font-size:11px;color:var(--text-muted)">Click to replace</div>
                </div>
              </div>
              <div class="alert alert-error" *ngIf="uploadError" style="margin-top:10px">{{ uploadError }}</div>
            </div>
          </div>

          <!-- Profile Form -->
          <div class="profile-main">
            <div class="card">
              <div class="alert alert-success" *ngIf="success" style="margin-bottom:20px">{{ success }}</div>
              <div class="alert alert-error" *ngIf="error" style="margin-bottom:20px">{{ error }}</div>

              <form [formGroup]="form" (ngSubmit)="save()">
                <div class="form-section-title">Professional Info</div>
                <div class="grid-2" style="margin-bottom:16px">
                  <div class="form-group">
                    <label class="form-label">Current Title / Designation</label>
                    <input class="form-control" formControlName="currentTitle" placeholder="e.g. Software Engineer" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">Years of Experience</label>
                    <input class="form-control" type="number" formControlName="experience" placeholder="e.g. 3" />
                  </div>
                </div>

                <div class="form-group" style="margin-bottom:16px">
                  <label class="form-label">Skills (comma separated)</label>
                  <input class="form-control" formControlName="skills"
                    placeholder="e.g. Java, Spring Boot, React, MySQL" />
                </div>

                <div class="form-group" style="margin-bottom:16px">
                  <label class="form-label">Education</label>
                  <input class="form-control" formControlName="education"
                    placeholder="e.g. B.Tech Computer Science, Mumbai University" />
                </div>

                <div class="form-group" style="margin-bottom:16px">
                  <label class="form-label">About / Summary</label>
                  <textarea class="form-control" formControlName="summary" rows="4"
                    placeholder="Brief summary about yourself, your goals, and expertise..."></textarea>
                </div>

                <div class="form-section-title" style="margin-top:24px">Contact & Links</div>
                <div class="grid-2" style="margin-bottom:16px">
                  <div class="form-group">
                    <label class="form-label">Phone</label>
                    <input class="form-control" formControlName="phone" placeholder="+91 XXXXX XXXXX" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">Location</label>
                    <input class="form-control" formControlName="location" placeholder="e.g. Mumbai, Maharashtra" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">LinkedIn URL</label>
                    <input class="form-control" formControlName="linkedIn" placeholder="linkedin.com/in/yourprofile" />
                  </div>
                  <div class="form-group">
                    <label class="form-label">GitHub URL</label>
                    <input class="form-control" formControlName="github" placeholder="github.com/yourusername" />
                  </div>
                </div>

                <div style="display:flex;justify-content:flex-end;gap:12px;margin-top:8px">
                  <button type="submit" class="btn btn-primary btn-lg" [disabled]="saving">
                    <span *ngIf="!saving">💾 Save Profile</span>
                    <div *ngIf="saving" class="spinner" style="width:18px;height:18px;border-width:2px"></div>
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .page-layout { padding: 32px 40px; max-width: 1100px; margin: 0 auto; }
    .page-header { display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 28px; }
    .page-header h2 { font-size: 2rem; }
    .profile-completeness { text-align: right; display: flex; flex-direction: column; gap: 6px; align-items: flex-end; }
    .complete-label { font-size: 11px; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.08em; }
    .complete-pct { font-size: 13px; font-weight: 700; color: var(--teal); }

    .profile-layout { display: grid; grid-template-columns: 260px 1fr; gap: 20px; }

    .profile-card { text-align: center; padding: 28px 20px; }
    .avatar-lg {
      width: 72px; height: 72px; border-radius: 50%;
      background: linear-gradient(135deg, var(--teal-glow), var(--bg-elevated));
      border: 2px solid rgba(0,229,195,0.3);
      display: flex; align-items: center; justify-content: center;
      font-family: var(--font-display); font-size: 1.6rem; font-weight: 800; color: var(--teal);
      margin: 0 auto 14px;
    }
    .profile-card h4 { font-size: 1rem; margin-bottom: 4px; }
    .user-email { font-size: 12px; color: var(--text-muted); }

    .resume-upload {
      border: 2px dashed var(--border); border-radius: var(--radius-md);
      padding: 28px 16px; text-align: center; cursor: pointer;
      transition: var(--transition);
    }
    .resume-upload:hover { border-color: var(--teal); background: var(--teal-glow); }
    .resume-upload.has-file { border-color: rgba(0,229,195,0.3); background: var(--teal-glow); }

    .form-section-title {
      font-family: var(--font-display); font-size: 0.95rem; font-weight: 700;
      color: var(--teal); margin-bottom: 18px;
      padding-bottom: 10px; border-bottom: 1px solid var(--border);
    }

    @media(max-width: 900px) { .profile-layout { grid-template-columns: 1fr; } }
  `]
})
export class ApplicantProfileComponent implements OnInit {
  form = this.fb.group({
    currentTitle: [''], experience: [''], skills: [''],
    education: [''], summary: [''], phone: [''],
    location: [''], linkedIn: [''], github: ['']
  });
  saving = false; success = ''; error = '';
  uploading = false; resumeUrl = ''; uploadError = '';
  isNew = true;

  get initials(): string {
    return (this.auth.currentUser?.name || '').split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  get completeness(): number {
    const vals = Object.values(this.form.value);
    const filled = vals.filter(v => v && String(v).trim()).length;
    return Math.round((filled / vals.length) * 100);
  }

  constructor(private fb: FormBuilder, private api: ApiService, public auth: AuthService) {}

  ngOnInit() {
    this.api.getMyProfile().subscribe({
      next: (p) => { if (p) { this.form.patchValue(p); this.resumeUrl = p.resumeUrl || ''; this.isNew = false; } },
      error: () => {}
    });
  }

  save() {
    this.saving = true; this.error = '';
    const call = this.isNew ? this.api.saveProfile(this.form.value) : this.api.updateProfile(this.form.value);
    call.subscribe({
      next: () => { this.success = 'Profile saved successfully!'; this.saving = false; this.isNew = false; setTimeout(() => this.success = '', 3000); },
      error: (e) => { this.error = e.error?.message || 'Failed to save profile'; this.saving = false; }
    });
  }

  onFileSelect(event: any) {
    const file = event.target.files[0];
    if (file) this.uploadFile(file);
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file) this.uploadFile(file);
  }

  uploadFile(file: File) {
    this.uploading = true; this.uploadError = '';
    this.api.uploadResume(file).subscribe({
      next: (res) => { this.resumeUrl = res.url || 'uploaded'; this.uploading = false; },
      error: (e) => { this.uploadError = e.error?.message || 'Upload failed'; this.uploading = false; }
    });
  }
}
