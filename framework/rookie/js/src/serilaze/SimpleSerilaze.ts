import { memory } from "../memonry";
import { ClassInfo } from "../rookies";
import { Serilaze } from "./Serilaze";

export class Int64Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeInt64(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readInt64();
  }
}


export class Int32Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeInt32(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readInt32();
  }
}

export class Int16Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeInt16(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readInt16();
  }
}

export class Int8Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeInt8(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readInt8();
  }
}

export class Float32Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeFloat32(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readFloat32();
  }
}

export class Float64Serilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeFloat64(data);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readFloat64();
  }
}

export class BoolSerilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    mem.writeInt8(data ? 1 : 0);
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    return mem.readInt8() === 1;
  }
}

export class StringSerilaze implements Serilaze {
  serilaze(data: any, _: ClassInfo, mem: memory): void {
    if (!data) {
      mem.writeRawVarint32(0);
    } else {
      const encoded = new TextEncoder().encode(data);
      mem.writeRawVarint32(encoded.length);
      mem.writeBytes(encoded.slice().buffer);
    }
  }
  deserilaze(_: ClassInfo, mem: memory): any {
    const len = mem.readRawVarint32();
    const bytes = mem.readBytes(len);
    return new TextDecoder().decode(bytes);
  }
}
