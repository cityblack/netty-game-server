// const ws = new WebSocket('ws://localhost:8081/ws');

import Rookies from "./rookies.ts";

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
import { Test } from './proto.ts'