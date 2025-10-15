import { Component } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './core/services/auth.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule],
  templateUrl: './app.component.html',
  styles: [`
    .nav-link.active {
      font-weight: bold;
      color: #0d6efd !important;
      border-bottom: 2px solid #0d6efd;
    }
    .navbar {
      padding: 0.8rem 1rem;
    }
  `]
})
export class App {
  isLoggedIn = false;

  constructor(private authService: AuthService, private router: Router) {
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
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
