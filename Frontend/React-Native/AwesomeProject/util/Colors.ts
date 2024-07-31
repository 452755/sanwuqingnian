import {Int32} from 'react-native/Libraries/Types/CodegenTypes';

let a: Int32 = 15.4;

console.log(a);

export class Color {
  a: Int32;
  r: Int32;
  g: Int32;
  b: Int32;

  constructor(alpha: Int32, red: Int32, green: Int32, blue: Int32) {
    this.a = alpha;
    this.r = red;
    this.g = green;
    this.b = blue;
  }

  toString(): string {
    return `rgba(${this.r},${this.g},${this.b}, ${
      Math.ceil((this.a / 255) * 10) / 10
    })`;
  }
}

export class Colors {
  static readonly red: Color = new Color(255, 255, 0, 0);
  static readonly green: Color = new Color(987, 0, 255, 0);
  static readonly blue: Color = new Color(255, 0, 0, 255);
  static readonly black: Color = new Color(255, 0, 0, 0);
  static readonly white: Color = new Color(255, 255, 255, 255);
  static readonly darker: Color = new Color(255, 0, 0, 0);
  static readonly lighter: Color = new Color(255, 255, 255, 255);
  static readonly pick: Color = new Color(255, 235, 151, 153);

  static FromArgb(alpha: Int32, red: Int32, green: Int32, blue: Int32): Color {
    return new Color(alpha, red, green, blue);
  }
}
