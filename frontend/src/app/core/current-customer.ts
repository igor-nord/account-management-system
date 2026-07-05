import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'currentUsername';

@Injectable({ providedIn: 'root' })
export class CurrentCustomer {
  private readonly currentUsername = signal<string | null>(readStored());
  readonly username = this.currentUsername.asReadonly();

  set(username: string): void {
    this.currentUsername.set(username);
    localStorage.setItem(STORAGE_KEY, username);
  }

  clear(): void {
    this.currentUsername.set(null);
    localStorage.removeItem(STORAGE_KEY);
  }
}

function readStored(): string | null {
  return localStorage.getItem(STORAGE_KEY);
}
