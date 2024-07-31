export type Nullable<T> = T | null | undefined

// interface FuncConstructor<T extends any[], TResult> {
//   (handle: FuncBase<T, TResult>): FuncBase<T, TResult>
//   new(handle: FuncBase<T, TResult>): FuncBase<T, TResult>
// }

// declare class Func<T extends any[], TResult> implements FuncBase<T, TResult> {

//   apply(this: Function, thisArg: any, argArray?: any)
//   call(this: Function, thisArg: any, ...argArray: any[])
//   bind(this: Function, thisArg: any, ...argArray: any[])
//   toString(): string
//   prototype: any
//   length: number
//   arguments: any
//   caller: Function
//   name: string
//   [Symbol.hasInstance](value: any): boolean

//   _handles: FuncBase<T, TResult>[]
//   add(handle: FuncBase<T, TResult>): void
//   remove(handle: FuncBase<T, TResult>): void
//   invoke(...args: T): TResult
  
// }



// class Func<T extends any[], TResult> extends Function {
//   _handlers: Func<T, TResult>[]

//   constructor(handler: Func<T, TResult>) {
//     super('...args', 'return this.invoke(...args)')
//     this._handlers = []
//     this._handlers.push(handler)
//     return this.bind(this);
//   }

//   add(handle: Func<T, TResult>): void {
//     if (this._handlers.indexOf(handle) === -1) {
//       this._handlers.push(handle)
//     }
//   }
  
//   remove(handler: Func<T, TResult>) {
//     const index = this._handlers.indexOf(handler)

//     if (index !== -1) {
//       this._handlers.splice(index, 1)
//     }
//   }

//   invoke(...args): TResult {
//     let result: TResult | undefined;

//     for (const handler of this._handlers) {
//       result = handler.apply(handler, args);
//     }

//     return result;
//   }
// }

type FuncBase<T extends any[] = any[], TResult = any> = (...args: T) => TResult

class Func<T extends any[] = any[], TResult = any> extends Function {
  private _handlers: FuncBase<T, TResult>[]

  constructor(handler: FuncBase<T, TResult>) {
    super('...args', 'return this.invoke(...args)')
      this._handlers = []
      this._handlers.push(handler)
      return this.bind(this);
  }

  add() {

  }

  invoke(...args: T) {
    let result: TResult | undefined;

    for (const handler of this._handlers) {
      result = handler.apply(handler, args);
    }

    return result;
  }
}

var func: Func<[number, number], void> = new Func<[number, number], void>((num1: number, num2: number) => {console.log('45')});

func(15, 15)

function n() {

}

//var func2: Func = n as FuncBase
//func2()
