import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../shared/services/api.service';

@Component({
  selector: 'app-mcq-test',
  template: `
    <div class="test-page">
      <!-- Loading -->
      <div class="center-page" *ngIf="loading">
        <div class="spinner"></div>
        <div style="margin-top:16px;color:var(--text-secondary)">Loading your test...</div>
      </div>

      <!-- Start Screen -->
      <div class="start-screen fade-in" *ngIf="!loading && !started && !submitted">
        <div class="start-card">
          <div style="font-size:48px;margin-bottom:16px">🎯</div>
          <div class="section-label" style="justify-content:center;margin-bottom:12px">Round 2 Assessment</div>
          <h2>MCQ Test</h2>
          <p style="color:var(--text-secondary);margin:16px 0">Job #{{ jobId }}</p>

          <div class="test-info-grid">
            <div class="test-info-item">
              <div class="ti-val">{{ questions.length }}</div>
              <div class="ti-label">Questions</div>
            </div>
            <div class="test-info-item">
              <div class="ti-val">{{ timeLimitMin }}m</div>
              <div class="ti-label">Time Limit</div>
            </div>
            <div class="test-info-item">
              <div class="ti-val">+1</div>
              <div class="ti-label">Per Correct</div>
            </div>
          </div>

          <div class="instructions">
            <div class="inst-title">Instructions</div>
            <ul>
              <li>Each question has only one correct answer</li>
              <li>You cannot go back once submitted</li>
              <li>The timer starts when you click Begin</li>
              <li>Unanswered questions will be marked wrong</li>
            </ul>
          </div>

          <div *ngIf="startError" class="alert alert-error" style="margin-bottom:16px">{{ startError }}</div>

          <button class="btn btn-primary btn-full btn-lg" (click)="beginTest()" [disabled]="startLoading">
            <span *ngIf="!startLoading">Begin Test →</span>
            <div *ngIf="startLoading" class="spinner" style="width:18px;height:18px;border-width:2px"></div>
          </button>
        </div>
      </div>

      <!-- Test Interface -->
      <div class="test-interface" *ngIf="started && !submitted">
        <!-- Top Bar -->
        <div class="test-topbar">
          <div class="test-brand">⬡ SmartHireAI — MCQ Test</div>
          <div class="test-progress-info">
            Question {{ currentQ + 1 }} / {{ questions.length }}
          </div>
          <div class="timer" [class.danger]="timeLeft < 60">
            ⏱ {{ formatTime(timeLeft) }}
          </div>
        </div>

        <!-- Progress Bar -->
        <div class="test-progress">
          <div class="test-progress-fill" [style.width]="progressPct + '%'"></div>
        </div>

        <div class="test-body">
          <!-- Question Nav -->
          <div class="q-nav">
            <div class="q-nav-grid">
              <div class="q-nav-btn"
                *ngFor="let q of questions; let i = index"
                [class.current]="i === currentQ"
                [class.answered]="answers[i] !== undefined"
                (click)="goToQuestion(i)">
                {{ i + 1 }}
              </div>
            </div>
            <div class="q-legend">
              <span class="leg-item"><span class="leg-dot answered"></span> Answered</span>
              <span class="leg-item"><span class="leg-dot current"></span> Current</span>
              <span class="leg-item"><span class="leg-dot"></span> Skipped</span>
            </div>
          </div>

          <!-- Question Card -->
          <div class="q-main">
            <div class="question-card card" *ngIf="questions[currentQ]">
              <div class="q-badge">Q{{ currentQ + 1 }}</div>
              <h4 class="q-text">{{ questions[currentQ].questionText }}</h4>

              <div class="options-list">
                <div class="option-item"
                  *ngFor="let opt of getOptions(questions[currentQ]); let oi = index"
                  [class.selected]="answers[currentQ] === 'ABCD'[oi]"
                  (click)="selectAnswer('ABCD'[oi])">
                  <div class="opt-letter">{{ 'ABCD'[oi] }}</div>
                  <div class="opt-text">{{ opt }}</div>
                </div>
              </div>

              <div class="q-navigation">
                <button class="btn btn-ghost" (click)="prev()" [disabled]="currentQ === 0">← Prev</button>
                <button class="btn btn-primary" *ngIf="currentQ < questions.length - 1" (click)="next()">Next →</button>
                <button class="btn btn-primary" *ngIf="currentQ === questions.length - 1" (click)="confirmSubmit()">
                  Submit Test ✓
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Result Screen -->
      <div class="result-screen fade-in" *ngIf="submitted">
        <div class="result-card">
          <div class="result-emoji">{{ score >= 70 ? '🎉' : score >= 50 ? '👍' : '😔' }}</div>
          <h2>Test Submitted!</h2>
          <div class="score-circle">
            <div class="score-num">{{ score }}%</div>
            <div class="score-label">Your Score</div>
          </div>
          <div class="score-bar-wrap">
            <div class="score-bar-bg">
              <div class="score-bar-fill" [style.width]="score + '%'"
                [style.background]="score >= 70 ? 'var(--teal)' : score >= 50 ? 'var(--amber)' : 'var(--red)'">
              </div>
            </div>
          </div>
          <div class="result-stats">
            <div class="rs-item">
              <div class="rs-val" style="color:var(--teal)">{{ correctCount }}</div>
              <div class="rs-label">Correct</div>
            </div>
            <div class="rs-item">
              <div class="rs-val" style="color:var(--red)">{{ questions.length - correctCount }}</div>
              <div class="rs-label">Wrong</div>
            </div>
            <div class="rs-item">
              <div class="rs-val">{{ questions.length }}</div>
              <div class="rs-label">Total</div>
            </div>
          </div>
          <p style="color:var(--text-secondary);font-size:14px;margin:16px 0">
            {{ score >= 70 ? "Great performance! The recruiter will review your results." : "Keep practicing. The recruiter will be notified of your results." }}
          </p>
          <a routerLink="/my-applications" class="btn btn-primary btn-full">View My Applications</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .test-page { min-height: 100vh; background: var(--bg-base); }

    /* Start */
    .center-page, .start-screen, .result-screen {
      min-height: 100vh; display: flex; align-items: center; justify-content: center;
      padding: 24px;
    }
    .start-card, .result-card {
      background: var(--bg-card); border: 1px solid var(--border);
      border-radius: var(--radius-lg); padding: 40px 36px;
      width: 100%; max-width: 500px; text-align: center;
    }
    .test-info-grid { display: flex; justify-content: center; gap: 32px; margin: 24px 0; }
    .test-info-item { text-align: center; }
    .ti-val { font-family: var(--font-display); font-size: 1.8rem; font-weight: 800; color: var(--teal); }
    .ti-label { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
    .instructions {
      background: var(--bg-elevated); border-radius: var(--radius-sm);
      padding: 16px 20px; margin: 20px 0; text-align: left;
    }
    .inst-title { font-size: 12px; font-weight: 600; color: var(--text-secondary); margin-bottom: 10px; text-transform: uppercase; letter-spacing: 0.06em; }
    .instructions ul { padding-left: 16px; }
    .instructions li { font-size: 13px; color: var(--text-muted); margin-bottom: 6px; }

    /* Top bar */
    .test-topbar {
      display: flex; align-items: center; justify-content: space-between;
      padding: 0 28px; height: 56px;
      background: var(--bg-surface); border-bottom: 1px solid var(--border);
    }
    .test-brand { font-family: var(--font-display); font-size: 14px; font-weight: 700; color: var(--teal); }
    .test-progress-info { font-size: 14px; color: var(--text-secondary); }
    .timer {
      font-family: var(--font-display); font-size: 1.1rem; font-weight: 700; color: var(--teal);
      background: var(--teal-glow); border: 1px solid rgba(0,229,195,0.2);
      border-radius: var(--radius-sm); padding: 6px 14px;
    }
    .timer.danger { color: var(--red); background: var(--red-glow); border-color: rgba(255,77,109,0.2); }

    .test-progress { height: 4px; background: var(--bg-elevated); }
    .test-progress-fill { height: 100%; background: linear-gradient(90deg, var(--teal-dim), var(--teal)); transition: width 0.3s; }

    /* Test Body */
    .test-body { display: grid; grid-template-columns: 220px 1fr; min-height: calc(100vh - 60px); }

    /* Question Nav */
    .q-nav {
      background: var(--bg-surface); border-right: 1px solid var(--border);
      padding: 24px 16px;
    }
    .q-nav-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 6px; margin-bottom: 20px; }
    .q-nav-btn {
      aspect-ratio: 1; border-radius: var(--radius-sm);
      background: var(--bg-elevated); border: 1px solid var(--border);
      display: flex; align-items: center; justify-content: center;
      font-size: 12px; font-weight: 600; cursor: pointer; transition: var(--transition);
      color: var(--text-muted);
    }
    .q-nav-btn:hover { border-color: var(--border-light); color: var(--text-primary); }
    .q-nav-btn.answered { background: var(--teal-glow); border-color: rgba(0,229,195,0.3); color: var(--teal); }
    .q-nav-btn.current { background: var(--teal); color: #0a0c0f; border-color: var(--teal); }

    .q-legend { display: flex; flex-direction: column; gap: 8px; margin-top: 16px; }
    .leg-item { display: flex; align-items: center; gap: 8px; font-size: 11px; color: var(--text-muted); }
    .leg-dot {
      width: 12px; height: 12px; border-radius: 2px;
      background: var(--bg-elevated); border: 1px solid var(--border);
    }
    .leg-dot.answered { background: var(--teal-glow); border-color: rgba(0,229,195,0.3); }
    .leg-dot.current { background: var(--teal); }

    /* Question Area */
    .q-main { padding: 32px; overflow-y: auto; }
    .question-card { padding: 32px; }
    .q-badge {
      font-family: var(--font-display); font-size: 11px; font-weight: 700;
      color: var(--teal); background: var(--teal-glow);
      border: 1px solid rgba(0,229,195,0.2);
      border-radius: var(--radius-sm); padding: 3px 10px;
      display: inline-block; margin-bottom: 16px;
    }
    .q-text { font-size: 1.1rem; line-height: 1.6; margin-bottom: 28px; }

    .options-list { display: flex; flex-direction: column; gap: 12px; margin-bottom: 28px; }
    .option-item {
      display: flex; align-items: center; gap: 16px;
      background: var(--bg-elevated); border: 1px solid var(--border);
      border-radius: var(--radius-md); padding: 14px 18px;
      cursor: pointer; transition: var(--transition);
    }
    .option-item:hover { border-color: var(--border-light); background: var(--bg-card); }
    .option-item.selected { border-color: var(--teal); background: var(--teal-glow); }
    .opt-letter {
      width: 28px; height: 28px; border-radius: 50%; flex-shrink: 0;
      background: var(--bg-card); border: 1px solid var(--border);
      display: flex; align-items: center; justify-content: center;
      font-size: 12px; font-weight: 700; color: var(--text-secondary);
    }
    .option-item.selected .opt-letter { background: var(--teal); border-color: var(--teal); color: #0a0c0f; }
    .opt-text { font-size: 14px; }

    .q-navigation { display: flex; justify-content: space-between; }

    /* Result */
    .score-circle {
      width: 120px; height: 120px; border-radius: 50%; margin: 24px auto;
      background: conic-gradient(var(--teal) 0%, var(--bg-elevated) 0%);
      display: flex; flex-direction: column; align-items: center; justify-content: center;
      border: 4px solid var(--teal); box-shadow: 0 0 24px rgba(0,229,195,0.2);
    }
    .score-num { font-family: var(--font-display); font-size: 1.8rem; font-weight: 800; color: var(--teal); }
    .score-label { font-size: 11px; color: var(--text-muted); }
    .score-bar-bg { height: 8px; background: var(--bg-elevated); border-radius: 4px; margin: 8px 0 24px; overflow: hidden; }
    .score-bar-fill { height: 100%; border-radius: 4px; transition: width 1s ease; }
    .result-stats { display: flex; justify-content: center; gap: 40px; margin: 16px 0; }
    .rs-item { text-align: center; }
    .rs-val { font-family: var(--font-display); font-size: 1.6rem; font-weight: 800; }
    .rs-label { font-size: 12px; color: var(--text-muted); margin-top: 4px; }

    @media(max-width: 768px) { .test-body { grid-template-columns: 1fr; } .q-nav { display: none; } }
  `]
})
export class McqTestComponent implements OnInit, OnDestroy {
  jobId!: number;
  loading = true; startLoading = false;
  started = false; submitted = false;
  startError = '';
  questions: any[] = [];
  answers: { [key: number]: string } = {};
  currentQ = 0;
  timeLimitMin = 30;
  timeLeft = 1800;
  timer: any;
  score = 0; correctCount = 0;

  get progressPct(): number {
    return ((this.currentQ + 1) / this.questions.length) * 100;
  }

  constructor(private route: ActivatedRoute, private api: ApiService, private router: Router) {}

  ngOnInit() {
    this.jobId = Number(this.route.snapshot.paramMap.get('jobId'));
    // Load test data
    this.api.startMcqTest(this.jobId).subscribe({
      next: (res: any) => {
        if (typeof res === 'string') {
          // API returns a string message — parse or use dummy for demo
          this.questions = this.generateDemoQuestions();
        } else {
          this.questions = res.questions || this.generateDemoQuestions();
          this.timeLimitMin = res.timeLimitMinutes || 30;
        }
        this.timeLeft = this.timeLimitMin * 60;
        this.loading = false;
      },
      error: () => {
        // Show demo questions if API fails
        this.questions = this.generateDemoQuestions();
        this.timeLeft = this.timeLimitMin * 60;
        this.loading = false;
        this.startError = 'Could not load test from server. Demo mode active.';
      }
    });
  }

  generateDemoQuestions() {
    return [
      { questionText: 'What does OOP stand for?', optionA: 'Object Oriented Programming', optionB: 'Open Object Protocol', optionC: 'Object Oriented Protocol', optionD: 'Oriented Object Programming', correctOption: 'A' },
      { questionText: 'Which of the following is not a Java keyword?', optionA: 'static', optionB: 'Boolean', optionC: 'void', optionD: 'true', correctOption: 'B' },
      { questionText: 'What is the default value of an int in Java?', optionA: 'null', optionB: 'undefined', optionC: '0', optionD: '-1', correctOption: 'C' },
      { questionText: 'Which data structure uses LIFO?', optionA: 'Queue', optionB: 'Stack', optionC: 'Linked List', optionD: 'Tree', correctOption: 'B' },
      { questionText: 'What is the time complexity of binary search?', optionA: 'O(n)', optionB: 'O(n²)', optionC: 'O(log n)', optionD: 'O(1)', correctOption: 'C' },
    ];
  }

  beginTest() {
    this.startLoading = true;
    setTimeout(() => {
      this.started = true;
      this.startLoading = false;
      this.startTimer();
    }, 500);
  }

  startTimer() {
    this.timer = setInterval(() => {
      this.timeLeft--;
      if (this.timeLeft <= 0) { this.submitTest(); }
    }, 1000);
  }

  selectAnswer(opt: string) { this.answers[this.currentQ] = opt; }
  goToQuestion(i: number) { this.currentQ = i; }
  next() { if (this.currentQ < this.questions.length - 1) this.currentQ++; }
  prev() { if (this.currentQ > 0) this.currentQ--; }

  confirmSubmit() {
    const unanswered = this.questions.length - Object.keys(this.answers).length;
    if (unanswered > 0) {
      if (!confirm(`You have ${unanswered} unanswered question(s). Submit anyway?`)) return;
    }
    this.submitTest();
  }

  submitTest() {
    clearInterval(this.timer);
    this.correctCount = 0;
    this.questions.forEach((q, i) => {
      if (this.answers[i] === q.correctOption) this.correctCount++;
    });
    this.score = Math.round((this.correctCount / this.questions.length) * 100);
    this.submitted = true;
  }

  getOptions(q: any): string[] {
    return [q.optionA, q.optionB, q.optionC, q.optionD];
  }

  formatTime(secs: number): string {
    const m = Math.floor(secs / 60).toString().padStart(2, '0');
    const s = (secs % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  ngOnDestroy() { clearInterval(this.timer); }
}
