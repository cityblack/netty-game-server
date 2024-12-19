import { Serilaze } from "./Serilaze";
import { ClassInfo, EMPTY_ID } from "../rookies";
import Rookie from "../rookies";
import Memory from "../memonry";
import { StructDesc } from "../meta";

export const protoDeserilaze = (
  id: number,
  buff: ArrayBuffer,
  rookie: Rookie
) => {
  const info = rookie.getClassInfo(id);
  const mem = new Memory(buff);
  return info.serializer?.deserilaze(info, mem);
};

export class DefaultObjectSerilaze implements Serilaze {
  private _rookie: Rookie;
  constructor(rookie: Rookie) {
    this._rookie = rookie;
  }
  serilaze(data: any, desc: StructDesc, mem: Memory): void {
    const classInfo = this._rookie.getClassInfo(desc.type);
    const fields = classInfo.fields;
    if (fields) {
      for (const field of fields) {
        const name = field.name;
        const value = data[name];
        const info = this._rookie.getClassInfo(field.type);
        if (!field.writeClass) {
          info.serializer?.serilaze(value, info, mem);
        } else {
          writeElement(value, field, info, mem);
        }
      }
    }
  }
  deserilaze(desc: StructDesc, mem: Memory) {
    const data: any = {};
    const classInfo = this._rookie.getClassInfo(desc.type);
    const fields = classInfo.fields;
    if (fields) {
      for (const field of fields) {
        const name = field.name;
        const info = this._rookie.getClassInfo(field.type);
        if (!field.writeClass) {
          data[name] = info.serializer?.deserilaze(field, mem);
        } else {
          data[name] = readElement(mem, this._rookie);
        }
      }
    }
    return data;
  }
}

export class ArraySerilaze implements Serilaze {
  rookie: Rookie;
  constructor(rookie: Rookie) {
    this.rookie = rookie;
  }

  serilaze(data: any, desc: StructDesc, mem: Memory): void {
    if (!desc.valueType) {
      throw new Error(desc.type + " valueType not defined");
    }
    const len = data.length;
    mem.writeRawVarint32(len);
    const classInfo = this.rookie.getClassInfo(desc.valueType);
    mem.writeInt16(classInfo.id);
    for (const item of data) {
      classInfo.serializer?.serilaze(item, classInfo, mem);
    }
  }
  deserilaze(_: ClassInfo, mem: Memory) {
    const len = mem.readRawVarint32();
    const data = [];
    const classInfo = this.rookie.getClassInfo(mem.readInt16());
    for (let i = 0; i < len; i++) {
      data.push(classInfo.serializer?.deserilaze(classInfo, mem));
    }
    return data;
  }
}

const writeElement = (
  data: any,
  desc: StructDesc,
  info: ClassInfo,
  mem: Memory
) => {
  if (!data) {
    mem.writeInt16(EMPTY_ID);
    return;
  }
  mem.writeInt16(info.id);
  info.serializer?.serilaze(data, desc, mem);
};

const readElement = (mem: Memory, rookie: Rookie) => {
  const id = mem.readInt16();
  if (id === EMPTY_ID) {
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
  serilaze(data: any, desc: StructDesc, mem: Memory): void {
    if (desc.valueType) {
      const len = data.length;
      mem.writeRawVarint32(len);
      if (len > 0) {
        const classInfo = this.rookie.getClassInfo(desc.valueType);
        for (const item of data) {
          writeElement(item, classInfo, classInfo, mem);
        }
      }
    }
  }
  deserilaze(_: StructDesc, mem: Memory): any {
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
  serilaze(data: any, desc: StructDesc, mem: Memory): void {
    if (!desc.type || !desc.valueType || !desc.mapValueType) {
      throw new Error(desc.type + " valueType not defined");
    }
    const keys = Object.keys(data);
    const len = keys.length;

    const keyInfo = this.rookie.getClassInfo(desc.valueType);
    const valueInfo = this.rookie.getClassInfo(desc.mapValueType);

    mem.writeRawVarint32(len);
    for (const key of keys) {
      const value = data[key];
      writeElement(key, keyInfo, keyInfo, mem);
      writeElement(value, valueInfo, valueInfo, mem);
    }
  }
  deserilaze(_: ClassInfo, mem: Memory): any {
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

export class DateSerilaze implements Serilaze {
  serilaze(data: any, _: StructDesc, mem: Memory): void {
    mem.writeInt64(data.getTime());
  }
  deserilaze(_: StructDesc, mem: Memory): any {
    return new Date(mem.readInt64());
  }
}
