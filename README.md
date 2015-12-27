Android Serial Helper
=====================

Android serial helper inspired with PL2303HXD, send camera image encoded with Luby Transform Code(LTCode) through the rs232 port.

Preview
=======
![Alt text](https://github.com/smileboywtu/AndroidSerialHelper/blob/master/screenshots/main.jpg)
![Alt text](https://github.com/smileboywtu/AndroidSerialHelper/blob/master/screenshots/serial.jpg)

Introduction
============

this application was developed with eclipse android tool and contains everything in it.
with this application you can:

+ use this application as a general serial port
+ send camera image encoded with lt code use serial


Hardware Require
================

+ Android device with OTG, better if the OTG can supply enough power
+ PL2303HXD usb to uart cable
+ Android device with camera available if you want to send lt code

Luby Code Information
=====================

when use the lt code, the package is contructed with 100 bytes per. and if you want to
decode the  image out from the received bytes, you need to read [LubyTransformCode][0] for
more information.

[0]: https://github.com/smileboywtu/LubyTransformCode "luby transform code project"
