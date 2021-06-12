APDU
APDU commands pairs examples

In the context of smart cards, an application protocol data unit (APDU) is the communication unit between a smart card reader and a smart card. The structure of the APDU is defined by ISO/IEC 7816-4 Organization, security and commands for interchange.

Command-response pairs
----------------------
A command-response pair, namely a command APDU followed by a response APDU in the opposite direction (see ISO/IEC 7816-3). There shall be no interleaving of command-response pairs across the interface, i.e., the response APDU shall be received before initiating another command-response pair.


| FIELD  | DESCRIPTION        | NUMBER OF BYTES |
|:-------|:-------------------|:---------------:|
|CMDHead |CLA:Class Byte      |1                |
|CMDHead |INS:Instruction Byte|1                |
|CMDHead |P1/P2:Parameter Byte|2                |
|LcField |Lenght of DataComand|0, 1 o 3         |
|CDField |Command Data if Lc>0|Nc               |
|LeField |Lenght of Data      |0, 1, 2 o 3      |

| FIELD  | DESCRIPTION        | NUMBER OF BYTES |
|:-------|:-------------------|:---------------:|
|RDataFd |SW1/SW2:Status Bytes|2                |

If the process is aborted, then the card may become unresponsive. However if a response APDU occurs, then the response data field shall be absent and SW1-SW2 shall indicate an error.
P1-P2 indicates controls and options for processing the command. A parameter byte set to '00' generally provides no further qualification. There is no other general convention for encoding the parameter bytes.

General conventions are specified hereafter for encoding the class byte denoted CLA, the instruction byte denoted INS and the status bytes denoted SW1-SW2). In those bytes, the RFU bits shall be set to 0 unless otherwise specified. To see the specific commands and error responses see the ISO / IEC 7816-4 standard.

Examples
--------

Get Version of SAM
|   CLA   |   INS   |    P1    |   P2   |   Lc   |  
|:-------:|:-------:|:--------:|:------:|:------:|
|80h      |60h      |Data      |Data    |Lenght  |     
|80h      |60h      |00h       |00h     |00h     |


Select DESFire App with AID = 0x484000
|   CLA   |   INS   |   P1   |   P2   |   Lc   | DataB1 | DataB2 | DataB3 | DataB4 |  
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|
|90h      |5Ah      |00h     |00h     |03h     |AID_0   |AID_1   |AID_2   |Data    |
|90h      |5Ah      |00h     |00h     |03h     |48h     |40h     |00h     |00h     |


Get App File IDs
|   CLA   |   INS   |   P1   |   P2   |   Le   |
|:-------:|:-------:|:------:|:------:|:------:|
|90h      |6Fh      |00h     |00h     |00h     |
|90h      |6Fh      |00h     |00h     |00h     |


Get challenge for authentication from DESFire card
|   CLA   |   INS   |   P1   |   P2   |   Le   | DataB1 | DataB2 |
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|
|90h      |AAh      |00h     |00h     |N° Bytes| Key N° |  Data  |
|90h      |AAh      |00h     |00h     |01h     |01h     |00h     |


Send challenge to SAM
|   CLA   |   INS   |   P1   |   P2   |   Lc   | DataB1 | DataB2 | DataB3 to DataB18 | DataB19| 
|:-------:|:-------:|:------:|:------:|:------:|:------:|:------:|:-----------------:|:------:|
|80h      |0Ah      |00h     |00h     |12h     |04h     |00h     |Challenge from Card|00h     |


RESPONSES:
9000h -> OK
For error responses look up in https://www.eftlab.com/knowledge-base/complete-list-of-apdu-responses/

