# -*- coding:gbk -*-
# author : pdm
# email : ppppdm@gmail.com
#
# Test using select in socket read in a thread
# And write something in another thread



HOST='localhost'
PORT=6000
SERVER_PORT=6001

import threading

import socket

'''
import sys
def myPrint(ss):
    sys.stdout.flush()
    print(threading.get_ident())
    print(threading.enumerate())
    print(threading.currentThread().getName())
    print(threading.currentThread().ident)
    print(ss)

my_thread = threading.Thread(None, myPrint, 'myThread', ('This is new thread.', ), None)
my_thread.start()
'''

import select
def select_socket_read(sock):
    i = 0
    while i < 5:
        readable , writable , exceptional = select.select([sock], [], [])
        for s in readable:
            data = s.recv(1024)
            print(data)
        i+=1

import time
def socket_write(sock):
    for i in range(5):
        sock.send(bytes('hello world', 'utf8'))
        time.sleep(2)

def server():
    server_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_sock.bind((HOST, SERVER_PORT))
    server_sock.listen(1)
    sock, addr = server_sock.accept()
    i = 0
    while True:
        data = sock.recv(1024)
        data += bytes(str(i), 'utf8')
        sock.send(data)
        i+=1

if __name__=='__main__':
    print(__file__, 'test')
    
    # start server 
    server_thread = threading.Thread(None, server, 'server')
    server_thread.start()
    
    # start client
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((HOST, PORT))
    sock.connect((HOST, SERVER_PORT))
    
    # start client read
    read_thread = threading.Thread(None, select_socket_read, 'read_thread', (sock, ))
    read_thread.start()
    
    # start client write
    write_thread = threading.Thread(None, socket_write, 'write_thread', (sock, ))
    write_thread.start()
    
