import { Injectable } from '@angular/core';
import {MessageService, Message, ConfirmationService, Confirmation} from "primeng/api";

export enum MsgReasonError {
  BAD_CREDENTIALS, EMAIL_ERROR,
  SRC_PARSER, SRC_BUILDER, SRC_GENERATOR,
  PARSER, GENERATOR,
}

export enum MsgReasonSuccess {
  SIGN_IN, SIGN_UP, SIGN_OUT,
  PW_CHANGE_REQ, PW_CHANGE, ACCOUNT_UPDATE,
  PARSER_CREATE, GENERATOR_CREATE, SESSION_CREATE,
  PARSER_UPDATE, GENERATOR_UPDATE, SESSION_UPDATE,
  PARSER_DELETE, GENERATOR_DELETE, SESSION_DELETE,
  PARSERS_DELETE, GENERATORS_DELETE, SESSIONS_DELETE,
  PARSER_VERSION_CREATE, GENERATOR_VERSION_CREATE,
  PARSER_VERSION_DELETE, GENERATOR_VERSION_DELETE,
  COPY_URL_PROTECTED_PARSER, COPY_URL_PROTECTED_GENERATOR,
  COPY_RESULT_FILE
}

export enum DeleteConfirmResource {
  PARSER, GENERATOR, SESSION
}

class MsgError implements Message {
  key = 'toast-error';
  severity = 'error';
  constructor(public summary: string, public detail: string) {}
}

class MsgSuccess implements Message {
  key = 'toast-success';
  severity = 'success';
  constructor(public summary: string, public detail: string) {}
}

class DeleteConfirmDialog implements Confirmation {
  public message: string;
  public header = 'Delete confirmation';
  public acceptButtonStyleClass = 'p-button-danger p-button-text';
  public rejectButtonStyleClass = 'p-button-text p-button-secondary';
  public acceptIcon = 'none';
  public rejectIcon = 'none';
  public acceptLabel = 'Delete';
  public rejectLabel = 'Cancel';
  public accept?: () => void;
  public reject?: () => void;
  constructor(resourceName: string, plural: boolean, accept?: () => void, reject?: () => void) {
    this.message = `Do you want to delete ${plural?'these':'this'} ${resourceName}${plural?'s':''} ?`;
    this.accept = accept;
    this.reject = reject;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AppMessageService {

  public constructor(
    private _messageService: MessageService,
    private _confirmationService: ConfirmationService,
  ) {}

  /** Displays an error message for a failed validation due to a required field. */
  public showValidationFailedRequired(fieldName: string) {
    this._messageService.add(
      new MsgError('Validation failed', `${fieldName} is required`)
    );
  }

  /** Displays an error message for a failed validation due to a value not matching a pattern. */
  public showValidationFailedPattern(fieldName: string, value: string) {
    this._messageService.add(
      new MsgError('Validation failed', `"${value}" is not a valid ${fieldName}`)
    );
  }

  /** Displays an error message for a failed validation due to a resource that already exists. */
  public showValidationFailedAlreadyExists(fieldName: string, value: string) {
    this._messageService.add(
      new MsgError('Validation failed', `The ${fieldName} "${value}" already exists`)
    );
  }

  /**
   * Displays an error message.
   * @param summary message title
   * @param detail message content
   */
  public showCustomError(summary: string, detail: string) {
    this._messageService.add(new MsgError(summary, detail));
  }

  public showError(reason: MsgReasonError) {
    switch(reason) {
      case MsgReasonError.BAD_CREDENTIALS:
        this._messageService.add(
          new MsgError('Authentication failed', 'Bad credentials')
        );
        break;
      case MsgReasonError.EMAIL_ERROR:
        this._messageService.add(
          new MsgError('Server error', 'Email could not be sent')
        );
        break;
      case MsgReasonError.SRC_PARSER:
        this._messageService.add(
          new MsgError('Source parser error', 'The source parser couldn\'t be compiled.')
        );
        break;
      case MsgReasonError.SRC_BUILDER:
        this._messageService.add(
          new MsgError('Source builder error', 'The source builder couldn\'t be compiled.')
        );
        break;
      case MsgReasonError.SRC_GENERATOR:
        this._messageService.add(
          new MsgError('Source generator error', 'The source generator couldn\'t be compiled.')
        );
        break;
      case MsgReasonError.PARSER:
        this._messageService.add(
          new MsgError('Parser error', 'The syntax is invalid.')
        );
        break;
      case MsgReasonError.GENERATOR:
        this._messageService.add(
          new MsgError('Generator error', 'An error have occured during generation.')
        );
        break;
    }
  }

  public showSuccess(reason: MsgReasonSuccess) {
    switch(reason) {
      case MsgReasonSuccess.SIGN_IN:
        this._messageService.add(
          new MsgSuccess('Authentication', 'You are now connected')
        );
        break;
      case MsgReasonSuccess.SIGN_OUT:
        this._messageService.add(
          new MsgSuccess('Authentication', 'You are now disconnected')
        );
        break;
      case MsgReasonSuccess.SIGN_UP:
        this._messageService.add(new MsgSuccess(
          'Sign up',
          'Your account have been created and you have received a confirmation email.'
        ));
        break;
      case MsgReasonSuccess.PW_CHANGE_REQ:
        this._messageService.add(new MsgSuccess(
          'Change password',
          'You have received a email with an link to change your password.'
        ));
        break;
      case MsgReasonSuccess.PW_CHANGE:
        this._messageService.add(
          new MsgSuccess('Change password', 'Your password have been changed successfully.')
        );
        break;
      case MsgReasonSuccess.ACCOUNT_UPDATE:
        this._messageService.add(
          new MsgSuccess('Account', 'Account updated successfully.')
        );
        break;
      case MsgReasonSuccess.PARSER_CREATE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser created successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATOR_CREATE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator created successfully.')
        );
        break;
      case MsgReasonSuccess.PARSER_DELETE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser deleted successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATOR_DELETE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator deleted successfully.')
        );
        break;
      case MsgReasonSuccess.PARSERS_DELETE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parsers deleted successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATORS_DELETE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generators deleted successfully.')
        );
        break;
      case MsgReasonSuccess.SESSION_DELETE:
        this._messageService.add(
          new MsgSuccess('Session', 'Session deleted successfully.')
        );
        break;
      case MsgReasonSuccess.SESSIONS_DELETE:
        this._messageService.add(
          new MsgSuccess('Session', 'Sessions deleted successfully.')
        );
        break;
      case MsgReasonSuccess.PARSER_UPDATE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser updated successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATOR_UPDATE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator updated successfully.')
        );
        break;
      case MsgReasonSuccess.SESSION_UPDATE:
        this._messageService.add(
          new MsgSuccess('Session', 'Session updated successfully.')
        );
        break;
      case MsgReasonSuccess.COPY_URL_PROTECTED_PARSER:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser link have been copied to your clipboard.')
        );
        break;
      case MsgReasonSuccess.COPY_URL_PROTECTED_GENERATOR:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator link have been copied to your clipboard.')
        );
        break;
      case MsgReasonSuccess.COPY_RESULT_FILE:
        this._messageService.add(
          new MsgSuccess('Result file', 'File content have been copied to your clipboard.')
        );
        break;
      case MsgReasonSuccess.PARSER_VERSION_CREATE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser version created successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATOR_VERSION_CREATE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator version created successfully.')
        );
        break;
      case MsgReasonSuccess.PARSER_VERSION_DELETE:
        this._messageService.add(
          new MsgSuccess('Parser', 'Parser version deleted successfully.')
        );
        break;
      case MsgReasonSuccess.GENERATOR_VERSION_DELETE:
        this._messageService.add(
          new MsgSuccess('Generator', 'Generator version deleted successfully.')
        );
        break;
    }
  }

  /**
   * Displays a delete confirmation dialog.
   * @param resource type of the resource
   * @param multiple if multiple resources must be deleted (for indicating plural)
   * @param onAccept callback called when the user accepts the confirmation
   * @param onReject callback called when the user rejects the confirmation
   */
  public deleteConfirmDialog(
    resource: DeleteConfirmResource, multiple: boolean,
    onAccept?: () => void, onReject?: () => void
  ) {
    let resourceName: string;
    switch(resource) {
      case DeleteConfirmResource.PARSER:
        resourceName = 'parser';
        break;
      case DeleteConfirmResource.GENERATOR:
        resourceName = 'generator';
        break;
      case DeleteConfirmResource.SESSION:
        resourceName = 'session';
        break;
    }
    const dialog = new DeleteConfirmDialog(resourceName, multiple, onAccept, onReject);
    this._confirmationService.confirm(dialog);
  }
}

