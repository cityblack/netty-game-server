// const ws = new WebSocket('ws://localhost:8081/ws');

// ws.onopen = function() {
//     console.log('Connected to WebSocket server');
//     sendMessage('hello server');
// };

// ws.onmessage = function(event) {
//     const message = event.data;
//     console.log(event)
//     console.log('Received message:', message);
//     // sendMessage('hello server');
// };

// ws.onerror = function(error) {
//     console.error('WebSocket error:', error);
// };

// ws.onclose = function() {
//     console.log('Disconnected from WebSocket server');
// };

// // Function to send messages to the server
// function sendMessage(message) {
//     if (ws.readyState === WebSocket.OPEN) {
//         ws.send(message);
//     } else {
//         console.error('WebSocket is not connected');
//     }
// }
import { RequestData } from "./proto.ts";
import Rookie from "./rookies.ts";
import Memory from "./memonry.ts";
import { DefaultClient, SimpleCoder } from "./client/client";
import { WebSocketSessionFactory } from "./client/session.ts";

const rookie = new Rookie();
rookie.register(RequestData);
console.log(rookie);
const coder = new SimpleCoder(rookie);
const factory = new WebSocketSessionFactory();
const client = new DefaultClient(coder, factory);
client.addProcessor({
  onConnect(sess) {
    console.log("connect:", sess);
  },
  onMessage(sess, msg) {
    console.log(sess, msg);
  },
  onError(err) {
    console.error(err);
  },
});
client.connect("ws://localhost:8081/ws").then((sess) => {
  const request = new RequestData();
  request.id = 100;
  request.name = "hello";
  request.age = 10;
  request.price = 10.1;
  request.tail = 10.1;
  request.list = [1, 2, 3];
  request.set = [1, 2, 3];
  request.map = {
    1: 1,
    2: 2,
    3: 3,
  };
  sess.send(request);
});
// const mem = new Memory(new ArrayBuffer(1024));
// const test = new Test();
// rookie.serialze(test, mem);
// const deserilaze = rookie.deserilaze(2001, mem);
// console.log(deserilaze);
// import Memory from "./memonry.ts";

// const mem = new Memory(new ArrayBuffer(1024));
// mem.writeInt8(-8);
// mem.writeInt16(22);
// mem.writeInt32(311);
// mem.writeInt32(-311);
// mem.writeInt64(41111);
// mem.writeInt64(-41111);
// mem.writeFloat32(50.1);
// mem.writeFloat64(6.2);
// mem.writeRawVarint32(155);
// const str = new TextEncoder().encode("hello");
// mem.writeRawVarint32(str.length);
// mem.writeBytes(str.buffer);

// console.log(mem.readInt8());
// console.log(mem.readInt16());
// console.log(mem.readInt32());
// console.log(mem.readInt32());
// console.log(mem.readInt64());
// console.log(mem.readInt64());
// console.log(mem.readFloat32());
// console.log(mem.readFloat64());
// console.log(mem.readRawVarint32());
// const len = mem.readRawVarint32();
// console.log(new TextDecoder().decode(mem.readBytes(len)));
