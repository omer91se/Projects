//
// Created by Omer Segal and Anat Bar-Sinai on 29/12/18.
//

#ifndef CLIENT_TASK_H
#define CLIENT_TASK_H

#include <iostream>
#include <mutex>
#include <thread>

#include "ConnectionHandler.h"

using namespace std;

class Task{
private:
    ConnectionHandler *_handler;

    bool terminate;

    bool didLogin;

    bool should_terminate();

    void shortToBytes(short num, char* bytesArr);

public:
    Task (ConnectionHandler *handler);

    void run();

    void terminate_task();

    void login();



};

#endif //CLIENT_TASK_H
