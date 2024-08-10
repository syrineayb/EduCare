import { Component, OnInit, ElementRef } from '@angular/core';
import { TopicResponse } from "../../models/topic/topic-response";
import { TopicService } from "../../services/topic/topic.service";
import { TopicPageResponse } from "../../models/topic/topic-page-response";
import {CourseService} from "../../services/course/course.service";
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/auth/authentication.service";

@Component({
  selector: 'app-categories',
  templateUrl: './topic-card.component.html',
  styleUrls: ['./topic-card.component.css']
})
export class TopicCardComponent implements OnInit {
  topics: TopicPageResponse = {};
  filteredTopics: TopicResponse[] = [];
  selectedFile: File | null = null;
  isAuthenticated: boolean = false; // Flag to track user's authentication status

  currentPage = 0;
  totalPages = 0;
  pageSize = 4;
  pages: number[] = [];
  searchTerm = '';


  constructor(    private router: Router,
                  private topicService: TopicService,
                  private elRef: ElementRef,
                  private courseService: CourseService,
                  private authService: AuthenticationService,

  ) { }

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();

    this.loadTopics();
  }

  loadTopics(): void {
    this.topicService.getLatestTopicsForCandidates(this.currentPage, 3).subscribe({
      next: (response: TopicPageResponse) => {
        this.topics = response;
        this.totalPages = response.totalPages || 0;
        // Handle pagination buttons visibility based on current page and total pages
      },
      error: (error) => {
        console.error('Error loading topics:', error);
      }
    });
  }

  scrollToRight(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTopics();
      const container = this.elRef.nativeElement.querySelector('.container');
      if (container) {
        container.scrollLeft += 300; // Adjust the scroll amount as needed
      }
    }
  }

  scrollToLeft(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTopics();
      const container = this.elRef.nativeElement.querySelector('.container');
      if (container) {
        container.scrollLeft -= 300;
      }
    }
  }
  onCardClick(topicId: number): void {
    console.log(this.isAuthenticated);
    this.router.navigate(['/courses', topicId]);
  }
}
