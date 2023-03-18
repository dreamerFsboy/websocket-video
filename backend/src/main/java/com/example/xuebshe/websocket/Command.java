package com.example.xuebshe.websocket;

public interface Command {
    int CREATEROOM = 1;
    int JOINASK = 2;
    int JOINUNEXIST = 3;
    int JOINAGREE = 4;
    int JOINREFUSE = 5;
    int CHANGESOURCE = 6;
    int GETINFO = 7;
    int KICK = 8;
    int VS = 9;
    int AS = 10;
    int MESSAGE= 11;
}
