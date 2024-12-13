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
import { Test, Item } from "./proto.ts";
import Rookie from "./rookies.ts";

const rookie = new Rookie();
rookie.register(Test);
rookie.register(Item);
console.log(rookie);
const test = new Test();
const buff = rookie.serialze(test);
const deserilaze = rookie.deserilaze(2001, buff);
console.log(deserilaze);
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
