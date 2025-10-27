import { Component } from '@angular/core';
import {AuthService} from '../../core/services/auth.service';

@Component({
  selector: 'app-home.component',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  constructor(
    protected authService: AuthService
  ) {

  }

  getLocalIp(): string {
    return this.authService.getLocalIp().toString();
  }
}
