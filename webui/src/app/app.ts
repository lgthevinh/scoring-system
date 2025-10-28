import {Component, OnInit, signal, WritableSignal} from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';
import {filter, Observable} from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.component.html'
})
export class App implements OnInit {
  isLoggedIn: WritableSignal<boolean> = signal(false);
  showNavbar = true;
  localIp: Observable<string> | undefined;

  constructor(protected authService: AuthService, private router: Router) {

    this.authService.isAuthenticated().subscribe(isAuth => {
      this.isLoggedIn.set(isAuth);
      if (!isAuth && !this.router.url.includes('/auth')) {
        this.router.navigate(['/auth']);
      }
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.authService.isAuthenticated().subscribe(isAuth => {
        this.isLoggedIn.set(isAuth);
        if (!isAuth && !this.router.url.includes('/auth')) {
          this.router.navigate(['/auth']);
        }
      });
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  ngOnInit() {
    if (this.localIp === undefined) {
      this.localIp = this.authService.getLocalIp();
    }

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        // List of routes where navbar should be hidden
        const hiddenNavbarRoutes = ['/match-control'];
        this.showNavbar = !hiddenNavbarRoutes.includes(event.urlAfterRedirects);
      });

  }
}
