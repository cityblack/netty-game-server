import Rookie from "../rookies";
import { Field, ID_KEY, Proto } from "../meta";
import Memory from "../memonry";
import Session, { SessionFactory } from "./session";
import { Pipline, DefaultPipline, Processor, Msg } from "./pipline";
import CRC32 from "../util/crc32";
import MD5 from "../util/md5";

const heart_beat_key = "heartBeat";

export interface Client {
	connect(address: string): Promise<Session>;
	getCoder(): Coder;
}

@Proto(-2)
class HeartbeatProtocol {}

@Proto(-1)
class AuthProtocol {
	@Field("string")
	value1: string;
	@Field("int32")
	value2: number;
	constructor(value1: string) {
		this.value1 = MD5.encode(value1);
		this.value2 = CRC32.encodeString(this.value1);
	}
}

const HEART = new HeartbeatProtocol();

export class DefaultClient implements Client {
	private coder: Coder;
	private pipline: Pipline;
	private _requstId: number;
	private _sessions: Record<string, Session> = {};
	private _sessionFactory: SessionFactory;
	private _config: ClientConfig;
	private _auth: AuthProtocol;

	constructor(config: ClientConfig) {
		this._sessionFactory = config.sessionFactory;
		this.coder = config.coder;
		this._requstId = 0;
		this.pipline = new DefaultPipline();
		this.coder.getRookie().register(HeartbeatProtocol);
		this.coder.getRookie().register(AuthProtocol);
		this._config = config;
		this._auth = new AuthProtocol(config.authSlot);
	}

	connect(adress: string): Promise<Session> {
		return new Promise((resolve, reject) => {
			this._sessionFactory.createSession(this, adress, {
				onconnect: (sess) => {
					this._sessions[sess.getId()] = sess;
					this._onconnect(sess);
					resolve(sess);
				},
				onmessage: (sess, data) => this._onmessage(sess, data),
				onerror: (sess, err) => {
					reject(err);
					this._onerror(sess, err);
				},
				onclose: (sess) => this._onclose(sess),
			});
		});
	}

	addProcessor(p: Processor): void {
		this.pipline.addProcessor(p);
	}

	private _onmessage(session: Session, data: any): void {
		blobToArrayBuffer(data).then((buff) => {
			const msg = this.coder.decode(session, buff);
			if (!msg) {
				return;
			}
			this.pipline.fireRead(session, msg);
		});
	}

	private _onconnect(session: Session): void {
		this.send(session, this._auth);
		session.setAttr(
			heart_beat_key,
			setInterval(() => {
				this.send(session, HEART);
			}, this._config.heartBeat)
		);
		this.pipline.fireConnect(session);
	}

	private _onerror(sess: Session, err: any): void {
		this.pipline.fireError(sess, err);
	}

	private _onclose(sess: Session): void {
		const interval = sess.getAttr(heart_beat_key);
		if (interval) {
			clearInterval(interval);
		}
		delete this._sessions[sess.getId()];
		if (sess.onClose) {
			sess.onClose();
		}
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
	getRookie(): Rookie;
}

export class SimpleCoder implements Coder {
	private _rookie: Rookie;
	constructor(rookie: Rookie) {
		this._rookie = rookie;
	}

	/**
	 *  1~4B|2B|1B|4B| ...
	 *  len|id|type|reqId|data
	 * @param _ session
	 * @param data encode data
	 * @returns bytes
	 */
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

	getRookie(): Rookie {
		return this._rookie;
	}
}

const blobToArrayBuffer = async (blob: Blob): Promise<ArrayBuffer> => {
	// For Node.js environment
	if (blob instanceof Buffer) {
		return blob.buffer.slice(
			blob.byteOffset,
			blob.byteOffset + blob.byteLength
		);
	}
	// For browser environment (fallback)
	if (blob.arrayBuffer) {
		return await blob.arrayBuffer();
	}
	// Legacy support
	return new Promise((resolve, reject) => {
		const reader = new FileReader();
		reader.onload = () => resolve(reader.result as ArrayBuffer);
		reader.onerror = reject;
		reader.readAsArrayBuffer(blob);
	});
};

export type ClientConfig = {
	heartBeat: number;
	sessionFactory: SessionFactory;
	coder: Coder;
	authSlot: string;
};
