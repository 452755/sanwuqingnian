import { FC } from "react";

/** Error thrown when an action is aborted. */
export class ArgumentNullError extends Error {
    // @ts-ignore: Intentionally unused.
    // eslint-disable-next-line @typescript-eslint/naming-convention
    private __proto__: Error;

    public paramName: string;

    /** Constructs a new instance of {@link AbortError}.
     *
     * @param {string} errorMessage A descriptive error message.
     */
    constructor(paramName: string) {
        const trueProto = new.target.prototype;
        console.log(trueProto)
        const msg = paramName + " 不能为空";
        super(msg);
        this.paramName = paramName
        this.name = 'ArgumentNullError'
        // Workaround issue in Typescript compiler
        // https://github.com/Microsoft/TypeScript/issues/13965#issuecomment-278570200
        this.__proto__ = trueProto;
    }
}

export abstract class ViewHolder {
    private itemView: FC | JSX.Element | React.ReactNode;

    constructor(itemView: FC | JSX.Element | React.ReactNode) {
        if (itemView === null || itemView === undefined) {
            throw new ArgumentNullError("itemView")
        }
        this.itemView = itemView;
    }
}

export abstract class Adapter<VH extends ViewHolder> {

}

export default function RecyclerView(children? :React.ReactNode) {
    return (<div>{ children }</div>)
}