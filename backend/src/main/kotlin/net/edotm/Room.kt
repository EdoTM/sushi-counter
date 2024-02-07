package net.edotm

class Room(val name: String, val sessions: MutableSet<UserData> = mutableSetOf())