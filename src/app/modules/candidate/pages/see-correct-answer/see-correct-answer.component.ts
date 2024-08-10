import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { QuestionResult } from '../../../../models/quiz/SubmitAnswerResponse';

@Component({
  selector: 'app-see-correct-answer',
  templateUrl: './see-correct-answer.component.html',
  styleUrls: ['./see-correct-answer.component.css']
})
export class SeeCorrectAnswerComponent implements OnInit {
  questionResults: QuestionResult[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit(): void {
    const state = this.router.getCurrentNavigation()?.extras.state as { questionResults: QuestionResult[] };
    if (state && state.questionResults) {
      this.questionResults = state.questionResults;
    } else {
      console.error('No question results found in state.');
    }
  }

  goBack(): void {
    this.location.back();
  }
}
