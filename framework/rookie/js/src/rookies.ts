import {
  ArraySerilaze,
  DefaultObjectSerilaze,
  protoSerilaze,
  protoDeserilaze,
} from "./serilaze/DefaultSerilaze";
import { Serilaze } from "./serilaze/Serilaze";
import {
  BoolSerilaze,
  Float32Serilaze,
  Float64Serilaze,
  Int16Serilaze,
  Int64Serilaze,
  Int32Serilaze,
  Int8Serilaze,
  StringSerilaze,
} from "./serilaze/SimpleSerilaze";
import {
  ListSerilaze,
  MapSerilaze,
  DateSerilaze,
} from "./serilaze/DefaultSerilaze";
import { DataType, FieldInfo, FIELDS_KEY, ID_KEY, StructDesc } from "./meta";

export const EMPTY_ID = 1,
  ARRAY_ID = 0,
  DEFEIND_MIN_ID = 0,
  DEFINED_MAX_ID = 100;

export interface ClassInfo extends StructDesc {
  id: number;
  serializer?: Serilaze;
  fields?: FieldInfo[];
}

/**
 * Rookie
 * support:
 * double | float | int32 | float64 | int64
 * string | bool | bytes
 * list<Proto | number | string | number | string | []>
 * map<number | string, Proto | number | string | []>
 */
export default class Rookie {
  classInfo: Record<number, ClassInfo> = {};
  baseInfo: Record<string, ClassInfo> = {};
  status: number = 0;
  // Not support base type
  serialze(data: any): ArrayBuffer {
    this._check();
    const id = data[ID_KEY];
    if (!id) {
      throw new Error("classInfo must have id");
    }
    const classInfo = this.classInfo[id];
    if (!classInfo) {
      throw new Error("classInfo not found:" + id);
    }
    const serilaze = classInfo.serializer;
    if (!serilaze) {
      throw new Error("serializer not found:" + id);
    }
    return protoSerilaze(id, data, this);
  }

  deserilaze(id: number, buff: ArrayBuffer): any {
    this._check();
    const classInfo = this.classInfo[id];
    if (!classInfo) {
      throw new Error("classInfo not found:" + id);
    }
    const serilaze = classInfo.serializer;
    if (!serilaze) {
      throw new Error("serializer not found:" + id);
    }
    return protoDeserilaze(id, buff, this);
  }

  register(classInfo: any) {
    const id = classInfo[ID_KEY];
    if (!id) {
      throw new Error("classInfo must have id");
    }
    if (id >= DEFEIND_MIN_ID && id <= DEFINED_MAX_ID) {
      throw new Error(
        "Rookie register id must be greater than " +
          DEFEIND_MIN_ID +
          " and less than " +
          DEFINED_MAX_ID
      );
    }
    const property = classInfo[FIELDS_KEY];
    if (!property) {
      throw new Error(id + " classInfo must be a class");
    }
    if (this.classInfo[id]) {
      throw new Error(id + " classInfo already exists");
    }
    this.classInfo[id] = {
      id: id,
      fields: classInfo[FIELDS_KEY],
      type: id,
      writeClass: true,
    };
  }

  getClassInfo(key: DataType): ClassInfo {
    if (typeof key === "number") {
      return this.classInfo[key];
    }
    return this.baseInfo[key as string];
  }

  _registerClassInfo(classInfo: ClassInfo) {
    if (this.classInfo[classInfo.id]) {
      throw new Error(classInfo.id + " classInfo already exists");
    }
    this.classInfo[classInfo.id] = classInfo;
    if (typeof classInfo.type != "number") {
      this.baseInfo[classInfo.type as string] = classInfo;
    }
  }

  _check() {
    if (this.status === 0) {
      this._init();
      this.status = 1;
    }
  }

  _init() {
    const rigster = new RigsterDefined(this);
    rigster.register();
    for (const key in this.classInfo) {
      const classInfo = this.classInfo[key];
      if (!classInfo.serializer) {
        classInfo.serializer = new DefaultObjectSerilaze(this);
      }
    }
  }
}

class RigsterDefined {
  private _rookie: Rookie;
  private _index: number;
  constructor(rookie: Rookie) {
    this._rookie = rookie;
    this._index = ARRAY_ID;
  }

  register() {
    this._registerSpecial();
    this._registerMulit("bool", new BoolSerilaze());
    this._registerMulit("int8", new Int8Serilaze());
    this._registerMulit("int16", new Int16Serilaze());
    this._registerMulit("int32", new Int32Serilaze());
    this._registerMulit("int64", new Int64Serilaze());
    this._registerMulit("float32", new Float32Serilaze());
    this._registerMulit("float64", new Float64Serilaze());
    this._registerMulit("string", new StringSerilaze(), 3, false);
    this._registerMulit("date", new DateSerilaze(), 3, false);

    this._registerMulit("list", new ListSerilaze(this._rookie), 7);
    this._registerMulit("map", new MapSerilaze(this._rookie), 10);
    this._registerMulit("set", new MapSerilaze(this._rookie), 5);
  }

  _registerSpecial() {
    this._registerNext({
      id: this._index,
      type: "array",
      valueType: "array",
      mapValueType: "array",
      writeClass: false,
      serializer: new ArraySerilaze(this._rookie),
    });
    // empty
    this._index++;
    // this._registerNext({
    //   id: this._index,
    //   writeClass: false,
    // });
  }

  _registerNext(classInfo: ClassInfo) {
    this._rookie._registerClassInfo(classInfo);
    this._index++;
  }

  _registerMulit(
    dataType: DataType,
    serilaze: Serilaze,
    num: number = 2,
    base: boolean = true
  ) {
    for (let i = 0; i < num; i++) {
      this._registerNext({
        id: this._index,
        type: dataType,
        writeClass: base,
        serializer: serilaze,
      });
    }
  }
}
