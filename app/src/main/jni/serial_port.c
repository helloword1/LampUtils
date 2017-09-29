#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include   <stdlib.h>
#include <stdio.h>
#include<pthread.h>

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) //__android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) //__android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) //__android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define TRUE 1
#define FALSE -1
#define BUFSIZE 32
//设置波特率偶人9600
int BaudRate = B9600;

int mTtyfd=-1;
//#define GET_ARRAY_LEN(array,len){len = (sizeof(array) / sizeof(array[0]));}

jchar  humidity =0;
jshort temperature=0;
jchar  pm=0;

jchar  wind_power=0;

jchar  noise = 0;

jchar  electricity;
jchar  lamp_state=0;
jchar  power_mode=0;
jchar  battery_date=0;
jchar  battery_hour=0;
jchar  electric_date=0;
jchar  electric_hour=0;
jshort  lamppost_temp=0;
jchar  fan_state=0;

//供电模式设置返回数据
jchar power_mode_change;
////////////CRC16校验表////////
static const jbyte auchCRCHi[] =
        {
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x00,   0xC1,   0x81,   0x40,   0x01,   0xC0,   0x80,   0x41,
                0x01,   0xC0,   0x80,   0x41,   0x00,   0xC1,   0x81,   0x40
        };

static const jbyte auchCRCLo[] =
        {
                0x00,   0xC0,   0xC1,   0x01,   0xC3,   0x03,   0x02,   0xC2,
                0xC6,   0x06,   0x07,   0xC7,   0x05,   0xC5,   0xC4,   0x04,
                0xCC,   0x0C,   0x0D,   0xCD,   0x0F,   0xCF,   0xCE,   0x0E,
                0x0A,   0xCA,   0xCB,   0x0B,   0xC9,   0x09,   0x08,   0xC8,
                0xD8,   0x18,   0x19,   0xD9,   0x1B,   0xDB,   0xDA,   0x1A,
                0x1E,   0xDE,   0xDF,   0x1F,   0xDD,   0x1D,   0x1C,   0xDC,
                0x14,   0xD4,   0xD5,   0x15,   0xD7,   0x17,   0x16,   0xD6,
                0xD2,   0x12,   0x13,   0xD3,   0x11,   0xD1,   0xD0,   0x10,
                0xF0,   0x30,   0x31,   0xF1,   0x33,   0xF3,   0xF2,   0x32,
                0x36,   0xF6,   0xF7,   0x37,   0xF5,   0x35,   0x34,   0xF4,
                0x3C,   0xFC,   0xFD,   0x3D,   0xFF,   0x3F,   0x3E,   0xFE,
                0xFA,   0x3A,   0x3B,   0xFB,   0x39,   0xF9,   0xF8,   0x38,
                0x28,   0xE8,   0xE9,   0x29,   0xEB,   0x2B,   0x2A,   0xEA,
                0xEE,   0x2E,   0x2F,   0xEF,   0x2D,   0xED,   0xEC,   0x2C,
                0xE4,   0x24,   0x25,   0xE5,   0x27,   0xE7,   0xE6,   0x26,
                0x22,   0xE2,   0xE3,   0x23,   0xE1,   0x21,   0x20,   0xE0,
                0xA0,   0x60,   0x61,   0xA1,   0x63,   0xA3,   0xA2,   0x62,
                0x66,   0xA6,   0xA7,   0x67,   0xA5,   0x65,   0x64,   0xA4,
                0x6C,   0xAC,   0xAD,   0x6D,   0xAF,   0x6F,   0x6E,   0xAE,
                0xAA,   0x6A,   0x6B,   0xAB,   0x69,   0xA9,   0xA8,   0x68,
                0x78,   0xB8,   0xB9,   0x79,   0xBB,   0x7B,   0x7A,   0xBA,
                0xBE,   0x7E,   0x7F,   0xBF,   0x7D,   0xBD,   0xBC,   0x7C,
                0xB4,   0x74,   0x75,   0xB5,   0x77,   0xB7,   0xB6,   0x76,
                0x72,   0xB2,   0xB3,   0x73,   0xB1,   0x71,   0x70,   0xB0,
                0x50,   0x90,   0x91,   0x51,   0x93,   0x53,   0x52,   0x92,
                0x96,   0x56,   0x57,   0x97,   0x55,   0x95,   0x94,   0x54,
                0x9C,   0x5C,   0x5D,   0x9D,   0x5F,   0x9F,   0x9E,   0x5E,
                0x5A,   0x9A,   0x9B,   0x5B,   0x99,   0x59,   0x58,   0x98,
                0x88,   0x48,   0x49,   0x89,   0x4B,   0x8B,   0x8A,   0x4A,
                0x4E,   0x8E,   0x8F,   0x4F,   0x8D,   0x4D,   0x4C,   0x8C,
                0x44,   0x84,   0x85,   0x45,   0x87,   0x47,   0x46,   0x86,
                0x82,   0x42,   0x43,   0x83,   0x41,   0x81,   0x80,   0x40
        };


/////////////////////数据处理/////////////////////
union DataHLCovert
{
    jboolean  dataHL[2];
    jchar covert_code;
}covert_type;

////三合一传感器(PM2.5 温度 湿度)数据结构/////
#pragma pack(1)
typedef struct DataThree
{
    jboolean  address ;
    jboolean  function_code;
    jboolean  effective_bytes;
    jboolean  humidityh;
    jboolean  humidityl;
    jboolean  temperatureh;
    jboolean  temperaturel;
    jchar     retain_one;
    jchar     retain_two;
    jchar     retain_three;
    jboolean  pm1;
    jboolean  pm2;
    jboolean  check_codel;
    jboolean  check_codeh;
} data_three_type;
data_three_type *dataThree;

////风速传感器 数据结构
#pragma pack(1)
typedef  struct DataWindPower
{
    jboolean  address ;
    jboolean  function_code;
    jboolean  data_bytes;
    jchar     retain_two;
    jboolean  wind_powerh;
    jboolean  wind_powerl;
    jboolean  check_codel;
    jboolean  check_codeh;
}data_wind_power;
data_wind_power *dataWind;

////噪声传感器 数据结构
#pragma pack(1)
typedef  struct DataNoise
{
    jboolean  address ;
    jboolean  function_code;
    jboolean  data_bytes;
    jchar     retain_two;
    jboolean  noiseh;
    jboolean  noisel;
    jboolean  check_codel;
    jboolean  check_codeh;
}data_noise;
data_noise *dataNoise;

////照明灯 数据结构
#pragma pack(1)
typedef  struct DataLamp
{
    jboolean  address ;
    jboolean  function_code;
    jboolean  electricity;
    jboolean  lamp_state;
    jboolean  power_mode;
    jboolean  battery_dateh;
    jboolean  battery_datel;
    jboolean  battery_hour;
    jboolean  electric_dateh;
    jboolean  electric_datehl;
    jboolean  electric_hour;
    jbyte     lamppost_temp;
    jboolean  fan_state;
    jboolean  check_codel;
    jboolean  check_codeh;
}data_lamp;
data_lamp *dataLamp;

////照明灯 数据结构
#pragma pack(1)
typedef  struct DataPowerMode
{
    jboolean  address ;
    jboolean  function_code;
    jboolean  power_mode;
    jboolean  check_codel;
    jboolean  check_codeh;
}data_power_mode;
data_power_mode *dataPowerMode;

void converJchar(jboolean value1,jboolean value2)
{
    covert_type.dataHL[1] = value1;
    covert_type.dataHL[0] = value2;
}

static speed_t getBaudrate(jint baudrate)
{
    switch(baudrate) {
        case 0: return B0;
        case 50: return B50;
        case 75: return B75;
        case 110: return B110;
        case 134: return B134;
        case 150: return B150;
        case 200: return B200;
        case 300: return B300;
        case 600: return B600;
        case 1200: return B1200;
        case 1800: return B1800;
        case 2400: return B2400;
        case 4800: return B4800;
        case 9600: return B9600;
        case 19200: return B19200;
        case 38400: return B38400;
        case 57600: return B57600;
        case 115200: return B115200;
        case 230400: return B230400;
        case 460800: return B460800;
        case 500000: return B500000;
        case 576000: return B576000;
        case 921600: return B921600;
        case 1000000: return B1000000;
        case 1152000: return B1152000;
        case 1500000: return B1500000;
        case 2000000: return B2000000;
        case 2500000: return B2500000;
        case 3000000: return B3000000;
        case 3500000: return B3500000;
        case 4000000: return B4000000;
        default: return -1;
    }
}

jchar Count_CRC16(const jboolean data[], jboolean len)
{
    jboolean   uchCRCHi = 0xFF,
            uchCRCLo = 0xFF,
            uIndex;

    while(len--)
    {
        uIndex = uchCRCLo ^ *data++;
        uchCRCLo = uchCRCHi ^ auchCRCHi[uIndex];
        uchCRCHi = auchCRCLo[uIndex];
    }

    return(((jchar)uchCRCHi << 8) | uchCRCLo);
}

int serial_set_parity(int fd, int databits, int stopbits, int parity){


    struct termios options;
    if(tcgetattr(fd, &options)  !=  0)
    {
        LOGE("SetupSerial 1.");
        return(FALSE);
    }
    options.c_cflag &= ~CSIZE;
    switch(databits) /*设置数据位数*/
    {
        case 7:
            options.c_cflag |= CS7;
            break;
        case 8:
            options.c_cflag |= CS8;
            break;
        default:
            LOGE("Unsupported data size");
            return (FALSE);
    }
    switch(parity)
    {
        /* 没有校验 */
        case 'n':
        case 'N':
            options.c_cflag &= ~PARENB;   /* Clear parity enable */
            options.c_iflag &= ~INPCK;     /* Enable parity checking */
            break;
            /* 奇校验 */
        case 'o':
        case 'O':
            options.c_cflag |= (PARODD | PARENB);  /* 设置为奇效验*/
            options.c_iflag |= INPCK;             /* Disnable parity checking */
            break;
            /* 偶校验 */
        case 'e':
        case 'E':
            options.c_cflag |= PARENB;     /* Enable parity */
            options.c_cflag &= ~PARODD;   /* 转换为偶效验*/
            options.c_iflag |= INPCK;       /* Disnable parity checking */
            break;
        case 'S':
        case 's':  /*as no parity*/
            options.c_cflag &= ~PARENB;
            options.c_cflag &= ~CSTOPB;
            break;
        default:
            LOGE("Unsupported parity.");
            return (FALSE);
    }
    /* 设置停止位*/
    switch(stopbits)
    {
        case 1:
            options.c_cflag &= ~CSTOPB;
            break;
        case 2:
            options.c_cflag |= CSTOPB;
            break;
        default:
            LOGE("Unsupported stop bits");
            return (FALSE);
    }
    /* Set input parity option */
    if(parity != 'n')
        options.c_iflag |= INPCK;
    options.c_cc[VTIME] = 150; // 15 seconds
    options.c_cc[VMIN] = 0;

    tcflush(fd,TCIFLUSH); /* Update the options and do it NOW */
    if(tcsetattr(fd,TCSANOW,&options) != 0)
    {
        LOGE("SetupSerial 3");
        return (FALSE);
    }
    return (TRUE);
}

void setTermios(struct termios * pNewtio, int uBaudRate)
{

    bzero(pNewtio, sizeof(struct termios));
    //8N1
    pNewtio->c_cflag = uBaudRate | CS8 | CREAD | CLOCAL;
    pNewtio->c_iflag = IGNPAR;
    pNewtio->c_oflag = 0;
    pNewtio->c_lflag = 0; //non ICANON
    pNewtio->c_cc[VINTR] = 0;
    pNewtio->c_cc[VQUIT] = 0;
    pNewtio->c_cc[VERASE] = 0;
    pNewtio->c_cc[VKILL] = 0;
    pNewtio->c_cc[VEOF] = 4;
    pNewtio->c_cc[VTIME] = 5;
    pNewtio->c_cc[VMIN] = 0;
    pNewtio->c_cc[VSWTC] = 0;
    pNewtio->c_cc[VSTART] = 0;
    pNewtio->c_cc[VSTOP] = 0;
    pNewtio->c_cc[VSUSP] = 0;
    pNewtio->c_cc[VEOL] = 0;
    pNewtio->c_cc[VREPRINT] = 0;
    pNewtio->c_cc[VDISCARD] = 0;
    pNewtio->c_cc[VWERASE] = 0;
    pNewtio->c_cc[VLNEXT] = 0;
    pNewtio->c_cc[VEOL2] = 0;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_open_1dev(JNIEnv *env,
                                                                        jobject instance,
                                                                        jstring dev_,
                                                                        jint baudrate) {

    const char *Dev_utf = (*env)->GetStringUTFChars(env, dev_, JNI_FALSE);
    int fd = open(Dev_utf, O_RDWR);
    (*env)->ReleaseStringUTFChars(env, dev_, Dev_utf);
    if (-1 == fd)
    { /*设置数据位数*/
        return -1;
    }
    BaudRate = getBaudrate(baudrate);
    mTtyfd =fd;
    return fd;
}


JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_send_1data(JNIEnv *env,
                                                                         jobject instance, jint fd,
                                                                         jcharArray data_,
                                                                         jint dataLen) {

    jchar *data = (*env)->GetCharArrayElements(env, data_, NULL);
    //TODO
    int i;
    int len =(int)dataLen;
    char command[8];
    for(i=0;i<len;i++){
        //java中char[]数组元素是char类型占2个字节和jni中的jchar相同，要转换成一个字节的char类型所以要去掉一个字节，方法就是将每个元素&oxff
        command[i] = (char)data[i]&0xff;
    }
    int flag = write(fd,command,len);
    (*env)->ReleaseCharArrayElements(env, data_, data, 0);
    return flag;
}

JNIEXPORT void JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_receive_1data(JNIEnv *env,
                                                                            jobject instance,
                                                                            jint fd, jint len) {
    // TODO
    int read_data_len;
    unsigned  char buff[BUFSIZE];
    struct termios oldtio, newtio;
    struct timeval tv;
    fd_set rfds;

    bzero(buff, BUFSIZE);
    tcgetattr(fd, &oldtio);
    setTermios(&newtio, BaudRate);
    tcflush(fd, TCIFLUSH);
    tcsetattr(fd, TCSANOW, &newtio);

    if (serial_set_parity(fd,8,1,'N') == FALSE)
    {
        LOGE("Set Parity Error\n");
        close(fd);
        return;
    }
    tv.tv_sec =1;//设定超时秒数
    tv.tv_usec=0;//设定超时毫秒数

    jclass cls2 = (*env)->GetObjectClass(env, instance);
    jmethodID mid = (*env)->GetMethodID(env, cls2, "callback", "(I)V");

    if (mid == NULL)
    {
        LOGE("method not found!");
        close(fd);
        return; /* method not found */
    }
    int fdState;
    while(TRUE) {
        FD_ZERO(&rfds);//清空集合
        FD_SET(fd, &rfds);///* 把要检测的句柄fd加入到集合里 */
        /* 这说明select函数出错 -1*/
        /* 说明在我们设定的时间值1秒加0毫秒的时间内，mTty的状态没有发生变化 =0 */
        /* 检测我们上面设置到集合read里的句柄是否有可读信息 >0*/
        fdState = select(1 + fd, &rfds, &rfds, NULL, &tv);
        if (fdState <= 0)
            break;
        if (fdState > 0) {
            if (FD_ISSET(fd, &rfds)) {/* 先判断一下mTty这外被监视的句柄是否真的变成可读的了 */
                read_data_len = read(fd, buff, sizeof(buff));
                if (read_data_len >= 0) {

                    unsigned char dataBuff[read_data_len];
                    bzero(dataBuff, read_data_len);
                    jint isreceive = 9;
                    if (memcpy(dataBuff, buff, read_data_len) != NULL) {

                        //温湿度PM2.5数据处理
                        if (dataBuff[0] == 0x01 && dataBuff[1] == 0x04 && dataBuff[2] == 0x0c &&
                            read_data_len == 17) {
                            isreceive = 1;
                            dataThree = dataBuff;
                            converJchar(dataThree->humidityh, dataThree->humidityl);
                            humidity = covert_type.covert_code;
                            converJchar(dataThree->temperatureh, dataThree->temperaturel);
                            temperature = covert_type.covert_code;
                            converJchar(dataThree->pm1, dataThree->pm2);
                            pm = covert_type.covert_code;
                        }
                        //噪声数据处理
                        if (dataBuff[0] == 0x0A && dataBuff[1] == 0x04 && dataBuff[2] == 0x04 &&
                            read_data_len == 9) {
                            isreceive = 2;
                            dataNoise = dataBuff;
                            converJchar(dataNoise->noiseh, dataNoise->noisel);
                            noise = covert_type.covert_code;
                        }
                        //风力数据处理
                        if (dataBuff[0] == 0x02 && dataBuff[1] == 0x03 && dataBuff[2] == 0x04 &&
                            read_data_len == 9) {
                            isreceive = 3;
                            dataWind = dataBuff;
                            converJchar(dataWind->wind_powerh, dataWind->wind_powerl);
                            wind_power = covert_type.covert_code;
                        }
                        //照明灯数据处理
                        if (dataBuff[0] == 0x7e && dataBuff[1] == 0x04  &&
                            dataBuff[14] == 0x7F && read_data_len == 15) {
                            isreceive = 4;
                            dataLamp = dataBuff;
                            electricity = dataLamp->electricity;
                            lamp_state = dataLamp->lamp_state;
                            power_mode = dataLamp->power_mode;
                            converJchar(dataLamp->battery_dateh, dataLamp->battery_datel);
                            battery_date = covert_type.covert_code;
                            battery_hour = dataLamp->battery_hour;
                            converJchar(dataLamp->electric_dateh, dataLamp->electric_datehl);
                            electric_date = covert_type.covert_code;
                            electric_hour = dataLamp->electric_hour;
                            lamppost_temp = dataLamp->lamppost_temp;
                            fan_state = dataLamp->fan_state;
                        }
                        if(dataBuff[0]==0x7d&&dataBuff[1]==0x03&&dataBuff[3]==0x70&&dataBuff[4]==0x7f&&read_data_len==5)
                        {
                            isreceive=5;
                            dataPowerMode=dataBuff;
                            power_mode_change=dataPowerMode->power_mode;
                        }
//                    (*env)->SetByteArrayRegion(env, bytearray, 0, read_data_len, dataBuff);
//                    (*env)->SetObjectField(env, instance, fid, bytearray);
                        (*env)->CallVoidMethod(env, instance, mid, isreceive);
                    }
                    break;
                }
            }
            tcflush(fd, TCIOFLUSH);
        }
    }
    tcflush(fd, TCIOFLUSH);
    tcsetattr(fd, TCSANOW, &oldtio);
    return;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_close_1dev(JNIEnv *env,
                                                                         jobject instance,
                                                                         jint fd) {
    // TODO
    close(fd);
    return (jint)1;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getHumidity(JNIEnv *env,
                                                                          jobject instance) {
    // TODO
    return humidity;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getWindPower(JNIEnv *env,
                                                                           jobject instance) {

    // TODO
    return wind_power;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getNoise(JNIEnv *env,
                                                                       jobject instance) {

    // TODO
    return noise;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getPm(JNIEnv *env, jobject instance) {

    // TODO
    return pm;
}


JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getTemperature(JNIEnv *env,
                                                                             jobject instance) {
    // TODO
    return temperature;
}


JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getElectricity(JNIEnv *env,
                                                                             jobject instance) {

    // TODO
    return electricity;
}
JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getLampState(JNIEnv *env,
                                                                           jobject instance) {

    // TODO
    return lamp_state;

}

JNIEXPORT jint JNICALL
        Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getPowerMode(JNIEnv *env,
                                                                                   jobject instance) {

    // TODO
    return power_mode;
}


    JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getBatteryDate(JNIEnv *env,
                                                                             jobject instance) {

    // TODO
    return battery_date;

}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getBatteryHour(JNIEnv *env,
                                                                             jobject instance) {

    // TODO
    return battery_hour;

}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getElectricDate(JNIEnv *env,
                                                                              jobject instance) {

    // TODO
    return electric_date;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getElectricHour(JNIEnv *env,
                                                                              jobject instance) {

    // TODO
    return electric_hour;

}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getLamppostTemp(JNIEnv *env,
                                                                              jobject instance) {

    // TODO
    return lamppost_temp;

}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getFanState(JNIEnv *env,
                                                                          jobject instance) {

    // TODO
    return fan_state;
}

JNIEXPORT jint JNICALL
Java_com_example_jjt_1ssd_streetlamp_SerialPortLib_SerialPort_getPowerModeChange(JNIEnv *env,
                                                                                 jobject instance) {

    // TODO
    return power_mode_change;

}