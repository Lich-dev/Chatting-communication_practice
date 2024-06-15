About:
this project is a collection of different communication methods for the purpose of learning coding

Contents:
ChatClient - interface for all following chat client implementations
csvChatClient - chat client (cc) which uses a locally stored csv file as a medium for communication
DBChatClient - cc using a locally stored Apache Derby database for communication
directChatClient - cc using sockets and internet communication for sharing messages between live clients
FileChatClient - cc which stores messages in a local txt file
inMemoryChatClient - chat clients are linked together locally and communicate using memory
jsonChatClient - cc using a local Json file for communication
xmlChatClient - cc using a local Xml file for communication

MainFrame - user interface which is used as front-end for most chat client implementations

DirectServer - basic socket communication, mainly used for debugging and testing
HTTPServer - communication using HTTP protocol methods - a small test, HTTP communication framework recommended