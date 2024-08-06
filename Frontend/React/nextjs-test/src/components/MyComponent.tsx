declare interface FirstPostProp{
  title: string,
  items: any[],
  children : React.ReactNode,
  itemClick: Function
}

export default function FirstPost(props : FirstPostProp) {
  return (
    <div>
      <div>{props.children}</div>
      <div>我是恁爹</div>
    </div>
  );
}

