//
// Created by labkinetic on 29/12/18.
//

#ifndef CLIENT_ENCODERDECODER_H
#define CLIENT_ENCODERDECODER_H

#include <string>
#include <vector>


class EncoderDecoder {

public:

    EncoderDecoder();

    std::vector<std::string> encode(std::vector<std::string> msg);


};

#endif //CLIENT_ENCODERDECODER_H
