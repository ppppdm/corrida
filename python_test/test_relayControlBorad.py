# -*- coding:gbk -*-
'''
测试 relayControl Board 的功能
'''

import struct
import socket

# Borad ip address
BORAD_IP='192.168.1.110'
BORAD_PORT=6000

# Borad message frame bytes
FRAME_START   = B'\x55' # 1 byte
LOGIC_ADDR    = B''     # 4 bytes
CONTROL_START = B'\xaa' # 1 byte 
FRAME_END     = B'\x16' # 1 byte


# Constant variables
SUPER_ADDRESS = B'\xaa\xaa\xaa\xaa'

CONTROL_CODE_READ_PARAMS = B'\x00'    # read board parameter
CONTROL_CODE_READ_STATUS = B'\x01'    # read light/relay status
CONTROL_CODE_WRIT_PARAMS = B'\x10'    # write board parameter
CONTROL_CODE_WRIT_STATUS = B'\x11'    # write light/relay status
CONTROL_CODE_READ_SWITCH = B'\x22'    # read switch status

CONTROL_CODE_RECV_SWITCH = B'\xa2'    # recive switch status

# Global variables
connected = False



def cs_checksum(byt_arr):
    sum=0
    for byt in byt_arr:
        sum += byt
    return struct.pack('B', sum & 0xff)

def EncodeControlCode(control_code, data_code):
    byt_arr = bytearray()
    data_len = struct.pack('B', len(data_code))
    
    byt_arr += FRAME_START
    byt_arr += SUPER_ADDRESS
    byt_arr += CONTROL_START
    byt_arr += control_code
    byt_arr += data_len
    byt_arr += data_code
    byt_arr += cs_checksum(byt_arr)
    byt_arr += FRAME_END
    
    return byt_arr

if __name__=='__main__':
    import time
    print(__file__,', begin test!')
    control_code = CONTROL_CODE_WRIT_STATUS
    data_code = b'\x05\x00'
    open_2_delay = b'\x01\x00'
    close_2_delay = b'\x11\x00'
    open_cmd = EncodeControlCode(control_code, open_2_delay)
    close_cmd = EncodeControlCode(control_code, close_2_delay)
    
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((BORAD_IP, BORAD_PORT))
    #sock.send(byt_arr)
    print('connected!')
    '''
    i = 0
    while i < 10:
        print('send open_cmd')
        sock.send(open_cmd)
        data = sock.recv(1024)
        time.sleep(5)
        print('send close_cmd')
        sock.send(close_cmd)
        data = sock.recv(1024)
        time.sleep(5)
        i+=1
    '''
    
    '''
    # test CONTROL_CODE_READ_SWITCH weather broad will send initiative
    # board will send switch status initiative
    data = sock.recv(1024)
    print(data)
    
    # 联合测试 发现有时候 没有按开关时也能接收到数据
    # 暂时不清楚板子的发送机制，也可能是首次连接直接去等待数据接收的问题
    print('send open_cmd')
    sock.send(open_cmd)
    data = sock.recv(1024)
    time.sleep(5)
    sock.send(close_cmd)
    data = sock.recv(1024)
    '''
    
    # 首次连接板子后先开关灯，即执行一个发送的命令再去等待接收的数据
    print('send open_cmd')
    sock.send(open_cmd)
    data = sock.recv(1024)
    time.sleep(5)
    sock.send(close_cmd)
    data = sock.recv(1024)
    print(data)
    time.sleep(2)
    data = sock.recv(1024)
    print(data)
    
    
    time.sleep(1)
    print(data)
    sock.close()
    print('socket close')
    ''''''
    
