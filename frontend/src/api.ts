import axiosInstance from "./axiosInstance.ts";
import {AxiosResponse} from "axios";

const WS_URL = import.meta.env.VITE_API_BASE_URL.replace("http", "ws");

export function joinRoom(roomName: string) {
  return axiosInstance.post(`/room/join`, roomName);
}

export function createRoom(roomName: string) {
  return axiosInstance.put(`/room`, roomName);
}

export function connectToRoomWebSocket() {
  return new WebSocket(`${WS_URL}/order`);
}

type OrderMap = {
  [key: string]: number;
}

export function getOrders(): Promise<AxiosResponse<OrderMap>> {
  return axiosInstance.get(`/orders`);
}

export function getRoomTotalOrders(): Promise<AxiosResponse<OrderMap>> {
  return axiosInstance.get(`/room/total`);
}