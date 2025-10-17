import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent {
  authForm: FormGroup;
  errorMessage: string = '';
  loading: boolean = false;
  passwordVisible: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.authForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit() {
    if (this.authForm.valid) {
      this.loading = true;
      this.errorMessage = '';
      this.authService.login(this.authForm.value).subscribe({
        next: () => {
          this.router.navigate(['/matches']);
        },
        error: (err) => {
          this.errorMessage = 'Invalid username or password.';
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        }
      });
    }
  }

}
