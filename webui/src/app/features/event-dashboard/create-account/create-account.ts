import { Component } from '@angular/core';
import {AccountRoleType} from "../../../core/define/AccounRoleType";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-create-account',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './create-account.html',
  styleUrl: './create-account.css'
})
export class CreateAccount {
  protected readonly AccountRoleType = AccountRoleType;
  protected passwordVisible: boolean = false;

  createAccountForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {
    this.createAccountForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
      reEnterPassword: ['', Validators.required],
      role: [0, Validators.required]
    });
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  onSubmit() {
    const formValues = this.createAccountForm.value;
    if (this.validatePasswords(formValues.password, formValues.reEnterPassword)) {
      // Proceed with account creation logic
      console.log('Account creation data:', formValues);
      let credentials = {
        username: formValues.username,
        password: formValues.password,
        role: formValues.role
      }
      this.authService.createAccount(credentials).subscribe({
        next: () => {
          console.log('Account created successfully.');
          alert("Account created successfully.");
          this.createAccountForm.reset();
        },
        error(err) {
          alert("Error creating account: " + err.error.message);
          console.error('Error creating account:', err);
        },
        complete() {
          console.log('Account creation request completed.');
        }
      })
    } else {
      // Handle password mismatch
      console.error('Passwords do not match.');
      alert("Passwords do not match.");
    }
  }

  validatePasswords(password: string, confirmPassword: string): boolean {
    return password === confirmPassword;
  }

}
