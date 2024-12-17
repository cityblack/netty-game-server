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
import { RequestData } from "./test/proto.ts";
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
