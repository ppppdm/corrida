# -*- coding:gbk -*-
'''
测试 relayControl Board 的功能
'''

import struct

# Borad ip address
BORAD_IP='192.168.1.110'
BORAD_PORT='6000'

# Borad message frame bytes
FRAME_START   = B'\x55' # 1 byte
LOGIC_ADDR    = B''     # 4 bytes
CONTROL_START = B'\xaa' # 1 byte 
FRAME_END     = B'\x16' # 1 byte


# Constant variables
SUPER_ADDRESS = B'\xaa\xaa\xaa\xaa'

# Global variables
connected = False



def cs_checksum(byt_arr):
    sum=0
    for byt in byt_arr:
        sum += byt
    return struct.pack('B', sum & 0xff)

def EncodeControlCode(control_code, data_len, data_code):
    byt_arr = bytearray()
    
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
    print(__file__,', begin test!')
    
    byt_arr = EncodeControlCode(b'\x22', b'\x01', b'\x0f')
    print(byt_arr)

