import axiosInstance from "./axiosInstance.ts";
import {AxiosResponse} from "axios";

export function joinRoom(roomName: string) {
  return axiosInstance.post(`/room/join`, roomName);
}

export function createRoom(roomName: string) {
  return axiosInstance.put(`/room`, roomName);
}

export function connectToRoomWebSocket() {
  return new WebSocket(`ws://localhost:8080/order`);
}

type OrderMap = {
  [key: string]: number;
}

export function getOrders(): Promise<AxiosResponse<OrderMap>> {
  return axiosInstance.get(`/orders`);
}

export function getRoomTotalOrders() {

}