import { DOCUMENT } from '@angular/common';
import { Injectable, inject } from '@angular/core';

export interface ToastOptions {
  timeOut?: number;
  closeButton?: boolean;
  progressBar?: boolean;
  preventDuplicates?: boolean;
  positionClass?: string;
  toastClass?: string;
  titleClass?: string;
}

type ToastKind = 'success' | 'error' | 'info' | 'warning';

@Injectable({ providedIn: 'root' })
export class ToastService {
  private readonly document = inject(DOCUMENT);
  private readonly active = new Set<HTMLElement>();

  success(message: string, title = '', options?: ToastOptions): void { this.show('success', message, title, options); }
  error(message: string, title = '', options?: ToastOptions): void { this.show('error', message, title, options); }
  info(message: string, title = '', options?: ToastOptions): void { this.show('info', message, title, options); }
  warning(message: string, title = '', options?: ToastOptions): void { this.show('warning', message, title, options); }

  clear(): void {
    for (const toast of this.active) toast.remove();
    this.active.clear();
  }

  private show(kind: ToastKind, message: string, title: string, options: ToastOptions = {}): void {
    const body = this.document.body;
    if (!body) return;
    const key = `${kind}:${title}:${message}`;
    if (options.preventDuplicates !== false && [...this.active].some(toast => toast.dataset['toastKey'] === key)) return;

    let container = body.querySelector<HTMLElement>('.app-toast-container');
    if (!container) {
      container = this.document.createElement('div');
      container.className = `app-toast-container ${options.positionClass ?? 'toast-top-center'}`;
      container.setAttribute('aria-live', kind === 'error' ? 'assertive' : 'polite');
      body.appendChild(container);
    }
    const toast = this.document.createElement('div');
    toast.className = `app-toast app-toast-${kind} ${options.toastClass ?? ''}`.trim();
    toast.dataset['toastKey'] = key;
    toast.setAttribute('role', kind === 'error' ? 'alert' : 'status');
    if (title) {
      const heading = this.document.createElement('strong');
      heading.className = options.titleClass ?? 'app-toast-title';
      heading.textContent = title;
      toast.appendChild(heading);
    }
    const text = this.document.createElement('span');
    text.textContent = message;
    toast.appendChild(text);
    if (options.closeButton !== false) {
      const close = this.document.createElement('button');
      close.type = 'button'; close.textContent = '×'; close.setAttribute('aria-label', 'Close notification');
      close.addEventListener('click', () => this.dismiss(toast));
      toast.appendChild(close);
    }
    container.appendChild(toast); this.active.add(toast);
    const timeout = options.timeOut ?? 5000;
    if (timeout > 0) globalThis.setTimeout(() => this.dismiss(toast), timeout);
  }

  private dismiss(toast: HTMLElement): void {
    toast.remove(); this.active.delete(toast);
  }
}
