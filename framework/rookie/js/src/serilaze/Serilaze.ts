import Memory from "../memonry";
import { StructDesc } from "../meta";

export interface ProtoSerilaze {
  serilaze(data: any): ArrayBuffer;
  deserilaze(id: number, buff: ArrayBuffer): any;
}

export interface Serilaze {
  serilaze(data: any, desc: StructDesc, mem: Memory): void;
  deserilaze(desc: StructDesc, mem: Memory): any;
}
