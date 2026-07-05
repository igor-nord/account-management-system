import { Component, ElementRef, OnDestroy, effect, input, viewChild } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { BalanceSnapshot } from './overview.models';

Chart.register(...registerables);

@Component({
  selector: 'app-balance-chart',
  template: `<canvas #canvas></canvas>`,
})
export class BalanceChart implements OnDestroy {
  readonly series = input<BalanceSnapshot[]>([]);
  private readonly canvas = viewChild<ElementRef<HTMLCanvasElement>>('canvas');
  private chart?: Chart;

  constructor() {
    effect(() => {
      const points = this.series();
      const ref = this.canvas();
      if (!ref) {
        return;
      }
      this.chart?.destroy();
      this.chart = new Chart(ref.nativeElement, {
        type: 'line',
        data: {
          labels: points.map((p) => new Date(p.time).toLocaleString()),
          datasets: [{ label: 'Balance', data: points.map((p) => Number(p.balance)) }],
        },
      });
    });
  }

  ngOnDestroy(): void {
    this.chart?.destroy();
  }
}
