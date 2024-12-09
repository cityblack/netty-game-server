import { ProtoSerilaze, Serilaze } from "./Serilaze";
import { ClassInfo, Field, Proto, Rookie } from "../rookies";
import Memory, { memory } from "../memonry";
import { EMPTY_INDEX } from "../constant";

export class DefaultProtoSerilaze implements ProtoSerilaze {
  private _serilaze: Serilaze;
  private _rookie: Rookie;
  constructor(serilaze: Serilaze, rookie: Rookie) {
    this._serilaze = serilaze;
    this._rookie = rookie;
  }

  serilaze(data: Proto): ArrayBuffer {
    const info = this._rookie.getClassInfo(data.protoId);
    const mem = new Memory(new ArrayBuffer(1024));
    this._serilaze.serilaze(data, info, mem);
    return mem.getBuff();
  }

  deserilaze(id: number, buff: ArrayBuffer): Proto {
    const info = this._rookie.getClassInfo(id);
    const mem = new Memory(buff);
    return this._serilaze.deserilaze(info, mem);
  }
}

export class DefaultObjectSerilaze implements Serilaze {
  private _rookie: Rookie;
  constructor(rookie: Rookie) {
    this._rookie = rookie;
  }
  serilaze(data: any, classInfo: ClassInfo, mem: memory): void {
    const fields = classInfo.fields;
    for (const field of fields) {
      const name = field.name;
      const value = data[name];
      const info = this._rookie.getClassInfo(field.type);
      info.serializer?.serilaze(value, info, mem);
    }
  }
  deserilaze(classInfo: ClassInfo, mem: memory) {
    const data: any = {};
    const fields = classInfo.fields;
    for (const field of fields) {
      const name = field.name;
      const info = this._rookie.getClassInfo(field.type);
      data[name] = info.serializer?.deserilaze(info, mem);
    }
    return data;
  }
}

export class ArraySerilaze implements Serilaze {
  rookie: Rookie;
  constructor(rookie: Rookie) {
    this.rookie = rookie;
  }

  serilaze(data: any, classInfo: ClassInfo, mem: memory): void {
    const len = data.length;
    mem.writeRawVarint32(len);
    mem.writeInt16(classInfo.id);
    for (const item of data) {
      classInfo.serializer?.serilaze(item, classInfo, mem);
    }
  }
  deserilaze(classInfo: ClassInfo, mem: memory) {
    const len = mem.readRawVarint32();
    const data = [];
    for (let i = 0; i < len; i++) {
      data.push(classInfo.serializer?.deserilaze(classInfo, mem));
    }
    return data;
  }
}

const writeElement = (data: any, info: ClassInfo, mem: memory) => {
  if (!data) {
    mem.writeInt16(EMPTY_INDEX);
    return;
  }
  mem.writeInt16(info.id);
  info.serializer?.serilaze(data, info, mem);
};

const readElement = (mem: memory, rookie: Rookie) => {
  const id = mem.readInt16();
  if (id === EMPTY_INDEX) {
    return null;
  }
  const info = rookie.getClassInfo(id);
  return info.serializer?.deserilaze(info, mem);
};

export class ListSerilaze implements Serilaze {
  rookie: Rookie;
  constructor(rookie: Rookie) {
    this.rookie = rookie;
  }
  serilaze(data: any, classInfo: ClassInfo, mem: memory): void {
    const len = data.length;
    mem.writeRawVarint32(len);
    // mem.writeInt16(classInfo.id);
    for (const item of data) {
      writeElement(item, classInfo, mem);
    }
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    const len = mem.readRawVarint32();
    const data = [];
    for (let i = 0; i < len; i++) {
      data.push(readElement(mem, this.rookie));
    }
    return data;
  }
}

export class MapSerilaze implements Serilaze {
  rookie: Rookie;
  constructor(rookie: Rookie) {
    this.rookie = rookie;
  }
  serilaze(data: any, classInfo: ClassInfo, mem: memory): void {
    if (!classInfo.valueType) {
      throw new Error(classInfo.id + " valueType not defined");
    }
    const keys = Object.keys(data);
    const len = keys.length;
    const valueInfo = this.rookie.getClassInfo(classInfo.valueType);
    mem.writeRawVarint32(len);
    for (const key of keys) {
      const value = data[key];
      writeElement(key, classInfo, mem);
      writeElement(value, valueInfo, mem);
    }
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    const len = mem.readRawVarint32();
    const data: any = {};
    for (let i = 0; i < len; i++) {
      const key = readElement(mem, this.rookie);
      const value = readElement(mem, this.rookie);
      data[key] = value;
    }
    return data;
  }
}
