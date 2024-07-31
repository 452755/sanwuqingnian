import "reflect-metadata";

// 自定义装饰器，用于创建代理对象
function delegateProxy(target: any) {
    const handler = {
        get: function (target: any, propKey: PropertyKey, receiver: any) {
            const value = Reflect.get(target, propKey, receiver);
            if (typeof value === 'function') {
                // 如果属性是一个函数，将其绑定到原始对象上
                return value.bind(target);
            }
            return value;
        },
        set: function (target: any, p: string | symbol, newValue: any, receiver: any): boolean {
            return true;
        }
    };

    return new Proxy(target, handler);
}


// interface IFuncBase<T extends any[], TResult> {
//   _handles: FuncBase<T, TResult>[]
//   add(handle: FuncBase<T, TResult>): void
//   remove(handle: FuncBase<T, TResult>): void
//   invoke(...args: T): TResult
// }
  
//type FuncBase<T extends any[], TResult> = IFuncBase<T, TResult> & ((...args: T) => TResult)

@delegateProxy
class delegate {
  
}

interface Action<T extends any[], TResult> {
  (...args: T): TResult
}

const a: Action<[], void> = ()=>{}

// class aAction<T extends any[], TResult> implements Action<T, TResult> {
//     // 实现接口定义
//     public execute(...args: any[]): number {
//         // 在这里实现具体的逻辑
//         return args.reduce((sum, current) => sum + current, 0);
//     }
// }