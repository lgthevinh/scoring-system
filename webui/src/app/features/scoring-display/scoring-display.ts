import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-field-display',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './scoring-display.html',
  styleUrl: './scoring-display.css'
})
export class ScoringDisplay implements OnInit, OnDestroy {
  // Display state
  durationSec: WritableSignal<number> = signal(150);
  timeLeft: WritableSignal<number> = signal(this.durationSec());
  running: WritableSignal<boolean> = signal(false);

  // Teams and scores (placeholder values; wire to your services later)
  redTeams: WritableSignal<string[]> = signal(["Team 1", "Team 2", "Team 3"]);
  blueTeams: WritableSignal<string[]> = signal(["Team 4", "Team 5", "Team 6"]);
  redScore: WritableSignal<number> = signal(0);
  blueScore: WritableSignal<number> = signal(0);

  // Fullscreen state
  isFullscreen: WritableSignal<boolean> = signal(false);

  // Gear control fade
  controlsVisible: WritableSignal<boolean> = signal(true);
  private hideTimer: any = null;
  private tickTimer: any = null;

  // Track fullscreen changes
  private onFullscreenChange = () => {
    const isFs =
      !!document.fullscreenElement ||
      !!(document as any).webkitFullscreenElement ||
      !!(document as any).mozFullScreenElement ||
      !!(document as any).msFullscreenElement;
    this.isFullscreen.set(isFs);
  };

  ngOnInit(): void {
    this.resetHideTimer();
    document.addEventListener('fullscreenchange', this.onFullscreenChange);
    // Safari/legacy vendor events (no-ops if unsupported)
    document.addEventListener('webkitfullscreenchange' as any, this.onFullscreenChange as any);
    document.addEventListener('mozfullscreenchange' as any, this.onFullscreenChange as any);
    document.addEventListener('MSFullscreenChange' as any, this.onFullscreenChange as any);
  }

  ngOnDestroy(): void {
    this.clearTick();
    this.clearHideTimer();
    document.removeEventListener('fullscreenchange', this.onFullscreenChange);
    document.removeEventListener('webkitfullscreenchange' as any, this.onFullscreenChange as any);
    document.removeEventListener('mozfullscreenchange' as any, this.onFullscreenChange as any);
    document.removeEventListener('MSFullscreenChange' as any, this.onFullscreenChange as any);
  }

  // ========== Fullscreen Button ==========
  // Hook this to the modal button: (click)="toggleFullscreen()"
  toggleFullscreen() {
    if (!this.isFullscreen()) {
      this.enterFullscreen();
    } else {
      this.exitFullscreen();
    }
  }

  private enterFullscreen() {
    // Prefer the whole document; swap to a specific element if you want only the canvas:
    // const el = document.querySelector('.frame') as HTMLElement || document.documentElement;
    const el: any = document.documentElement;

    const req =
      el.requestFullscreen?.bind(el) ||
      el.webkitRequestFullscreen?.bind(el) ||   // Safari
      el.mozRequestFullScreen?.bind(el) ||      // Firefox old
      el.msRequestFullscreen?.bind(el);         // IE/Edge old

    if (req) {
      try {
        const p = req();
        if (p && typeof p.then === 'function') {
          (p as Promise<void>).catch(err => console.warn('Fullscreen request failed:', err));
        }
      } catch (e) {
        console.warn('Fullscreen request threw:', e);
      }
    } else {
      console.warn('Fullscreen API not supported on this element/browser.');
    }
  }

  private exitFullscreen() {
    const anyDoc: any = document;
    const exit =
      document.exitFullscreen?.bind(document) ||
      anyDoc.webkitExitFullscreen?.bind(anyDoc) ||  // Safari
      anyDoc.mozCancelFullScreen?.bind(anyDoc) ||   // Firefox old
      anyDoc.msExitFullscreen?.bind(anyDoc);        // IE/Edge old

    if (exit) {
      try {
        const p = exit();
        if (p && typeof p.then === 'function') {
          (p as Promise<void>).catch(err => console.warn('Exit fullscreen failed:', err));
        }
      } catch (e) {
        console.warn('Exit fullscreen threw:', e);
      }
    }
  }
  // ======================================

  // Timer logic
  start() {
    if (this.running()) return;
    if (this.timeLeft() <= 0) this.timeLeft.set(this.durationSec());
    this.running.set(true);
    this.clearTick();
    this.tickTimer = setInterval(() => {
      const t = this.timeLeft() - 1;
      this.timeLeft.set(t);
      if (t <= 0) {
        this.stop();
        this.timeLeft.set(0);
      }
    }, 1000);
  }

  stop() {
    this.running.set(false);
    this.clearTick();
  }

  reset() {
    this.stop();
    this.timeLeft.set(this.durationSec());
  }

  setDurationFromForm(minutes: number, seconds: number) {
    const total = Math.max(0, Math.floor(minutes) * 60 + Math.floor(seconds));
    this.durationSec.set(total);
    if (!this.running()) this.timeLeft.set(total);
  }

  // Formatting
  mmss(): string {
    const total = Math.max(0, this.timeLeft());
    const m = Math.floor(total / 60);
    const s = total % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  }

  // Controls fade/show
  revealControls() {
    this.controlsVisible.set(true);
    this.resetHideTimer();
  }

  onAnyInteract() {
    this.revealControls();
  }

  private resetHideTimer() {
    this.clearHideTimer();
    this.hideTimer = setTimeout(() => this.controlsVisible.set(false), 5000);
  }

  private clearHideTimer() {
    if (this.hideTimer) {
      clearTimeout(this.hideTimer);
      this.hideTimer = null;
    }
  }

  private clearTick() {
    if (this.tickTimer) {
      clearInterval(this.tickTimer);
      this.tickTimer = null;
    }
  }
}
