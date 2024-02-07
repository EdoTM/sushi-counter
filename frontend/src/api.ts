import axiosInstance from "./axiosInstance.ts";

export function joinRoom(roomName: string) {
  return axiosInstance.post(`/room/join`, roomName);
}

export function createRoom(roomName: string) {
  return axiosInstance.put(`/room`, roomName);
}

export function connectToRoomWebSocket() {
  return new WebSocket(`ws://localhost:8080/order`);
}

export function getRoomTotalOrders() {

}