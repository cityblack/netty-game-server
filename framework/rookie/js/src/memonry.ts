export type memory = Memory;

class Memory {
  private buffer: ArrayBuffer;
  private dataView: DataView;
  private _offset: number;

  constructor(buffer: ArrayBuffer) {
    if (!buffer || !(buffer instanceof ArrayBuffer)) {
      throw new Error("Invalid buffer: must be a valid ArrayBuffer");
    }

    this.buffer = buffer;
    this.dataView = new DataView(buffer);
    this._offset = 0;
  }

  getBuff(): ArrayBuffer {
    return this.buffer;
  }

  // Ensure safe read operations
  private ensureReadable(bytes: number): void {
    if (this._offset + bytes > this.buffer.byteLength) {
      throw new Error(
        `Cannot read ${bytes} bytes at offset ${this._offset}. Buffer size: ${this.buffer.byteLength}`
      );
    }
  }

  // Ensure safe write operations
  private ensureWritable(bytes: number): void {
    if (this._offset + bytes > this.buffer.byteLength) {
      throw new Error(
        `Cannot write ${bytes} bytes at offset ${this._offset}. Buffer size: ${this.buffer.byteLength}`
      );
    }
  }

  readBytes(length: number): ArrayBuffer {
    const buffer = this.buffer.slice(this._offset, this._offset + length);
    this._offset += length;
    return buffer;
  }

  // Read methods with improved safety
  readInt8(): number {
    this.ensureReadable(1);
    const value = this.dataView.getInt8(this._offset);
    this._offset += 1;
    return value;
  }

  readInt16(): number {
    this.ensureReadable(2);
    const value = this.dataView.getInt16(this._offset);
    this._offset += 2;
    return value;
  }

  readInt32(): number {
    return this.decodeZigZag32(this.readRawVarint32());
  }

  readFloat32(): number {
    this.ensureReadable(4);
    const value = this.dataView.getFloat32(this._offset);
    this._offset += 4;
    return value;
  }

  readFloat64(): number {
    this.ensureReadable(8);
    const value = this.dataView.getFloat64(this._offset);
    this._offset += 8;
    return value;
  }

  readRawVarint32(): number {
    return this.readRawVarint64();
  }

  readRawVarint64(): number {
    // Note: JavaScript has limitations with 64-bit integers
    // Consider using BigInt for full 64-bit support
    let result = 0;
    let shift = 0;

    while (true) {
      this.ensureReadable(1);
      const currentByte = this.dataView.getUint8(this._offset++);
      result |= (currentByte & 0x7f) << shift;

      if ((currentByte & 0x80) === 0) {
        break;
      }

      shift += 7;
      if (shift > 64) {
        throw new Error("Varint is too long");
      }
    }

    return result;
  }

  encodeZigZag32(value: number): number {
    return (value << 1) ^ (value >> 31);
  }

  decodeZigZag32(value: number): number {
    return (value >>> 1) ^ -(value & 1);
  }

  encodeZigZag64(value: number): number {
    return (value << 1) ^ (value >> 63);
  }

  // Write methods with improved safety
  writeInt8(value: number): void {
    this.ensureWritable(1);
    this.dataView.setInt8(this._offset++, value);
  }

  writeInt16(value: number): void {
    this.ensureWritable(2);
    this.dataView.setInt16(this._offset, value);
    this._offset += 2;
  }

  writeInt32(value: number): void {
    this.ensureWritable(4);
    this.writeRawVarint32(this.encodeZigZag32(value));
  }

  writeInt64(value: number): void {
    this.ensureWritable(8);
    // Note: JavaScript has limitations with 64-bit integers
    // Consider using BigInt for full 64-bit support
    this.dataView.setBigInt64(this._offset, BigInt(value));
    this._offset += 8;
  }

  writeRawVarint32(value: number): void {
    while (value >= 0x80) {
      this.writeInt8((value & 0x7f) | 0x80);
      this._offset++;
      value = value >> 7;
    }
    this.writeInt8(value);
    this._offset++;
  }

  writeFloat32(value: number): void {
    this.ensureWritable(4);
    this.dataView.setFloat32(this._offset, value);
    this._offset += 4;
  }

  writeFloat64(value: number): void {
    this.ensureWritable(8);
    this.dataView.setFloat64(this._offset, value);
    this._offset += 8;
  }
}

export default Memory;
