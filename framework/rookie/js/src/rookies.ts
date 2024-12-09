import {
  DefaultObjectSerilaze,
  DefaultProtoSerilaze,
} from "./serilaze/DefaultSerilaze";
import { ProtoSerilaze, Serilaze } from "./serilaze/Serilaze";

export interface Proto {
  protoId: number;
}

export type Field = {
  name: string;
  type: DataType;
  arr: boolean;
};

const FIELDS_KEY = "$_class";
export type ClassInfo = {
  id: number;
  type: DataType;
  serializer?: Serilaze;
  array: boolean;
  fields: Field[];
  valueType?: DataType;
};

/**
 * Rookie
 * support:
 * double | float | int32 | float64 | int64
 * string | bool | bytes
 * list<Proto | number | string | number | string>
 * map<number | string, Proto | number | string | number[]>
 *
 * Not support:
 * list<list | map>
 * map<number | string, list | string[] | map>
 */
export class Rookie {
  classInfo: Record<number, ClassInfo> = {};
  baseInfo: Record<string, ClassInfo> = {};
  status: number = 0;
  // Not support base type
  serialze(data: Proto): ArrayBuffer {
    this._check();
    const id = data.protoId;
    const classInfo = this.classInfo[id];
    if (!classInfo) {
      throw new Error("classInfo not found:" + id);
    }
    const serilaze = classInfo.serializer;
    if (!serilaze) {
      throw new Error("serializer not found:" + id);
    }
    return new DefaultProtoSerilaze(serilaze, this).serilaze(data);
  }

  deserilaze(id: number, buff: ArrayBuffer): Proto {
    this._check();
    const classInfo = this.classInfo[id];
    if (!classInfo) {
      throw new Error("classInfo not found:" + id);
    }
    const serilaze = classInfo.serializer;
    if (!serilaze) {
      throw new Error("serializer not found:" + id);
    }
    return new DefaultProtoSerilaze(serilaze, this).deserilaze(id, buff);
  }

  register(id: number, classInfo: any) {
    if (!classInfo.constructor[FIELDS_KEY]) {
      throw new Error(id + " classInfo must be a class");
    }
    if (this.classInfo[id]) {
      throw new Error(id + " classInfo already exists");
    }
    this.classInfo[id] = {
      id: id,
      fields: classInfo[FIELDS_KEY],
      type: id,
      array: false,
    };
  }

  getClassInfo(key: DataType): ClassInfo {
    if (typeof key === "number") {
      return this.classInfo[key];
    }
    return this.baseInfo[key as string];
  }

  _check() {
    if (this.status === 0) {
      this._init();
      this.status = 1;
    }
  }

  _init() {
    for (const key in this.classInfo) {
      const classInfo = this.classInfo[key];
      if (!classInfo.serializer) {
        classInfo.serializer = new DefaultObjectSerilaze(
          classInfo.fields,
          this
        );
      }
    }
  }
}

export type DataType =
  | "double"
  | "float32"
  | "float64"
  | "int8"
  | "int16"
  | "int32"
  | "int64"
  | "string"
  | "bool"
  | "bytes"
  | "list"
  | "map"
  | "set"
  | "array"
  | Number;

/**
 *
 * @param type -- object type
 * @param arr -- is array
 * @param valueType -- map value type. It's required when type is map
 * @returns
 */
export const Field = (
  type: DataType,
  valueType?: DataType,
  mapValueType?: DataType
) => {
  return (target: any, propertyKey: string) => {
    // Ensure the target has a $_class metadata object
    if (!target.constructor[FIELDS_KEY]) {
      target.constructor[FIELDS_KEY] = [];
    }

    // Add or update field metadata
    target.constructor[FIELDS_KEY].push({
      name: propertyKey,
      type,
      valueType,
      mapValueType,
    });
  };
};
