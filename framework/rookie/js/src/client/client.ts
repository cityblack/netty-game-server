import Rookie from "../rookies";
import { ID_KEY } from "../meta";
import Memory from "../memonry";
import Session, { SessionFactory } from "./session";

export interface Client {
  connect(address: string): Promise<Session>;
  getCoder(): Coder;
}

export type Msg = {
  sess: Session;
  id: number;
  type: number;
  requestId: number;
  value: any;
};

export interface Processor {
  onConnect(sess: Session): void;
  onMessage(sess: Session, msg: Msg): void;
  onError(sess: Session, err: any): void;
}

export class DefaultClient implements Client {
  private coder: Coder;
  private processor: Processor[];
  private _requstId: number;
  private _sessions: Record<string, Session> = {};
  private _sessionFactory: SessionFactory;

  constructor(coder: Coder, sessionFactory: SessionFactory) {
    this.processor = [];
    this._sessionFactory = sessionFactory;
    this.coder = coder;
    this._requstId = 0;
  }

  connect(adress: string): Promise<Session> {
    return new Promise((resolve, reject) => {
      this._sessionFactory.createSession(
        this,
        adress,
        (sess) => {
          this._sessions[sess.getId()] = sess;
          resolve(sess);
          this._onconnect(sess);
        },
        (sess, data) => {
          this._onmessage(sess, data);
        },
        (sess, err) => {
          reject(err);
          this._onerror(sess, err);
        }
      );
    });
  }

  addProcessor(p: Processor): void {
    this.processor.push(p);
  }

  private _onmessage(session: Session, data: any): void {
    blobToArrayBuffer(data).then((buff) => {
      const msg = this.coder.decode(session, buff);
      if (!msg) {
        return;
      }
      this.processor.forEach((p) => p?.onMessage(session, msg));
    });
  }

  private _onconnect(session: Session): void {
    this.processor.forEach((p) => p?.onConnect(session));
  }

  private _onerror(sess: Session, err: any): void {
    this.processor.forEach((p) => p?.onError(sess, err));
  }

  send(session: Session, data: any): void {
    if (!data[ID_KEY]) {
      throw new Error(`${data} not have ${ID_KEY}.`);
    }
    const msg: Msg = {
      sess: session,
      id: data[ID_KEY],
      type: 2,
      requestId: this._requstId++,
      value: data,
    };
    const buff = this.coder.encode(session, msg);
    session.write(buff);
  }

  getCoder(): Coder {
    return this.coder;
  }
}

export interface Coder {
  encode(sess: Session, data: Msg): ArrayBuffer;
  decode(sess: Session, data: ArrayBuffer): Msg | undefined;
}

export class SimpleCoder implements Coder {
  private _rookie: Rookie;
  constructor(rookie: Rookie) {
    this._rookie = rookie;
  }

  encode(_: Session, data: Msg): ArrayBuffer {
    const mem = new Memory(new ArrayBuffer(512));
    // mem.writeRawVarint32(data.value.length);
    mem.writeInt16(data.id);
    mem.writeInt8(data.type);
    mem.writeInt32(data.requestId, false);
    this._rookie.serialze(data.value, mem);
    const buff = mem.getBuff();
    const wrapper = new Memory(new ArrayBuffer(buff.byteLength + 4));
    wrapper.writeRawVarint32(buff.byteLength);
    wrapper.writeBytes(buff);

    return wrapper.getBuff();
  }

  decode(sess: Session, data: ArrayBuffer): Msg | undefined {
    const mem = new Memory(data);
    const len = mem.readRawVarint32();
    if (len < 0) {
      return;
    }
    const id = mem.readInt16();
    const type = mem.readInt8();
    const requestId = mem.readInt32(false);
    const value = this._rookie.deserilaze(mem);
    return {
      sess,
      id,
      type,
      requestId,
      value,
    };
  }
}

const blobToArrayBuffer = (blob: Blob): Promise<ArrayBuffer> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      if (reader.result instanceof ArrayBuffer) {
        resolve(reader.result);
      } else {
        reject(new Error("Failed to convert Blob to ArrayBuffer"));
      }
    };
    reader.onerror = () => reject(reader.error);
    reader.readAsArrayBuffer(blob);
  });
};
