import { AbstractControl, FormControl, FormArray, FormGroup } from '@angular/forms';

export type FormGroupControls = { [key: string]: AbstractControl };
export type AbstractControlValue<T> = T extends { value: infer Y } ? Y : never;
  
export class EnhancedFormControl<T = any> extends FormControl {
  readonly value!: T;
}
  
export class EnhancedFormArray<T extends AbstractControl> extends FormArray {
  controls!: T[];
  readonly value!: AbstractControlValue<T>[];

  static create<T extends AbstractControl>(controls: T[]) {
    return new EnhancedFormArray<T>(controls);
  }
}
  
export class EnhancedFormGroup<T extends FormGroupControls> extends FormGroup {
  controls!: T;
  readonly value!: { [key in keyof T]: AbstractControlValue<T[key]> };

  static create<T extends FormGroupControls>(controls: T) {
    return new EnhancedFormGroup<T>(controls);
  }
  
  getRawValue(): { [key in keyof T]: AbstractControlValue<T[key]> } {
    return super.getRawValue();
  }
}