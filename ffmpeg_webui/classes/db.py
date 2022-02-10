from pymongo import MongoClient

# TODO create database handling class

class DB(MongoClient):
    
    def __init__(self, host: str = "localhost", port: int = 27017):
        super().__init__(host = host, port = port)
        
    def __test_if_initialized(self):
        pass
    
    def __init(self):
        pass