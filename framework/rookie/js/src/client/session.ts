import { DefaultClient } from "./client";

export interface SessionFactory {
  createSession(
    client: DefaultClient,
    address: string,
    event: SessionEvent
  ): void;
}

export interface SessionEvent {
  onconnect: (sess: Session) => void;
  onmessage: (sess: Session, data: any) => void;
  onerror: (sess: Session, err: any) => void;
  onclose: (sess: Session) => void;
}

export default interface Session {
  send(data: any): void;
  write(msg: ArrayBuffer): void;
  close(): void;
  onClose?(): void;
  getId(): string;
  getAttr(key: string): any;
  setAttr(key: string, value: any): void;
}

export class WebSocketSessionFactory implements SessionFactory {
  createSession(
    client: DefaultClient,
    address: string,
    event: SessionEvent
  ): void {
    const sess = new WebSocketSession(client);
    sess._bind(address, event);
  }
}

export class WebSocketSession implements Session {
  client: DefaultClient;
  ws: WebSocket | null = null;
  id: string;
  attr: Record<string, any> = {};
  constructor(client: DefaultClient) {
    this.client = client;
    this.id = uuid();
  }
  _bind(address: string, event: SessionEvent) {
    this.ws = new WebSocket(address);
    this.ws.onopen = () => event.onconnect(this);
    this.ws.onmessage = (e) => event.onmessage(this, e.data);
    this.ws.onerror = (error) => event.onerror(this, error);
    this.ws.onclose = () => event.onclose(this);
  }

  send(data: any): void {
    this.client.send(this, data);
  }

  write(msg: ArrayBuffer): void {
    this.ws?.send(msg);
  }

  close(): void {
    this.ws?.close();
  }

  getId(): string {
    return this.id;
  }
  setAttr(key: string, value: any): void {
    this.attr[key] = value;
  }
  getAttr(key: string): any {
    return this.attr[key];
  }
}

const uuid = (): string => {
  return Math.random().toString(36).slice(2);
};
