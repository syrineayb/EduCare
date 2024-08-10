import { Component, OnInit } from '@angular/core';
import { TopicResponse } from '../../../../models/topic/topic-response';
import { TopicService } from '../../../../services/topic/topic.service';
import { ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TopicRequest } from "../../../../models/topic/topic-request";
import { TopicPageResponse } from "../../../../models/topic/topic-page-response";

@Component({
  selector: 'app-topic-management',
  templateUrl: './topic-management.component.html',
  styleUrls: ['./topic-management.component.css']
})
export class TopicManagementComponent implements OnInit {
  topics: TopicResponse[] = [];
  filteredTopics: TopicResponse[] = [];
  selectedFile: File | null = null;
  newTopicForm!: FormGroup;
  selectedPageSize: number = 4; // Default page size

  currentPage = 0;
  totalPages = 0;
  pageSize = 4;
  pages: number[] = [];
  searchTerm = '';
  newTopic: TopicRequest = {
    title: '',
    imageFile: null
  };
  topicsPerPage: number = 5; // Default topics per page

  constructor(
    private topicService: TopicService,
    private toastr: ToastrService,
    private formBuilder: FormBuilder // Inject FormBuilder
  ) {}

  ngOnInit(): void {
    this.newTopicForm = this.formBuilder.group({ // Initialize newTopicForm
      title: ['', Validators.required] // Add validation for title
    });
    this.loadTopics();
  }

  get f() {
    return this.newTopicForm.controls;
  }

  loadTopics(): void {
    this.topicService.getTopicsInBatch(this.currentPage, this.pageSize).subscribe({
      next: (response: TopicPageResponse) => {
          this.topics = response.content || [];
          this.totalPages = response.totalPages || 0;
          this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1); // Update pages array
          this.filteredTopics = this.topics; // Set filteredUsers to all users initially
      },
      error: (error) => {
        console.error('Error loading topics:', error);
        this.showErrorMessage('Failed to fetch topics. Please try again later.');
      }
    });
  }

  applyFilters(): void {
    if (this.searchTerm.trim() === '') {
      // No search term, show all topics
      this.loadTopics();
    } else {
      // Apply search term filter
      this.topicService.findTopicsByTitle(this.searchTerm).subscribe(
        (response: TopicResponse[]) => {
          this.filteredTopics = response;
        },
        (error) => {
          console.error('Error filtering topics by title:', error);
          this.showErrorMessage('Failed to filter topics by title. Please try again later.');
        }
      );
    }
  }

  onEditFileSelected(event: any): void {
    const file: File = event.target.files[0];
    this.selectedFile = file;
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  createTopic(): void {
    if (this.newTopic.title) {
      const formData = new FormData();
      formData.append('title', this.newTopic.title);

      if (this.selectedFile) {
        formData.append('imageFile', this.selectedFile);
      }

      this.topicService.createTopic(formData).subscribe({
        next: (topicResponse: any) => {
          console.log('Topic created successfully:', topicResponse);
          this.topics.unshift(topicResponse); // Add to the beginning of the array
          this.resetForm(); // Reset form fields
          this.loadTopics(); // Reload topics
          this.showSuccessMessage('Topic created successfully.');
        },
        error: (error: any) => {
          console.error('Error creating topic:', error);
          this.showErrorMessage('Failed to create topic. Please try again later.');
        }
      });
    } else {
      console.error('Title is missing.');
      this.showErrorMessage('Title is missing.');
    }
  }

  updateTopic(topic: TopicResponse): void {
    // Check if either title or image file is provided
    if (!topic.title && !this.selectedFile) {
      console.error('Title or image file is missing.');
      this.showErrorMessage('Title or image file is missing.');
      return;
    }

    const formData = new FormData();

    // Append title if provided
    if (topic.title) {
      formData.append('title', topic.title);
    }

    // Append image file if provided
    if (this.selectedFile) {
      formData.append('imageFile', this.selectedFile);
    }

    this.topicService.updateTopic(topic.id, formData).subscribe({
      next: (res) => {
        this.showSuccessMessage('Topic updated successfully.');
        console.log('Topic updated successfully', res);
        this.loadTopics();
      },
      error: (error: any) => {
        console.error('Error updating topic:', error);
        this.showErrorMessage('Failed to update topic. Please try again later.');
      }
    });
  }

  deleteTopic(topicId: number): void {
    this.topicService.deleteTopic(topicId).subscribe(
      () => {
        console.log('Topic deleted successfully');
        this.showSuccessMessage('Topic deleted successfully.');
        this.loadTopics();
      },
      (error: any) => {
        console.error('Error deleting topic:', error);
        this.showErrorMessage('Failed to delete topic. Please try again later.');
      }
    );
  }

  private showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  private showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }

  resetForm(): void {
    this.newTopic.title = '';
    this.selectedFile = null;
    const fileInput = document.getElementById('imageFile') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = '';
    }
  }

  changePage(page: number, event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    this.currentPage = page;
    this.loadTopics();
  }

  nextPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTopics();
    }
  }

  prevPage(event: Event): void {
    event.preventDefault(); // Prevent default anchor tag behavior
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTopics();
    }
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.applyFilters();
  }


}
