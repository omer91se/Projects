#include <iostream>
#include <mutex>
#include <thread>
#include <sstream>

#include "../include/Task.h"
#include "../include/ConnectionHandler.h"
#include "../include/EncoderDecoder.h"

short bytesToShort(char byte[2]);

using namespace std;
int main(int argc, char **argv) {

    if(argc != 3){
        return 1;
    }
    std::string ip = argv[1];
    short port = std::stoi(argv[2]);

    EncoderDecoder encdec;
    ConnectionHandler handler(ip,port);
    if(!handler.connect()){
        return 1;
    }

    //start a thread to handle the user keyboard input:
    Task cin_handler(&handler);
    std::thread th1(&Task::run, &cin_handler);

    //handle the server input:
    bool to_terminate = false;
    string to_print;
    string to_dec;

    while(!to_terminate) {
        char op_byte[2];

        if (handler.getBytes(op_byte, 2)) {
            short op = bytesToShort(op_byte);
            string to_print = "";

            if (op == 9) {
                to_print = "NOTIFICATION ";

                //TODO
                char pm_public_byte[1];
                handler.getBytes(pm_public_byte, 1);


                if (pm_public_byte[0] == '0') {
                    to_print = to_print + "PM ";
                } else {
                    to_print = to_print + "Public ";
                }

                string posting_user;
                handler.getLine(posting_user);
                posting_user = posting_user.substr(0, posting_user.size()-1);
                to_print = to_print + posting_user + " ";

                string content;
                handler.getLine(content);
                content = content.substr(0, content.size()-1);
                to_print = to_print + content;
            }

            if (op == 10) {
                to_print = "ACK ";

                char message_op_byte[2];
                handler.getBytes(message_op_byte, 2);
                short message_op = bytesToShort(message_op_byte);;
                to_print = to_print + to_string(message_op);

                if(message_op == 4 || message_op == 7){
                    char num_users_byte[2];
                    handler.getBytes(num_users_byte, 2);
                    short num_users = bytesToShort(num_users_byte);
                    to_print = to_print + " " + to_string(num_users);

                    for(int i = 0 ; i < num_users ; ++i){
                        string username;
                        handler.getLine(username);
                        username = username.substr(0, username.size()-1);
                        to_print = to_print + " " + username;
                    }


                }

                if(message_op == 8){
                    //num of post:
                    char num_post_byte[2];
                    handler.getBytes(num_post_byte, 2);
                    short num_post = bytesToShort(num_post_byte);
                    to_print = to_print + " " + to_string(num_post);

                    //num of followers:
                    char num_followers_byte[2];
                    handler.getBytes(num_followers_byte, 2);
                    short num_followers = bytesToShort(num_followers_byte);
                    to_print =  to_print + " " + to_string(num_followers);

                    //num of following:
                    char num_following_byte[2];
                    handler.getBytes(num_following_byte, 2);
                    short num_following = bytesToShort(num_following_byte);
                    to_print =  to_print + " " + to_string(num_following);
                }

            }

            if (op == 11) {
                to_print = "ERROR ";
                char error_op_byte[2];
                handler.getBytes(error_op_byte, 2);
                short error_op = bytesToShort(error_op_byte);;
                to_print = to_print + to_string(error_op);

            }


            cout << to_print << endl;

            if(to_print == "ACK 2")
                cin_handler.login();

            to_terminate = (to_print == "ACK 3");
            if(to_terminate)
                cin_handler.terminate_task();
        }
    }
    th1.join();
}

short bytesToShort(char* bytesArr) {
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}
