##Package
`<header>[<type><length><value>]`  
The body may contain more than one data block called "TLV".

##General communications
The password is transmitted on each configuration change request.

##Sample output for a login

	===NEW PACKET ON PORT 29808 FROM /192.168.0.10
	Decrypted data:
	 Version:1
	 OPCODE:3
	 MAC:F4F26DA4F8DE
	 HOST MAC:08002738F48B
	 SEQUENCE NUMBER:102
	 ERROR CODE (7 or 8 are somehow bad):0
	 LENGTH:67
	 FRAGMENT OFFSET:0
	 TOKEN ID:22238
	 CHECKSUM (apparently not implemented):0
	  BODY:
	 -START TLV-
	  TYPE:512 (Username)
	  LENGTH:6
	  BODY:61646D696E00 (String: admin)
	 -START TLV-
	  TYPE:514 (Password)
	  LENGTH:17
	  BODY:6161616161616161616161616161616100 (String: aaaaaaaaaaaaaaaa)
	 -START TLV-
	  TYPE:-1 (null)
	  LENGTH:0
	  BODY: (String: )
	  RAW: 0103F4F26DA4F8DE08002738F48B00660000000000430000000056DE000000000200000661646D696E00020200116161616161616161616161616161616100FFFF0000

