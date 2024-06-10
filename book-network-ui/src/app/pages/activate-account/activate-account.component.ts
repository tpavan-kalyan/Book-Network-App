import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/services';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.scss']
})
export class ActivateAccountComponent {
  onCodeCompleted(token: string) {
    this.confirmAccount(token);
  } 
  redirectToLogin() {
    this.router.navigate(['login']);
  }
  message: string = '';
  isOkay: boolean = true;
  submitted: boolean = false;

  private confirmAccount(token: string) {
    this.authService.confirm({
      token
    }).subscribe({
      next: () => {
        this.message = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.submitted = true;
      },
      error: () => {
        this.message = 'Token has been expired or invalid';
        this.submitted = true;
        this.isOkay = false;
      }
    });
  }

  constructor(
    private router: Router,
    private authService: AuthenticationService
  ) { }
}
