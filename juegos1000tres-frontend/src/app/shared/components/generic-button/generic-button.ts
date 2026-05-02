import { NgClass, NgIf, NgTemplateOutlet } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'generic-button',
  imports: [NgClass, NgIf, NgTemplateOutlet, RouterLink],
  templateUrl: './generic-button.html',
  styleUrl: './generic-button.css',
})
export class GenericButton {
  private resolvedColor: 'red' | 'green' | 'gray' | 'purple' = 'gray';

  @Input() buttonType: 'button' | 'submit' | 'reset' = 'button';

  @Input() link: string | null = null;

  @Input()
  set color(value: string | undefined) {
    const normalized = (value || '').toLowerCase();

    if (
      normalized === 'red' ||
      normalized === 'green' ||
      normalized === 'gray' ||
      normalized === 'purple'
    ) {
      this.resolvedColor = normalized;
      return;
    }

    this.resolvedColor = 'gray';
  }

  get colorClass(): 'red' | 'green' | 'gray' | 'purple' {
    return this.resolvedColor;
  }
}
