interface BaseError extends Error{
    innerError?: BaseError
}

interface BaseErrorConstructor extends ErrorConstructor{
    new (): BaseError;
    new (message: string): BaseError;
    new (message: string, innerError: BaseError): BaseError
    (message?: string): BaseError;
    readonly prototype: BaseError;
}

var a = new BaseError()

declare var BaseError: BaseErrorConstructor