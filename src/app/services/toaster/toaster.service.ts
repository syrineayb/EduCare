import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root',
})
export class ToasterService {

  constructor(private toastr: ToastrService) { }

  success(message: string, title?: string, cssClass?: string) {
    this.toastr.success(message, title, {
      toastClass: cssClass
    });
  }

  error(message: string, title?: string, cssClass?: string, inlineStyles?: string) {
    this.toastr.error(message, title, {
      toastClass: cssClass,
      messageClass: inlineStyles
    });
  }

  warning(message: string, title?: string) {
    this.toastr.warning(message, title);
  }

  info(message: string, title?: string) {
    this.toastr.info(message, title);
  }

}
