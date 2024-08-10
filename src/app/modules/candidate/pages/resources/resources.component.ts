import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { ResourceResponse } from '../../../../models/resource/ResourceResponse';
import { ResourceService } from '../../../../services/resource/resource.service';
import { VgApiService } from '@videogular/ngx-videogular/core';
import { ToastrService } from 'ngx-toastr';
import { PageResponse } from '../../../../models/resource/PageResponse';
import { ForumService } from "../../../../services/forum/forum.service";
import {AnswerResponse} from "../../../../models/answer/answer-response";

import {MessageResponse} from "../../../../models/message/message-response";
import {AnswerRequest} from "../../../../models/answer/answer-request";
import {DomSanitizer} from "@angular/platform-browser";
import * as RecordRTC from "recordrtc";
import {AuthenticationService} from "../../../../services/auth/authentication.service";
import {UserResponse} from "../../../../models/account/user-response";
import {LessonService} from "../../../../services/lesson/lesson.service";
import {MessagePageResponse} from "../../../../models/message/message-page-response";

@Component({
  selector: 'app-resources',
  templateUrl: './resources.component.html',
  styleUrls: ['./resources.component.css']
})
export class ResourcesComponent implements OnInit {
  @ViewChild('audioElement') audioElement!: ElementRef<HTMLAudioElement>;
  ngAfterViewInit() {
    // Now audioElement is initialized and can be safely accessed
    console.log(this.audioElement.nativeElement); // Example usage
  }
  lessonId: number = 0;
  courseId: number = 0; // Add courseId variable
  resources: any[] = [];

  //resources: ResourceResponse[] | undefined = [];
  api: VgApiService = new VgApiService();
  selectedResource: ResourceResponse | null = null;
  pdfSrc: string | undefined; // Adjust the type to string
  currentPage = 0;
  pageSize = 1; // Adjust the page size as needed
  totalPages = 0;
  totalElements = 0;
  progressValues = new Map<number, number>();
  forum: { id: number; title: string } | undefined;
  messages: MessageResponse[] = [];
  discussionPage: MessagePageResponse | undefined;
  discussionCurrentPage = 0;
  discussionPageSize = 1;
  discussionTotalPages = 0;
  discussionTotalElements = 0;
  answers: AnswerResponse[] = [];
  showAnswers: boolean = false;
  showAddDiscussionForm: boolean = false;
  isSubjectDisabled: boolean = true;
  newDiscussion: {
    subject: string;
    messageText: string;
    recordFile?: File;
    imageFile?: File;
    videoFile?: File;
    pdfFile?: File;
  } = {
    subject: '',
    messageText: '',
    recordFile: undefined,
    imageFile: undefined,
    videoFile: undefined,
    pdfFile: undefined
  };
  isResourceComplete: boolean = false;
  buttonText: string = 'Mark as Complete';
  selectedMessageTypes: string[] = [];
  showPdfInput: boolean = false;
  showImageInput: boolean = false;
  showVideoInput: boolean = false;
  showRecordInput: boolean = false;
  showTextInput: boolean = false;
  discussionAnswersState: Map<number, boolean> = new Map<number, boolean>();
  answersCounts: { [key: number]: number } = {};
  newAnswer: AnswerRequest = {messageContent: ''};
  showAddAnswerInput: boolean = false;
  selectedAnswerId: number | null = null;
  selectedDiscussionId: number | null = null;
  showUpdateAnswerInput: boolean = false;
  updatedAnswer: AnswerResponse = {
    id: 0, // Initial value for id
    discussionId: 0, // Initial value for discussionId
    messageContent: '', // Initial value for messageContent
    messageText: '', // Initial value for discussionMessage
    authorId: 0, // Initial value for authorId
    authorFullName: '', // Initial value for authorFullName
    createdAt: '', // Initial value for createdAt
    updatedAt: '' // Initial value for updatedAt
  };
  showVideo: boolean = false;
  showImage: boolean = false;
  showText: boolean = false;
  showPdf: boolean = false;
  showAudio: boolean = false;
  recorder: any;
  recording: boolean = false;
  url: any;
  error: any;
  currentUser: UserResponse | null = null; // Initialize as null or undefined, depending on your preference
  allResourcesComplete: boolean = false;
  completionMessageVisible: boolean = false;
  disableNextButton: boolean = false;
  totalLessonsCount: number =0 ;
  searchKeyword: string = '';
  forumId: number = 0;  // Initialize with a default value

  constructor(
    private route: ActivatedRoute,
    private toastr: ToastrService,
    private resourceService: ResourceService,
    private forumService: ForumService,
    private lessonService: LessonService,
    private authService: AuthenticationService,
    private domSanitizer: DomSanitizer,
    private router: Router
  ) {
  }
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      console.log('Route parameters:', params); // Add this line
      this.lessonId = +params['LessonId'];
      this.courseId = +params['courseId'];
      console.log("course id : "+this.courseId);
      this.loadResources(this.currentPage, this.pageSize);
      this.fetchProgress(this.lessonId);
    });

    this.fetchForumTitle(this.lessonId);
    this.authService.getCurrentUser().subscribe(
      currentUser => {
        this.currentUser = currentUser;
        console.log(this.currentUser?.id);
      },
      error => {
        console.error('Error fetching current user:', error);
      }
    );
    this.lessonService.countLessonsByCourseId(this.courseId).subscribe(count => {
      this.totalLessonsCount = count;
    });
  }
  startRecording() {
    this.recording = true;
    let mediaConstraints = {
      video: false,
      audio: true,
    };
    navigator.mediaDevices.getUserMedia(mediaConstraints).then(
      (stream: MediaStream) => this.successCallback(stream),
      (error: any) => this.errorCallback(error)
    );
  }
  isNextLessonDisabled(): boolean {
    const nextLessonId = this.lessonId + 1;
    return nextLessonId > this.totalLessonsCount;
  }
  hasLessons(): boolean {
    // Check if resources is defined and not null, and if it has any elements
    return this.resources !== undefined && this.resources !== null && this.resources.length > 0;
  }


  navigateToNextLessonResources(): void {
    const nextLessonId = this.lessonId + 1;

    // Check if the next lesson id is within the total lessons count
    if (nextLessonId <= this.totalLessonsCount) {
      const nextLessonResourcesUrl = `/candidate/my-course/lessons/${this.courseId}/resources/${nextLessonId}`;
      this.router.navigate([nextLessonResourcesUrl]);
    } else {
      // If next lesson id exceeds total lessons count, do nothing (or display a message)
      console.log('No more lessons available');
    }
  }
  successCallback(stream: MediaStream) {
    var options: RecordRTC.Options = {
      mimeType: 'audio/wav' // or 'audio/webm'
    };
    this.recorder = new RecordRTC.StereoAudioRecorder(stream, options);
    this.recorder.record();
  }

  stopRecording() {
    this.recording = false;
    this.recorder.stop(this.processRecording.bind(this));
  }

  sanitize(url: string) {
    console.log('Sanitizing URL:', url);
    return this.domSanitizer.bypassSecurityTrustUrl(url);
  }


  processRecording(blob: Blob) {
    this.url = URL.createObjectURL(blob);
    console.log('Recorded audio URL:', this.url);
    this.newDiscussion.recordFile = new File([blob], 'recorded-audio.wav', { type: 'audio/wav' });
  }


  errorCallback(error: any) {
    this.error = 'Can not access your microphone.';
    console.error('Error accessing media devices:', error);
  }


  fetchForumTitle(lessonId: number): void {
    this.forumService.getForumByLessonId(lessonId).subscribe(
      (forumResponse: any) => {
        this.forum = forumResponse;
        if (this.forum && this.forum.id) {
          this.fetchDiscussions(this.forum.id, 0); // Removed discussionPageSize parameter
        }
      },
      (error: any) => {
        console.error('Error fetching forum title:', error);
      }
    );
  }


  fetchDiscussions(forumId: number, page: number): void {
    this.forumService.getAllDiscussionsByForumId(forumId, page, this.discussionPageSize).subscribe(
      (response: MessagePageResponse) => {
        if (response && response.content) {
          this.discussionPage = response;
          this.messages = response.content.map(discussion => ({
            ...discussion,
          }));
          this.discussionTotalPages = response.totalPages || 0;
          this.discussionTotalElements = response.totalElements || 0;

          // Call getAnswersCountByDiscussion for each discussion
          this.messages.forEach(discussion => {
            this.getAnswersCountByDiscussion(discussion.id);
          });
        } else {
          this.messages = [];
          this.discussionTotalPages = 0;
          this.discussionTotalElements = 0;
        }
      },
      (error: any) => {
        this.showErrorMessage('Failed to fetch discussions');
        console.error('Error fetching discussions:', error);
      }
    );
  }

  searchDiscussionsBySubject(page: number): void {
    if (this.searchKeyword.trim() !== '') {
      const size = this.discussionPageSize;
      this.forumService.searchDiscussionsBySubject(this.searchKeyword, size, page, this.lessonId)
        .subscribe(
          (response: MessagePageResponse) => {
            if (response && response.content) {
              this.discussionPage = response;
              this.messages = response.content.map(discussion => ({
                ...discussion,
              }));
              this.discussionTotalPages = response.totalPages || 0;
              this.discussionTotalElements = response.totalElements || 0;

              this.messages.forEach(discussion => {
                this.getAnswersCountByDiscussion(discussion.id);
              });
            } else {
              this.messages = [];
              this.discussionTotalPages = 0;
              this.discussionTotalElements = 0;
            }
          },
          (error: any) => {
            let errorMessage = 'An unexpected error occurred. Please try again.';
            switch (error.status) {
              case 403:
                errorMessage = 'Access forbidden. You do not have permission to access this resource.';
                break;
              case 404:
                errorMessage = `No discussions found with the subject "${this.searchKeyword}"`;
                break;
            }
            this.showErrorMessage(errorMessage);
            console.error('Error searching discussions:', error);
          }
        );
    }
  }


  resetSearch(): void {
    this.searchKeyword = ''; // Clear the search keyword
    if (this.forum?.id) {
      this.fetchDiscussions(this.forum.id, 0);
    }
  }
  toggleAnswers(discussion: MessageResponse): void {
    discussion.showAnswers = !discussion.showAnswers;

    // Fetch answers if they haven't been fetched yet
    if (discussion.showAnswers && discussion.answers?.length === 0) {
      this.forumService.getAnswersByDiscussion(discussion.id).subscribe(
        (answers: AnswerResponse[]) => {
          discussion.answers = answers;
        },
        (error: any) => {
          this.showErrorMessage('Failed to fetch answers');
          console.error('Error fetching answers:', error);
        }
      );
    }
  }


  submitDiscussion(): void {
    // Check if subject or message text is empty
    if (!this.newDiscussion.subject.trim()) {
      this.showErrorMessage('Subject is required.');
      return;
    }
    if (!this.newDiscussion.messageText.trim()) {
      this.showErrorMessage('Message is required.');
      return;
    }

    // Create a FormData object to handle file uploads
    const formData = new FormData();
    formData.append('subject', this.newDiscussion.subject.trim());
    formData.append('messageText', this.newDiscussion.messageText.trim());
    if (this.newDiscussion.recordFile) {
      formData.append('recordFile', this.newDiscussion.recordFile, this.newDiscussion.recordFile.name);
    }
    if (this.newDiscussion.imageFile) {
      formData.append('imageFile', this.newDiscussion.imageFile, this.newDiscussion.imageFile.name);
    }
    if (this.newDiscussion.videoFile) {
      formData.append('videoFile', this.newDiscussion.videoFile, this.newDiscussion.videoFile.name);
    }
    if (this.newDiscussion.pdfFile) {
      formData.append('pdfFile', this.newDiscussion.pdfFile, this.newDiscussion.pdfFile.name);
    }

    // Check if forum ID is available
    if (this.forum?.id) {
      // Call the forum service method to create message with files
      this.forumService.createMessage(formData, this.forum.id).subscribe(
        (messageResponse: MessageResponse) => {
          this.showSuccessMessage('Discussion created successfully');
          this.newDiscussion = {
            subject: '',
            messageText: '',
            recordFile: undefined,
            imageFile: undefined,
            videoFile: undefined,
            pdfFile: undefined
          }; // Reset newDiscussion object
          this.isSubjectDisabled = true;
          this.showAddDiscussionForm = false;
          // Reload discussions after creating a new one
          if (this.forum) {
            this.fetchDiscussions(this.forum.id, 0);
          }
        },
        (error: any) => {
          console.error('Error creating message:', error);
          this.showErrorMessage('Failed to create message');
        }
      );
    }
  }
  // Method to submit an answer
  // Method to submit discussion with message and files
  toggleMessageType(type: string): void {
    const index = this.selectedMessageTypes.indexOf(type);
    if (index === -1) {
      this.selectedMessageTypes.push(type);
      if (type === 'audio') {
        this.showRecordInput = true;
      }
    } else {
      this.selectedMessageTypes.splice(index, 1);
      if (type === 'audio') {
        this.showRecordInput = false;
      }
    }

    // Update input visibility based on selected message types
    this.showTextInput = this.selectedMessageTypes.includes('text');
    this.showPdfInput = this.selectedMessageTypes.includes('pdf');
    this.showImageInput = this.selectedMessageTypes.includes('image');
    this.showVideoInput = this.selectedMessageTypes.includes('video');
    this.showRecordInput = this.selectedMessageTypes.includes('audio');

    // Update other inputs similarly for video, record, etc.
  }


  onFileSelected(event: any, fileType: string): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      switch (fileType) {
        case 'pdfFile':
          this.newDiscussion.pdfFile = file;
          break;
        case 'imageFile':
          this.newDiscussion.imageFile = file;
          break;
        case 'videoFile':
          this.newDiscussion.videoFile = file;
          break;
        case 'recordFile':
          this.newDiscussion.recordFile = file;
          break;
        // Add cases for other file types
        default:
          break;
      }
    }
  }



  toggleAddDiscussionForm(): void {
    this.showAddDiscussionForm = !this.showAddDiscussionForm;
    if (this.showAddDiscussionForm) {
      this.isSubjectDisabled = false;
    }
  }

  getAnswersCountByDiscussion(discussionId: number): void {
    this.forumService.getAnswersCountByDiscussion(discussionId).subscribe(count => {
      // Store answers count in the object
      this.answersCounts[discussionId] = count;
    });
  }

  // Method to get answers count for a discussion
  getAnswersCount(discussionId: number): number {
    return this.answersCounts[discussionId] || 0;
  }

  toggleAddAnswerInput(discussionId: number): void {
    this.showAddAnswerInput = !this.showAddAnswerInput;
    this.selectedDiscussionId = discussionId;
  }
  toggleReplyInput(answer: AnswerResponse): void {
    answer.showReplyInput = !answer.showReplyInput;
  }
  submitAnswer(): void {
    if (this.selectedDiscussionId !== null) {
      this.forumService.createAnswer(this.selectedDiscussionId, this.newAnswer).subscribe(
        (response: AnswerResponse) => {
          // Handle the response, e.g., add the new answer to the discussion
          this.showSuccessMessage('Answer created successfully');

          // Fetch answers for the discussion to update the UI
          if (this.selectedDiscussionId !== null) {
            this.fetchAnswersForDiscussion(this.selectedDiscussionId);
          }

          this.resetAnswerForm();
        },
        (error) => {
          this.showErrorMessage('Error creating answer');
        }
      );
    }
  }

  fetchAnswersForDiscussion(discussionId: number): void {
    this.forumService.getAnswersByDiscussion(discussionId).subscribe(
      (answers: AnswerResponse[]) => {
        // Find the discussion and update its answers
        const discussion = this.messages.find(discussion => discussion.id === discussionId);
        if (discussion) {
          discussion.answers = answers;
        }
      },
      (error: any) => {
        this.showErrorMessage('Error fetching answers');
        console.error('Error fetching answers:', error);
      }
    );
  }

  resetAnswerForm(): void {
    this.newAnswer = {messageContent: ''};
    this.showAddAnswerInput = false;
    this.selectedDiscussionId = null;
  }

  cancelDiscussion(): void {
    // Reset the form
    //  this.newDiscussion = { subject: '', message: { text: '' } };
    this.showAddDiscussionForm = false;
    this.isSubjectDisabled = true;

  }



  fetchProgress(lessonId: number): void {
    this.resourceService.getResourceProgress(lessonId).subscribe({
      next: response => {
        this.progressValues.set(lessonId, response.progress);
        this.checkAllResourcesComplete();

      },
      error: error => {
        this.showErrorMessage('Failed to fetch resource progress')
        console.error('Error fetching progress:', error);
      }
    });
  }

  loadResources(page: number, size: number): void {
    this.resourceService.getResourcesByLessonId(this.lessonId, page, size).subscribe(
      (data: PageResponse) => {
        if (data && data.content) {
          this.resources = data.content;
          this.totalPages = data.totalPages || 0;
          this.totalElements = data.totalElements || 0;

          this.resources.forEach(resource => {
            this.fetchResourceProgress(resource.id);
          });
        } else {
          this.resources = [];
          this.totalPages = 0;
          this.totalElements = 0;
        }
      },
      error => {
        this.showErrorMessage('Failed to fetch resources');
        console.error('Error fetching resources:', error);
      }
    );
  }

  fetchResourceProgress(resourceId: number): void {
    this.resourceService.getResourceProgress(resourceId).subscribe({
      next: response => {
        this.progressValues.set(resourceId, response.progress);
      },
      error: error => {
        this.showErrorMessage('Failed to fetch resource');
        console.error('Error fetching progress for resource:', error);
      }
    });
  }

  openPdf(resource: any): void {
    this.selectedResource = resource;
    this.pdfSrc = resource.pdfFile;
  }

  closePdf(): void {
    this.selectedResource = null;
  }

  onPlayerReady(source: VgApiService): void {
    this.api = source;
    this.api.getDefaultMedia().subscriptions.loadedMetadata.subscribe(
      this.autoplay.bind(this)
    );
  }

  autoplay(): void {
    this.api.play();
  }

  checkAllResourcesComplete(): void {
    // Check if any resource has progress greater than 0
    this.allResourcesComplete = Array.from(this.progressValues.values()).some(progress => progress > 0);
  }
  markResourceComplete(resourceId: number): void {
    this.resourceService.markResourceAsCompleted(resourceId).subscribe(
      response => {
        // Resource marked as complete successfully
        this.isResourceComplete = true;
        this.fetchProgress(this.lessonId);
      },
      error => {
        // Error occurred while marking resource as complete
        this.showErrorMessage('Failed to mark resource as complete');
        console.error('Error marking resource as complete:', error);
      }
    );
  }

  loadNextResource(): void {
    const currentIndex = this.resources.findIndex(r => r.id === this.selectedResource?.id);
    const nextIndex = currentIndex + 1;

    if (nextIndex < this.resources.length) {
      // If there are more resources, load the next resource
      this.selectedResource = this.resources[nextIndex];
      this.isResourceComplete = false;
      this.currentPage++;
      this.loadResources(this.currentPage, this.pageSize);
    } else {
      // Check if all resources are completed
      const allResourcesCompleted = this.resources.every(resource => resource.completed);
      if (allResourcesCompleted) {
        // If all resources are completed, redirect to the lessons page
        this.router.navigate(['/candidate/my-course/lessons', this.courseId]);
      } else {
        // If there are still incomplete resources, display a message or handle as needed
        console.log('Some resources are still incomplete.');
      }
    }
  }


  showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success'
    });
  }

  showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error'
    });
  }

  changePage(page: number, event: Event): void {
    event.preventDefault();
    this.currentPage = page;
    this.loadResources(page, this.pageSize);
  }

  nextPage(event: Event): void {
    event.preventDefault();
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadResources(this.currentPage, this.pageSize);
    }
  }

  prevPage(event: Event): void {
    event.preventDefault();
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadResources(this.currentPage, this.pageSize);
    }
  }

  changeDiscussionPage(page: number, event: Event): void {
    event.preventDefault();
    this.discussionCurrentPage = page;
    if (this.forum?.id) {
      this.fetchDiscussions(this.forum.id, page);
    }
  }

  nextDiscussionPage(event: Event): void {
    event.preventDefault();
    if (this.discussionCurrentPage < this.discussionTotalPages - 1) {
      this.discussionCurrentPage++;
      if (this.forum?.id) {
        this.fetchDiscussions(this.forum.id, this.discussionCurrentPage);
      }
    }
  }

  prevDiscussionPage(event: Event): void {
    event.preventDefault();
    if (this.discussionCurrentPage > 0) {
      this.discussionCurrentPage--;
      if (this.forum?.id) {
        this.fetchDiscussions(this.forum.id, this.discussionCurrentPage);
      }
    }
  }

  generatePageArray(totalPages: number): number[] {
    return Array.from({length: totalPages}, (_, i) => i);
  }


  toggleContent(contentType: string): void {
    switch (contentType) {
      case 'pdf':
        this.showPdf = !this.showPdf;
        break;
      case 'audio':
        this.showAudio = !this.showAudio;
        break;
      case 'video':
        this.showVideo = !this.showVideo;
        break;
      case 'image':
        this.showImage = !this.showImage;
        break;
      default:
        break;
    }
  }

  /*  editDiscussion(discussionId: number, newSubject: string): void {
        // Create a new DiscussionRequest object with the updated subject
        const updatedDiscussion: DiscussionRequest = {
            subject: newSubject,
            message: { text: '' } // Assuming you want to keep the message empty for now
        };

        // Update the discussion with the new subject
        this.forumService.updateDiscussion(discussionId, updatedDiscussion).subscribe(
            response => {
                this.showSuccessMessage("Discussion updated successfully");
                console.log('Discussion updated successfully:', response);
                // Refresh discussions after update
          //      this.fetchDiscussions(this.forum.id, this.discussionCurrentPage);
            },
            error => {
              this.showErrorMessage("Error updating discussion");
                console.error('Error updating discussion:', error);
            }
        );
    }

   */



  /*
  updateDiscussion(discussion: MessageResponse): void {
      // Prepare FormData
      const formData = new FormData();
      formData.append('subject', discussion.subject);
      formData.append('messageText', discussion.messageText);

      // Example: Append file URLs if available in the response (adjust according to your API response)
      if (discussion.recordUrl) {
          // Simulate file upload by sending the file name as a string
          formData.append('recordFile', discussion.recordUrl);
      }
      if (discussion.imageUrl) {
          // Simulate file upload by sending the file name as a string
          formData.append('imageFile', discussion.imageUrl);
      }
      if (discussion.videoUrl) {
          // Simulate file upload by sending the file name as a string
          formData.append('videoFile', discussion.videoUrl);
      }
      if (discussion.pdfUrl) {
          // Simulate file upload by sending the file name as a string
          formData.append('pdfFile', discussion.pdfUrl);
      }

      // Call updateMessage method from forumService
      this.forumService.updateMessage(discussion.id, formData).subscribe(
          (response: MessageResponse) => {
              // Handle success
              this.showSuccessMessage('Discussion updated successfully');
              discussion.subject = response.subject; // Update local subject from response if necessary
              discussion.messageText = response.messageText; // Update local messageText from response if necessary
              discussion.updatedAt = response.updatedAt; // Update updatedAt from response if necessary
              discussion.editMode = false; // Exit edit mode
          },
          (error: any) => {
              // Handle error
              console.error('Error updating discussion:', error);
              this.showErrorMessage('Failed to update discussion');
          }
      );
  }

   */
  updateDiscussion(discussion: MessageResponse): void {
    const formData = new FormData();
    formData.append('subject', discussion.subject);
    formData.append('messageText', discussion.messageText);

    if (this.newDiscussion.recordFile) {
      formData.append('recordFile', this.newDiscussion.recordFile);
    }
    if (this.newDiscussion.imageFile) {
      formData.append('imageFile', this.newDiscussion.imageFile);
    }
    if (this.newDiscussion.videoFile) {
      formData.append('videoFile', this.newDiscussion.videoFile);
    }
    if (this.newDiscussion.pdfFile) {
      formData.append('pdfFile', this.newDiscussion.pdfFile);
    }

    this.forumService.updateMessage(discussion.id, formData).subscribe(
      (response: MessageResponse) => {
        this.showSuccessMessage('Discussion updated successfully');
        discussion.subject = response.subject;
        discussion.messageText = response.messageText;
        discussion.updatedAt = response.updatedAt;
        discussion.editMode = false;
        discussion.recordUrl = response.recordUrl;
        discussion.imageUrl = response.imageUrl;
        discussion.videoUrl = response.videoUrl;
        discussion.pdfUrl = response.pdfUrl;
        this.newDiscussion = {
          subject: '',
          messageText: '',
          recordFile: undefined,
          imageFile: undefined,
          videoFile: undefined,
          pdfFile: undefined
        };                 },
      (error: any) => {
        console.error('Error updating discussion:', error);
        this.showErrorMessage('Failed to update discussion');
      }
    );
  }

  deleteDiscussion(discussionId: number): void {
    // Delete discussion logic
    this.forumService.deleteDiscussion(discussionId).subscribe(() => {
      this.showSuccessMessage("Discussion deleted successfully");
      console.log('Discussion deleted successfully');

      // Refresh discussions after deletion
      this.fetchDiscussions(this.forum?.id || 0, this.discussionCurrentPage);
    }, error => {
      this.showErrorMessage("Error deleting discussion:");
      console.error('Error deleting discussion:', error);
    });
  }

  // Method to delete an answer
  deleteAnswer(discussionId: number, answerId: number): void {
    // Call the deleteAnswer method from the ForumService
    this.forumService.deleteAnswer(answerId).subscribe(
      () => {
        // Handle success
        console.log('Answer deleted successfully.');

        // Fetch answers for the discussion after deleting an answer
        this.fetchAnswersForDiscussion(discussionId);
      },
      error => {
        // Handle error
        console.error('Error deleting answer:', error);
      }
    );
  }



  editAnswer(discussionId: number, answerId: number): void {
    this.selectedDiscussionId = discussionId;
    this.selectedAnswerId = answerId;
    const discussion = this.messages.find(discussion => discussion.id === discussionId);
    if (discussion) {
      const answerToUpdate = discussion.answers?.find(answer => answer.id === answerId);
      if (answerToUpdate) {
        this.updatedAnswer = { ...answerToUpdate };
        this.showUpdateAnswerInput = true;
      }
    }
  }


  /* autoSaveAnswer(answerId: number) {
       // Check if the textarea is actively being edited
       if (this.isEditingAnswer) {
           // Implement the logic to save the updated answer automatically
           this.submitUpdatedAnswer(answerId);
       }
   }
   */





  toggleEditAnswer(discussionId: number, answerId: number) {
    // Toggle the showUpdateAnswerInput variable
    this.showUpdateAnswerInput = !this.showUpdateAnswerInput;
    this.selectedDiscussionId = discussionId;
    this.selectedAnswerId = answerId;

    // Initialize updatedAnswer with the current answer's content
    const answer = this.getAnswerById(discussionId, answerId);
    if (answer) {
      this.updatedAnswer.messageContent = answer.messageContent;
    }
  }

  submitUpdatedAnswer(answerId: number) {
    // Find the discussion and answer by their IDs
    const discussion = this.messages.find(d => d.answers?.some(a => a.id === answerId));
    if (discussion) {
      const answer = discussion.answers?.find(a => a.id === answerId);
      if (answer) {
        // Update the answer's content with the new content from the textarea
        answer.messageContent = this.updatedAnswer.messageContent;

        // Call your API or service method to save the updated answer to the backend
        this.forumService.updateAnswer(answerId, this.updatedAnswer).subscribe(
          response => {
            console.log('Answer updated successfully:', response);
          },
          error => {
            console.error('Error updating answer:', error);
          }
        );
      }
    }
  }

  getAnswerById(discussionId: number, answerId: number) {
    const discussion = this.messages.find(d => d.id === discussionId);
    if (discussion && discussion.answers) {
      return discussion.answers.find(a => a.id === answerId);
    }
    return null;
  }

  toggleEditMode(discussion: MessageResponse): void {
    if (discussion.editMode) {
      discussion.editMode = false;
      // discussion.updatedSubject = undefined;
    } else {
      this.messages.forEach(d => d.editMode = false); // Close other edit modes
      discussion.editMode = true;
      //   discussion.updatedSubject = discussion.subject;
    }
  }


  getButtonLabel(): string {
    return this.allResourcesComplete ? 'Next' : 'Mark as Complete';
  }

  navigateToNewResource() {
    this.currentPage++;
  }

  hasNextLesson() {
    console.log(this.lessonId);
  }


  submitReply(answer: AnswerResponse) {
    console.log('submited');
  }
}
