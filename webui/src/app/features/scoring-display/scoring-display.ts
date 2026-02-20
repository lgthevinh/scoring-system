import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {BroadcastService} from "../../core/services/broadcast.service";
import {FieldDisplayCommand} from '../../core/define/FieldDisplayCommand';
import {SyncService} from '../../core/services/sync.service';
import {Team} from '../../core/models/team.model';
import {MatchDetailDto} from '../../core/models/match.model';

@Component({
    selector: 'app-field-display',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './scoring-display.html',
    styleUrl: './scoring-display.css'
})
export class ScoringDisplay implements OnInit, OnDestroy {
    // Display mode: 'match' shows scoring timer, 'upnext' shows up-next preview
    displayMode: WritableSignal<string> = signal('match');

    // Display state
    durationSec: WritableSignal<number> = signal(180);
    timeLeft: WritableSignal<number> = signal(this.durationSec());
    running: WritableSignal<boolean> = signal(false);

    // Teams and scores (placeholder values; wire to your services later)
    redTeams: WritableSignal<Team[]> = signal([]);
    blueTeams: WritableSignal<Team[]> = signal([]);
    redScore: WritableSignal<number> = signal(0);
    blueScore: WritableSignal<number> = signal(0);

    // Up Next data
    upNextMatchCode: WritableSignal<string> = signal('');
    upNextFieldNumber: WritableSignal<number> = signal(0);
    upNextScheduledTime: WritableSignal<string> = signal('');
    upNextRedTeams: WritableSignal<Team[]> = signal([]);
    upNextBlueTeams: WritableSignal<Team[]> = signal([]);
    hasUpNext: WritableSignal<boolean> = signal(false);

    // Fullscreen state
    isFullscreen: WritableSignal<boolean> = signal(false);

    // Gear control fade
    controlsVisible: WritableSignal<boolean> = signal(true);
    private hideTimer: any = null;
    private tickTimer: any = null;

    fieldBindValue: number = 0;

    constructor(
        private syncService: SyncService,
        private broadcastService: BroadcastService
    ) {}

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
        this.syncService.getCurrentMatchField(0).subscribe({
            next: (match) => {
                console.log("Fetched current match field data:", match);

                if (match !== null) {
                    this.redTeams.set(match.redTeams);
                    this.blueTeams.set(match.blueTeams);
                    this.redScore.set(match.redScore?.totalScore || 0);
                    this.blueScore.set(match.blueScore?.totalScore || 0);
                }},
            error: (err) => {
                console.error("Error fetching current match field data:", err.message);
            }
        })

        this.subscribeToFieldTopic(0);

        // Notify that the field display is active
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

    onFieldBindChange(event: Event) {
        const target = event.target as HTMLSelectElement;
        this.fieldBindValue = target.value ? parseInt(target.value, 10) : 0;

        // Unsubscribe from all previous field topics
        this.broadcastService.unsubscribeAll();
        this.subscribeToFieldTopic(this.fieldBindValue!);

        this.syncService.getCurrentMatchField(this.fieldBindValue).subscribe({
            next: (match) => {
                console.log("Fetched current match field data:", match);

                if (match !== null) {
                    this.redTeams.set(match.redTeams);
                    this.blueTeams.set(match.blueTeams);
                }
            },
            error: (err) => {
                console.error("Error fetching current match field data:", err.message);
            }
        });
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

    private subscribeToFieldTopic(fieldId: number) {
        if (fieldId === null || fieldId === undefined) return;

        let timerTopic: string;
        let commandTopic: string;
        let scoreTopicRed: string;
        let scoreTopicBlue: string;

        if (fieldId === 0) {
            timerTopic = `/topic/display/field/*/timer`;
            commandTopic = `/topic/display/field/*/command`;
            scoreTopicRed = `/topic/live/field/*/score/red`;
            scoreTopicBlue = `/topic/live/field/*/score/blue`;
        } else {
            timerTopic = `/topic/display/field/${fieldId}/timer`;
            commandTopic = `/topic/display/field/${fieldId}/command`;
            scoreTopicRed = `/topic/live/field/${fieldId}/score/red`;
            scoreTopicBlue = `/topic/live/field/${fieldId}/score/blue`;
        }

        this.broadcastService.subscribeToTopic(commandTopic).subscribe({
            next: (msg) => {
                console.log("FieldDisplay received message:", msg);
                if (msg.type === FieldDisplayCommand.SHOW_TIMER) {
                    console.log("FieldDisplay SHOW_TIMER command received");
                    this.displayMode.set('match');

                    this.redTeams.set(msg.payload.redTeams);
                    this.blueTeams.set(msg.payload.blueTeams);

                    this.redScore.set(0);
                    this.blueScore.set(0);
                } else if (msg.type === FieldDisplayCommand.SHOW_UPNEXT) {
                    console.log("FieldDisplay SHOW_UPNEXT command received");
                    this.applyUpNextData(msg.payload);
                    this.displayMode.set('upnext');
                } else if (msg.type === FieldDisplayCommand.SHOW_MATCH) {
                    console.log("FieldDisplay SHOW_MATCH command received");
                    if (msg.payload) {
                        this.redTeams.set(msg.payload.redTeams);
                        this.blueTeams.set(msg.payload.blueTeams);
                        this.redScore.set(msg.payload.redScore?.totalScore || 0);
                        this.blueScore.set(msg.payload.blueScore?.totalScore || 0);
                    }
                    this.displayMode.set('match');
                }
            },
            error: (err) => {
                console.error("FieldDisplay message error:", err);
            }
        });

        // Also subscribe to the broadcast-all command topic for display commands from match-control
        if (fieldId !== 0) {
            this.broadcastService.subscribeToTopic('/topic/display/field/0/command').subscribe({
                next: (msg) => {
                    console.log("FieldDisplay received broadcast-all command:", msg);
                    if (msg.type === FieldDisplayCommand.SHOW_UPNEXT) {
                        this.applyUpNextData(msg.payload);
                        this.displayMode.set('upnext');
                    } else if (msg.type === FieldDisplayCommand.SHOW_MATCH) {
                        if (msg.payload) {
                            this.redTeams.set(msg.payload.redTeams);
                            this.blueTeams.set(msg.payload.blueTeams);
                            this.redScore.set(msg.payload.redScore?.totalScore || 0);
                            this.blueScore.set(msg.payload.blueScore?.totalScore || 0);
                        }
                        this.displayMode.set('match');
                    }
                },
                error: (err) => {
                    console.error("FieldDisplay broadcast-all command error:", err);
                }
            });
        }

        this.broadcastService.subscribeToTopic(timerTopic).subscribe({
            next: (msg) => {
                console.debug("FieldDisplay received timer message:", msg);
                if (msg.payload && msg.payload.remainingSeconds !== undefined) {
                    this.timeLeft.set(msg.payload.remainingSeconds);
                }
            },
            error: (err) => {
                console.error("FieldDisplay timer message error:", err);
            }
        });

        this.broadcastService.subscribeToTopic(scoreTopicRed).subscribe({
            next: (msg) => {
                console.debug("FieldDisplay received score message:", msg);
                if (msg.payload) {
                    this.redScore.set(msg.payload.totalScore);
                }
            },
            error: (err) => {
                console.error("FieldDisplay score message error:", err);
            }
        });

        this.broadcastService.subscribeToTopic(scoreTopicBlue).subscribe({
            next: (msg) => {
                console.debug("FieldDisplay received score message:", msg);
                if (msg.payload) {
                    this.blueScore.set(msg.payload.totalScore);
                }
            },
            error: (err) => {
                console.error("FieldDisplay score message error:", err);
            }
        });
    }

    private unsubscribeFromFieldTopic(fieldId: number) {
        if (fieldId === null || fieldId === undefined) return;

        if (fieldId === 0) {
            this.broadcastService.unsubscribeFromTopic(`/topic/display/field/*/command`);
            this.broadcastService.unsubscribeFromTopic(`/topic/display/field/*/timer`);
            this.broadcastService.unsubscribeFromTopic(`/topic/live/field/*/score`);
            this.broadcastService.unsubscribeFromTopic(`/topic/live/field/*/score/red`);
            this.broadcastService.unsubscribeFromTopic(`/topic/live/field/*/score/blue`);
            return;
        }

        this.broadcastService.unsubscribeFromTopic(`/topic/display/field/${fieldId}/command`);
        this.broadcastService.unsubscribeFromTopic(`/topic/display/field/${fieldId}/timer`);
        this.broadcastService.unsubscribeFromTopic(`/topic/live/field/${fieldId}/score/red`);
        this.broadcastService.unsubscribeFromTopic(`/topic/live/field/${fieldId}/score/blue`);
    }

    // ========== Up Next Helpers ==========
    private applyUpNextData(match: MatchDetailDto): void {
        if (!match) {
            this.hasUpNext.set(false);
            return;
        }
        this.upNextMatchCode.set(match.match?.matchCode || '');
        this.upNextFieldNumber.set(match.match?.fieldNumber || 0);
        this.upNextScheduledTime.set(match.match?.matchStartTime || '');
        this.upNextRedTeams.set(match.redTeams || []);
        this.upNextBlueTeams.set(match.blueTeams || []);
        this.hasUpNext.set(true);
    }

    formatScheduledTime(): string {
        if (!this.upNextScheduledTime()) return '';
        try {
            const date = new Date(this.upNextScheduledTime());
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        } catch {
            return this.upNextScheduledTime();
        }
    }
}
