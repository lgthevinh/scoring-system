import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.component.html'
})
export class App implements OnInit {
  isLoggedIn = false;
  localIp: string = '';
  showNavbar = true;

  constructor(protected authService: AuthService, private router: Router) {
    this.authService.isAuthenticated().subscribe(isAuth => {
      this.isLoggedIn = isAuth;
    });

    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      this.authService.isAuthenticated().subscribe(isAuth => {
        this.isLoggedIn = isAuth;
        if (!isAuth && !this.router.url.includes('/auth')) {
          this.router.navigate(['/auth']);
        }
      });
    });

    this.localIp = this.authService.getLocalIp();
    console.log('Local IP:', this.localIp);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }

  ngOnInit() {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        // List of routes where navbar should be hidden
        const hiddenNavbarRoutes = ['/match-controller'];
        this.showNavbar = !hiddenNavbarRoutes.includes(event.urlAfterRedirects);
      });
  }
}
