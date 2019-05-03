BOOST = -lboost_system -lpthread

# All Targets
all: BGSclient

# Tool invocations
BGSclient: bin/ConnectionHandler.o bin/client.o bin/EncoderDecoder.o bin/Task.o $(BOOST)
	@echo 'Building target: BGSclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGSclient bin/client.o bin/ConnectionHandler.o bin/EncoderDecoder.o bin/Task.o $(BOOST)
	@echo 'Finished building target: BGSclient'
	@echo ' '


bin/client.o: src/client.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/client.o src/client.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/EncoderDecoder.o: src/EncoderDecoder.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/Task.o: src/Task.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Task.o src/Task.cpp


#Clean the build directory
clean:
	rm -f bin/*


