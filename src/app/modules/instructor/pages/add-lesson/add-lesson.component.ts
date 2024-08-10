import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {FormBuilder, FormGroup, NgForm, Validators} from '@angular/forms';
import { LessonRequest } from "../../../../models/lesson/lesson-request";
import { LessonPageResponse } from "../../../../models/lesson/lesson-page-response";
import { LessonService } from "../../../../services/lesson/lesson.service";
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { VgApiService } from "@videogular/ngx-videogular/core";
import {ToastrService} from "ngx-toastr";
import {ResourceRequest} from "../../../../models/resource/ResourceRequest";
import {ResourceResponse} from "../../../../models/resource/ResourceResponse";
import {ResourceService} from "../../../../services/resource/resource.service";

@Component({
    selector: 'app-add-lesson',
    templateUrl: './add-lesson.component.html',
    styleUrls: ['./add-lesson.component.css']
})
export class AddLessonComponent implements OnInit {
    courseId: number | undefined;
    lessonsPage: LessonPageResponse = { content: [] }; // Initialize lessonsPage with empty content
    lesson: LessonRequest = { title: '', description: '' };
  NewRessource: ResourceRequest = {};
  resources: ResourceResponse[] = [];
  selectedResource:ResourceResponse|null=null;
  selectedImg: File | undefined = undefined;
  selectedVedio: File | undefined = undefined;
  selectedPdf: File | undefined = undefined;

    currentPage = 0;
    totalPages = 0;
    pages: number[] = [];

  @ViewChild('videoFile') videoFileInput!: ElementRef;
  @ViewChild('pdfFile') pdfFileInput!: ElementRef;
  @ViewChild('imageFile') imageFileInput!: ElementRef;
  searchTerm: string = ''; // Define searchTerm property
    videoUrl: string | ArrayBuffer | null = null; // URL of the uploaded video
    api: VgApiService = new VgApiService;
    //preload: string = 'auto';
    errorMessage: string | undefined;
    lessonForm!: FormGroup;
    pdfSrc: string | undefined; // Adjust the type to string
  selectedLesson: any | null = null;
  selectedLessonId: number | null = null; // Define selectedLessonId property

  constructor(
        private lessonService: LessonService,
        private route: ActivatedRoute,
        private sanitizer: DomSanitizer,
        private formBuilder: FormBuilder,
        private toastr: ToastrService,
        private resourceService: ResourceService, // Inject ResourceService


    ) {
        // Initialize videoUrl property
        this.videoUrl = null;
    }

  ngOnInit(): void {
    this.lessonForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(200)]],
      description: ['', [Validators.required, Validators.minLength(2)]],
    });

    this.route.params.subscribe(params => {
      this.courseId = +params['courseId'];
      console.log('Course ID:', this.courseId);
      this.loadLessons();
    });
  }

  onEditImgSelected(event: any): void {
    const file: File = event.target.files[0];
    this.selectedImg = file;
  }
  onEditVedioSelected(event: any): void {
    const file: File = event.target.files[0];
    this.selectedVedio = file;
  }
  onEditPdfSelected(event: any): void {
    const file: File = event.target.files[0];
    this.selectedPdf = file;
  }
    sanitizeUrl(url: string): SafeResourceUrl {
        return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }


    onVideoFileChange(event: any) {
        const file = event.target.files[0];
        const reader = new FileReader();

        reader.onload = () => {
            // Save the video URL
            this.videoUrl = reader.result;
        };

        if (file) {
            // Read the video file as a data URL
            reader.readAsDataURL(file);
        }
    }

    loadVideoPreview(file: File): void {
        const reader = new FileReader();
        reader.onload = () => {
            // Save the video URL
            this.videoUrl = reader.result;
        };
        if (file) {
            // Read the video file as a data URL
            reader.readAsDataURL(file);
        }
    }




  loadLessons(page: number = 1, size: number = 2): void {
        if (!this.courseId) {
            this.errorMessage = "Course ID is not provided.";
            return;
        }
        this.lessonService.getAllLessonsByCourses(this.courseId, page, size)
            .subscribe(
                (response) => {
                    console.log("List of lessons for course ID:", this.courseId);
                    console.log(response); // Log the lessons
                    this.lessonsPage = response;
                    this.calculatePagination();
                },
                (error) => {
                    this.errorMessage = error.error.errorMsg || "An error occurred while fetching lessons.";
                }
            );
    }
    /* Add Lesson Method */

  addLesson(): void {
    if (!this.courseId) {
      console.error('Course ID is not provided.');
      return;
    }

    if (!this.lessonForm.get('title')!.value.trim() || !this.lessonForm.get('description')!.value.trim()) {
      this.showErrorMessage('Please fill in the title and description fields.');
      return;
    }

    if (this.lessonForm.valid) {
      const lessonRequest: LessonRequest = {
        title: this.lessonForm.get('title')!.value,
        description: this.lessonForm.get('description')!.value
      };

      this.lessonService.createLesson(this.courseId, lessonRequest)
        .subscribe(
          (lessonResponse) => {
            this.showSuccessMessage("Lesson created successfully");
            console.log('Lesson added successfully:', lessonResponse);
            this.loadLessons(); // Reload lessons after adding a lesson
          },
          (error) => {
            this.showErrorMessage('Failed to create lesson. Please try again later.');
          }
        );
    } else {
      this.showErrorMessage('Please fill in all required fields.');
    }
  }



    /* Update Lesson Method */

  updateLesson(lesson: any): void {
    if (!this.courseId) {
      console.error('Course ID is not provided.');
      return;
    }
    if (!lesson.title && !lesson.description && !this.selectedImg && !this.selectedVedio && !this.selectedPdf) {
      this.showErrorMessage('Title or image file is missing.');
      console.error('Title or image file is missing.');
      return;
    }

    // Create lessonRequest object
    let lessonRequest: LessonRequest = {
      title: lesson.title, // Assuming title and description are always present
      description: lesson.description,
      courseId: this.courseId
    };

    // Update the lesson through LessonService
    this.lessonService.updateLesson(lesson.id, lessonRequest).subscribe(
      (updatedLesson) => {
        console.log('Lesson updated successfully:', updatedLesson);
        this.showSuccessMessage('Lesson updated successfully.');
        // Reload lessons after updating a lesson
        this.loadLessons();
      },
      (error) => {
        console.error('Error updating lesson:', error);
        this.showErrorMessage('Failed to update lesson. Please try again later.');
      }
    );
  }

  deleteLesson(lessonId: number): void {
    this.lessonService.deleteLesson(lessonId).subscribe(
      () => {
        console.log('Lesson deleted successfully.');
        this.showSuccessMessage('Lesson deleted successfully.');
        this.loadLessons(); // Reload lessons after deleting a lesson
      },
      (error) => {
        console.error('Error deleting lesson:', error);
        let errorMessage = "An error occurred while deleting the lesson.";
        if (error.error && error.error.errorMsg) {
          errorMessage = error.error.errorMsg;
        }
        this.showErrorMessage(errorMessage);
      }
    );
  }

    resetForm(lessonForm: NgForm): void {
        lessonForm.resetForm();
    }
    resetResourceForm(): void {
        this.selectedImg = undefined;
        this.selectedVedio = undefined;
        this.selectedPdf = undefined;

    }

    onPageChange(page: number): void {
        this.loadLessons(page);
    }

  onFileSelected(event: any, fileType: string): void {
    const file = event.target.files[0];
    const inputElement = event.target; // Reference to the input element
    if (file) {
      switch (fileType) {
        case 'image':
          this.selectedImg = file;
          this.NewRessource.imageFile = file;
          break;
        case 'video':
          this.selectedVedio = file;
          this.NewRessource.videoFile = file;
          this.loadVideoPreview(file); // Load video preview when selected
          break;
        case 'pdf':
          this.selectedPdf = file;
          this.NewRessource.pdfFile = file;
          break;
        default:
          break;
      }
      // Clear the input field after selecting the file
      inputElement.value = ''; // This clears the file input field
    }
  }

    calculatePagination(): void {
        if (this.lessonsPage && this.lessonsPage.totalPages) {
            this.totalPages = this.lessonsPage.totalPages;
            this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
        }
    }

    changePage(page: number, event: Event): void {
        event.preventDefault();
        this.currentPage = page;
        this.loadLessons(page + 1);
    }

    nextPage(event: Event): void {
        event.preventDefault();
        if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.loadLessons(this.currentPage + 1);
        }
    }

    prevPage(event: Event): void {
        event.preventDefault();
        if (this.currentPage > 0) {
            this.currentPage--;
            this.loadLessons(this.currentPage + 1);
        }
    }
  getAllResourcesByLessonId(): void {
    if (this.selectedLessonId==null) {
      this.errorMessage = 'Lesson ID is not provided.';
      console.error('Please select a lesson.');

      return;
    }

    this.resourceService.getAllResourcesByLessonId(this.selectedLessonId).subscribe(
      (resources: ResourceResponse[]) => {
        this.resources = resources;
      },
      (error) => {
        console.error('Error fetching lesson resources:', error);
        this.toastr.error('An error occurred while fetching lesson resources.');
      }
    );
  }

/*    addResourceForLesson(lessonId: number): void {
        if (lessonId !== null) {
            console.log('Adding resource for lesson:', lessonId);

            if (!this.selectedImg && !this.selectedVedio && !this.selectedPdf) {
                console.error('No files selected.');
                this.showErrorMessage('Please select at least one file (image, video, or PDF).');
                return; // Exit function if no files are selected
            }

            const formData = new FormData();
            formData.append('lessonId', lessonId.toString());

            if (this.selectedImg) {
                console.log('Adding image file:', this.selectedImg.name);
                formData.append('imageFile', this.selectedImg);
            }

            if (this.selectedVedio) {
                console.log('Adding video file:', this.selectedVedio.name);
                formData.append('videoFile', this.selectedVedio);
            }

            if (this.selectedPdf) {
                console.log('Adding PDF file:', this.selectedPdf.name);
                formData.append('pdfFile', this.selectedPdf);
            }

            this.resourceService.createResource(formData, lessonId)
                .subscribe(
                    (response: ResourceResponse) => {
                        console.log('Resource added successfully:', response);
                        this.getAllResourcesByLessonId();
                        this.showSuccessMessage('Resource added successfully!');

                        // Reset selected files
                        this.selectedImg = undefined;
                        this.selectedVedio = undefined;
                        this.selectedPdf = undefined;

                        // Clear file input spans
                        this.clearFileInputs();
                    },
                    (error) => {
                        console.error('Error adding resource:', error);
                        let errorMessage = "An error occurred while adding the resource.";
                        if (error.error && error.error.errorMsg) {
                            errorMessage = error.error.errorMsg;
                        }
                        this.showErrorMessage(errorMessage);
                    }
                );
        } else {
            console.error('Lesson ID is null.');
            this.showErrorMessage('Lesson ID is null. Please select a lesson.');
        }
    }
    clearFileInputs(): void {
        const imgSpan = document.getElementById('imgSpan');
        const videoSpan = document.getElementById('videoSpan');
        const pdfSpan = document.getElementById('pdfSpan');

        if (imgSpan) {
            imgSpan.textContent = '';
        }
        if (videoSpan) {
            videoSpan.textContent = '';
        }
        if (pdfSpan) {
            pdfSpan.textContent = '';
        }
    }

 */

  addResourceForLesson(lessonId: number): void {
    if (lessonId !== null) {
      if (!this.selectedImg && !this.selectedVedio && !this.selectedPdf) {
        this.showErrorMessage('Please select at least one file (image, video, or PDF).');
        return;
      }

      const formData = new FormData();
      formData.append('lessonId', lessonId.toString());

      if (this.selectedImg) {
        formData.append('imageFile', this.selectedImg);
      }
      if (this.selectedVedio) {
        formData.append('videoFile', this.selectedVedio);
      }
      if (this.selectedPdf) {
        formData.append('pdfFile', this.selectedPdf);
      }

      this.resourceService.createResource(formData, lessonId).subscribe(
        (response: ResourceResponse) => {
          this.getAllResourcesByLessonId();
          this.showSuccessMessage('Resource added successfully!');
          this.resetSelectedFiles();
        },
        (error) => {
          let errorMessage = "An error occurred while adding the resource.";
          if (error.error && error.error.errorMsg) {
            errorMessage = error.error.errorMsg;
          }
          this.showErrorMessage(errorMessage);
        }
      );
    } else {
      this.showErrorMessage('Lesson ID is null. Please select a lesson.');
    }
  }
  resetSelectedFiles(): void {
    this.selectedImg = undefined;
    this.selectedVedio = undefined;
    this.selectedPdf = undefined;
    this.clearFileInputs();
  }

  clearFileInputs(): void {
    this.videoFileInput.nativeElement.value = '';
    this.pdfFileInput.nativeElement.value = '';
    this.imageFileInput.nativeElement.value = '';
  }
  // Method to handle search
    searchLessons(keyword: string): void {
        if (this.searchTerm.trim() !== '') {
            this.lessonService.findLessonByTitleContaining(keyword)
                .subscribe(
                    (lessons) => {
                        // Handle the search results
                        console.log('Search results:', lessons);
                        // Update the lessonsPage with the search results
                        this.lessonsPage = { content: lessons, totalPages: lessons.length };
                        this.calculatePagination(); // Recalculate pagination based on search results
                    },
                    (error) => {
                        console.error('Error searching lessons:', error);
                        this.errorMessage = "An error occurred while searching for lessons.";
                    }
                );
        }
    }

    editLesson(lesson: any) {
        console.log("editing lesson loading... ");
    }

  uploadNewPdf(): void {
    const fileInput = document.getElementById('pdf') as HTMLInputElement;
    if (fileInput) {
      fileInput.addEventListener('change', (event) => {
        const file: File = (event.target as HTMLInputElement).files![0];
        this.selectedPdf = file;
      });
      fileInput.click(); // Trigger the file input click event
    }
  }

  uploadNewVideo(): void {
    const fileInput = document.getElementById('video') as HTMLInputElement;
    if (fileInput) {
      fileInput.addEventListener('change', (event) => {
        const file: File = (event.target as HTMLInputElement).files![0];
        this.selectedVedio = file;
      });
      fileInput.click(); // Trigger the file input click event
    }}

    onPlayerReady(source: VgApiService) {
        this.api = source;

        this.api.getDefaultMedia().subscriptions.loadedMetadata.subscribe(
            this.autoplay.bind(this)
        )

    }
    autoplay(){
        this.api.play();
    }

    handlePdfError(event: Event): void {
        console.error('Error opening PDF:', event);
    }


    openPdf(resource: any): void {
        this.selectedResource = resource;
        // Set pdfSrc based on the selected lesson's pdfFile property
        // Assuming pdfFile is the property that holds the PDF data
        this.pdfSrc = resource.pdfFile;
    }


    closePdf(): void {
        this.selectedResource = null; // Reset selectedLesson to close the PDF viewer
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

    onImageFileSelected(event: any, lesson: any): void {
        const file = event.target.files[0];
        if (file) {
            // Assuming lesson has an imageFile property
            lesson.imageFile = file;
        }

    }
    onVideoFileSelected(event: any, lesson: any): void {
        const file = event.target.files[0];
        if (file) {
            // Assuming lesson has a videoFile property
            lesson.videoFile = file;
            this.loadVideoPreview(file); // Load video preview when selected
        }
    }

    onPdfFileSelected(event: any, lesson: any): void {
        const file = event.target.files[0];
        if (file) {
            // Assuming lesson has a pdfFile property
            lesson.pdfFile = file;
        }
    }

  selectLesson(lessonId: number) :void{
      this.selectedLessonId=lessonId;
      this.getAllResourcesByLessonId()
  }

    updateResource(resource: ResourceResponse): void {
        if (resource) {
            const resourceId = resource.id;
            const formData = new FormData();

            if (this.selectedPdf) {
                formData.append('pdfFile', this.selectedPdf);
            }
            if (this.selectedImg) {
                formData.append('imageFile', this.selectedImg);
            }
            if (this.selectedVedio) {
                formData.append('videoFile', this.selectedVedio);
            }

            this.resourceService.updateResource(resourceId, formData).subscribe(
                () => {
                    this.showSuccessMessage('Resource updated successfully.');
                    console.log('Resource updated successfully');
                    this.getAllResourcesByLessonId();

                    // Clear file input spans
                    this.clearFileInputs();
                },
                (error: any) => {
                    console.error('Error updating resource:', error);
                    this.showErrorMessage('Failed to update resource. Please try again later.');
                }
            );
        }
    }
  selectResource(ressource: ResourceResponse) {
this.selectedResource=ressource;
  }
  deleteResource(resourceId: number): void {
    // You can implement the logic here to delete the resource
    console.log('Deleting resource with ID:', resourceId);
    // Call the service method to delete the resource
    this.resourceService.deleteResource(resourceId).subscribe(
      () => {
        console.log('Resource deleted successfully.');
        // Show success message
        this.showSuccessMessage('Resource deleted successfully.');
        this.getAllResourcesByLessonId(); // Reload resources after deleting a resource
      },
      (error) => {
        console.error('Error deleting resource:', error);
        let errorMessage = "An error occurred while deleting the resource.";
        if (error.error && error.error.errorMsg) {
          errorMessage = error.error.errorMsg;
        }
        // Show error message
        this.showErrorMessage(errorMessage);
      }
    );
  }
}
