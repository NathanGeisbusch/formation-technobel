import {FormGroup} from "@angular/forms";

export const CLASS_INVALID_FORM = 'ng-invalid ng-dirty';

/** Recursively map all FormGroup errors into an object. */
export function getAllFormErrors(form: FormGroup): any {
  const errors: any = {};
  for(let key in form.controls) {
    const value = form.get(key)!;
    const controlErrors = value.errors;
    if(value instanceof FormGroup) errors[key] = getAllFormErrors(value as any);
    else if(controlErrors != null) {
      for(let keyError in controlErrors) {
        errors[key] ||= {};
        errors[key][keyError] = controlErrors[keyError];
      }
    }
  }
  return errors;
}

/** Recursively check if there's a form error. */
export function hasAnyFormErrors(form: FormGroup): boolean {
  for(let key in form.controls) {
    const value = form.get(key)!;
    if(value instanceof FormGroup) {
      const nestedErrors = getAllFormErrors(value as any);
      if(nestedErrors) return true;
    }
    else if(value.errors != null) return true;
  }
  return false;
}
