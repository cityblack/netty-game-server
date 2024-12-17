import { DefaultClient } from "./client";

export interface SessionFactory {
  createSession(
    client: DefaultClient,
    address: string,
    onconnect: (sess: Session) => void,
    onmessage: (sess: Session, data: any) => void,
    onerror: (sess: Session, err: Error) => void
  ): void;
}

export default interface Session {
  send(data: any): void;
  write(msg: ArrayBuffer): void;
  close(): void;
  getId(): string;
}

export class WebSocketSessionFactory implements SessionFactory {
  createSession(
    client: DefaultClient,
    address: string,
    onconnect: (sess: Session) => void,
    onmessage: (sess: Session, data: any) => void,
    onerror: (sess: Session, err: any) => void
  ): void {
    const sess = new WebSocketSession(client);
    sess._bind(address, onconnect, onmessage, onerror);
  }
}

export class WebSocketSession implements Session {
  client: DefaultClient;
  ws: WebSocket | null = null;
  id: string;
  constructor(client: DefaultClient) {
    this.client = client;
    this.id = uuid();
  }
  _bind(
    address: string,
    onconnect: (sess: Session) => void,
    onmessage: (sess: Session, data: any) => void,
    onerror: (sess: Session, err: any) => void
  ) {
    this.ws = new WebSocket(address);
    this.ws.onopen = () => {
      onconnect(this);
    };
    this.ws.onmessage = (event) => {
      onmessage(this, event.data);
    };
    this.ws.onerror = (error) => {
      onerror(this, error);
    };
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
}

const uuid = (): string => {
  return Math.random().toString(36).slice(2);
};
