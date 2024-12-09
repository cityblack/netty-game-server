import { memory } from "../memonry";
import { Proto, ClassInfo } from "../rookies";

export interface ProtoSerilaze {
  serilaze(data: Proto): ArrayBuffer;
  deserilaze(id: number, buff: ArrayBuffer): Proto;
}

export interface Serilaze {
  serilaze(data: any, field: ClassInfo, mem: memory): void;
  deserilaze(field: ClassInfo, mem: memory): any;
}
