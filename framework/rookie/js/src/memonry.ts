export type memory = Memory;

const resizeArrayBuffer = (buffer: ArrayBuffer, len: number): ArrayBuffer => {
  const newBuffer = new ArrayBuffer(len);
  const newView = new Uint8Array(newBuffer);
  const oldView = new Uint8Array(buffer);
  newView.set(oldView);
  return newBuffer;
};
class Memory {
  private buffer: ArrayBuffer;
  private dataView: DataView;
  private _offset: number;
  private _readOffset: number;

  constructor(buffer: ArrayBuffer) {
    if (!buffer || !(buffer instanceof ArrayBuffer)) {
      throw new Error("Invalid buffer: must be a valid ArrayBuffer");
    }

    this.buffer = buffer;
    this.dataView = new DataView(buffer);
    this._offset = 0;
    this._readOffset = 0;
  }

  getBuff(): ArrayBuffer {
    return this.buffer.slice(this._readOffset, this._offset);
  }

  // Ensure safe read operations
  private ensureReadable(bytes: number): void {
    if (this._readOffset + bytes > this.dataView.byteLength) {
      throw new Error("Out of bounds read:" + this._offset + " + " + bytes);
    }
  }

  // Ensure safe write operations
  private ensureWritable(bytes: number): void {
    if (this._offset + bytes > this.buffer.byteLength) {
      this._resizeArrayBuffer();
    }
  }

  private _resizeArrayBuffer() {
    this.buffer = resizeArrayBuffer(this.buffer, this.buffer.byteLength * 1.5);
    this.dataView = new DataView(this.buffer);
  }

  readBytes(length: number): ArrayBuffer {
    this.ensureReadable(length);
    const buffer = this.buffer.slice(
      this._readOffset,
      this._readOffset + length
    );
    this._readOffset += length;
    return buffer;
  }

  // Read methods with improved safety
  readInt8(): number {
    this.ensureReadable(1);
    const value = this.dataView.getInt8(this._readOffset);
    this._readOffset += 1;
    return value;
  }

  readInt16(): number {
    this.ensureReadable(2);
    const value = this.dataView.getInt16(this._readOffset);
    this._readOffset += 2;
    return value;
  }

  readInt32(): number {
    return this.decodeZigZag32(this.readRawVarint32());
  }

  readInt64(): number {
    return this.decodeZigZag64(this.readRawVarint64());
  }

  readFloat32(): number {
    this.ensureReadable(4);
    const value = this.dataView.getFloat32(this._readOffset);
    this._readOffset += 4;
    return value;
  }

  readFloat64(): number {
    this.ensureReadable(8);
    const value = this.dataView.getFloat64(this._readOffset);
    this._readOffset += 8;
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
      const currentByte = this.readInt8();
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

  decodeZigZag64(value: number): number {
    return (value >>> 1) ^ -(value & 1);
  }

  writeBytes(encoded: ArrayBuffer): void {
    this.ensureWritable(encoded.byteLength);
    const newView = new DataView(encoded);
    for (let i = 0; i < encoded.byteLength; i++) {
      this.dataView.setInt8(this._offset + i, newView.getInt8(i));
    }
    this._offset += encoded.byteLength;
  }

  // Write methods with improved safety
  writeInt8(value: number): void {
    this.ensureWritable(1);
    this.dataView.setInt8(this._offset, value);
    this._offset += 1;
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
    this.writeRawVarint64(this.encodeZigZag64(value));
  }

  writeRawVarint32(value: number): void {
    this.writeRawVarint64(value);
  }

  writeRawVarint64(value: number): void {
    while ((value & -0x80) != 0) {
      this.writeInt8((value & 0x7f) | 0x80);
      value >>= 7;
    }
    this.writeInt8(value);
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

  close(): void {}
}

export default Memory;
