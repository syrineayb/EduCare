import { AbstractControl, ValidatorFn } from "@angular/forms";

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const now = new Date();
    const selectedDate = new Date(control.value);

    // Reset time part to 00:00:00 for comparison
    now.setHours(0, 0, 0, 0);
    selectedDate.setHours(0, 0, 0, 0);

    if (selectedDate < now) {
      return { 'pastDate': { value: control.value } };
    }

    return null;
  };
}
