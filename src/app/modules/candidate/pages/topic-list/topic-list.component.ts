import { Component, ElementRef, OnInit } from '@angular/core';
import { TopicService } from "../../../../services/topic/topic.service";
import { TopicPageResponse } from "../../../../models/topic/topic-page-response";
import { ToastrService } from "ngx-toastr";
import { TopicResponse } from "../../../../models/topic/topic-response";
import { Observable } from "rxjs";

@Component({
  selector: 'app-topic-list',
  templateUrl: './topic-list.component.html',
  styleUrls: ['./topic-list.component.css']
})
export class TopicListComponent implements OnInit {
  topTopics: TopicPageResponse = {};
  currentPage = 0;
  totalPages = 0;
  scrolledRight = false; // Variable to indicate if user has scrolled to the right
  scrolledLeft = false;
  searchKeyword: string = '';
  searchedTopics: TopicResponse[] | undefined = []; // Initialize as an empty array

  constructor(
    private topicService: TopicService,
    private toastr: ToastrService,
    private elRef: ElementRef
  ) { }

  ngOnInit(): void {
    this.loadTopics();
  }

  loadTopics(): void {
    this.topicService.getLatestTopicsForCandidates(this.currentPage, 3).subscribe({
      next: (response: TopicPageResponse) => {
        this.topTopics = response;
        this.totalPages = response.totalPages || 0;
        // Update scrolledRight value based on current page and total pages
        this.scrolledRight = this.currentPage < this.totalPages - 1;
      },
      error: (error) => {
        console.error('Error loading topics:', error);
        this.showErrorMessage('Failed to load topics.');
      }
    });
  }

  scrollRight(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTopics();
      const container = this.elRef.nativeElement.querySelector('.topic-container');
      if (container) {
        container.scrollLeft += 300; // Adjust scroll amount as needed
      }
      this.scrolledLeft = true; // Set scrolledLeft to true when scrolling to the right
    }
  }

  scrollLeft(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTopics();
      const container = this.elRef.nativeElement.querySelector('.topic-container');
      if (container) {
        container.scrollLeft -= 300; // Adjust scroll amount as needed
      }

      // Toggle the visibility of the left scroll button
      if (this.currentPage === 0) {
        this.scrolledLeft = false; // Hide the left scroll button if on the first page
      } else {
        this.scrolledLeft = true; // Show the left scroll button if not on the first page
      }
    }
  }

  searchTopics(): void {
    if (this.searchKeyword.trim()) {
      this.topicService.findTopicsByTitleContainingPaginated(this.searchKeyword, 0, 3).subscribe({
        next: (response) => {
          this.searchedTopics = response.content || [];
          this.totalPages = response.totalPages || 0;
          // Reset current page to 0 when performing a new search
          this.currentPage = 0;
          // Update scrolledLeft and scrolledRight based on current page and total pages
          this.scrolledLeft = false;
          this.scrolledRight = this.currentPage < this.totalPages - 1;
        },
        error: (error) => {
          console.error('Error searching topics:', error);
          this.showErrorMessage('Failed to search topics.');
        }
      });
    } else {
      // If search keyword is empty, load all topics
      this.loadTopics();
    }
  }


  resetSearch(): void {
    this.searchKeyword = ''; // Clear the search keyword
    this.searchedTopics = []; // Clear the searched topics array
    this.loadTopics(); // Reload all topics
  }

  private showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }
}
