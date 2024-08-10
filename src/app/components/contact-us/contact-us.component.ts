import { Component } from '@angular/core';
import { ContactService } from '../../services/contact/contact.service';
import { ToastrService } from 'ngx-toastr';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-contactus',
  templateUrl: './contact-us.component.html',
  styleUrls: ['./contact-us.component.css']
})
export class ContactUsComponent {
  contactForm: FormGroup;

  constructor(
    private contactService: ContactService,
    private toastr: ToastrService,
    private formBuilder: FormBuilder
  ) {
    this.contactForm = this.formBuilder.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      subject: ['', Validators.required],
      message: ['', Validators.required]
    });
  }

  get formControls() {
    return this.contactForm.controls;
  }

  submitForm() {
    if (this.contactForm.invalid) {
      this.showValidationErrorToast();
      return;
    }
    const contactData = this.contactForm.value;
    this.contactService.sendMessage(contactData).subscribe({
      next: () => {
        this.showSuccessToast();
        this.resetForm();
      },
      error: () => {
        this.showErrorToast();
      }
    });
  }

  resetForm() {
    this.contactForm.reset();
  }

  private showValidationErrorToast() {
    this.toastr.error('Please fill in all the required fields with correct information.', 'Form Validation Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }

  private showSuccessToast() {
    this.toastr.success('Message sent successfully.', 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  private showErrorToast() {
    this.toastr.error('An unexpected error occurred. Please try again later.', 'Server Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error',
    });
  }
}
